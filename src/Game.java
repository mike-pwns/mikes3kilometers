
public class Game {

    GameUI ui;
    boolean inGame = false;

    AudioPlayer musicPlayer;
    AudioPlayer specialMusicPlayer;
    AudioPlayer soundEffectPlayer;

    final double SOUND_EFFECT_PROBABILITY = 1;
    final double SECONDS_BETWEEN_SOUND_EFFECTS = 5;

    private Thread soundEffectThread;
    private volatile boolean soundEffectThreadRunning = false;

    Location[] locations;
    Location workingLocation;
    Angle workingAngle;

    int[] lastAngleIndex;

    private boolean wasInSpecialScene = false;

    private static final String AMBIENCE_TRACK = "horror_ambience.wav";
    private static final String CAREFREE = "carefree.wav";

    private static final String HOODIRONY = "hoodirony.wav";

    private static final String CLICK_SOUND = "typewriter.wav";

    private static final String[] SOUND_EFFECTS =
            AudioPlayer.loadSoundEffects();

    private static final int SPECIAL_LOCATION_INDEX = 12;
    private static final int SPECIAL_ANGLE_INDEX = 1;


    public Game() {

        locations = Location.loadLocations();

        musicPlayer = new AudioPlayer();
        specialMusicPlayer = new AudioPlayer();
        soundEffectPlayer = new AudioPlayer();

        musicPlayer.setLoop(true);
        specialMusicPlayer.setLoop(true);

        soundEffectPlayer.setLoop(false);

        lastAngleIndex = new int[locations.length];

        ui = new GameUI(this);
    }


    public void boot() {

        ui.setVisible(true);

        musicPlayer.playAudio(AMBIENCE_TRACK);

        System.out.println("Game booted");
    }


    /**
     * Development/testing entry point.
     */
    public void test(int locationIndex, int angleIndex) {

        if (!isValidLocationIndex(locationIndex)) {
            System.err.println("Invalid location index: " + locationIndex);
            return;
        }

        boot();

        startAtLocation(locationIndex, angleIndex);
    }


    /**
     * Starts a fresh game at the first location.
     */
    public void start() {

        startAtLocation(0, 0);

//        startSoundEffectThread();
    }


    private void startAtLocation(int locationIndex, int angleIndex) {

        if (!isValidLocationIndex(locationIndex)) {
            System.err.println("Invalid location index: " + locationIndex);
            return;
        }

        inGame = true;

        workingLocation = locations[locationIndex];

        goToAngle(angleIndex);
    }


    /**
     * Switches to an angle of the current location.
     */
    public void goToAngle(int angleIndex) {

        if (workingLocation == null) {
            System.err.println("Cannot change angle: no active location.");
            return;
        }

        Angle nextAngle = workingLocation.getAngle(angleIndex);

        if (nextAngle == null) {
            System.err.println("Invalid angle index: " + angleIndex);
            return;
        }

        workingAngle = nextAngle;

        int locationIndex = workingLocation.getLocationIndex();
        int actualAngleIndex = workingAngle.getAngleIndex();

        if (locationIndex < 0 || locationIndex >= lastAngleIndex.length) {
            System.err.println("Invalid location index: " + locationIndex);
            return;
        }

        lastAngleIndex[locationIndex] = actualAngleIndex;

        boolean isSpecialScene =
                locationIndex == SPECIAL_LOCATION_INDEX
                        && actualAngleIndex == SPECIAL_ANGLE_INDEX;


        // Only change audio when entering or leaving the special scene.
        if (isSpecialScene && !wasInSpecialScene) {

            enterSpecialScene();

        } else if (!isSpecialScene && wasInSpecialScene) {

            leaveSpecialScene();
        }

        wasInSpecialScene = isSpecialScene;


        String extension = isSpecialScene ? ".gif" : ".jpg";

        GameUI.BackgroundPanel backgroundPanel =
                new GameUI.BackgroundPanel(
                        "res/images/location_" + locationIndex
                                + "/angle" + actualAngleIndex + extension
                );

        ui.setGameScreen(
                backgroundPanel,
                workingAngle.getClickables(),
                Angle.buildAngleString(
                        workingLocation,
                        workingAngle,
                        workingLocation.getAngles()
                )
        );
    }


    /**
     * Pauses normal ambience and starts special music.
     */
    private void enterSpecialScene() {

        musicPlayer.pauseAudio();

        specialMusicPlayer.playAudio(HOODIRONY);

//        stopSoundEffectThread();
    }


    /**
     * Stops special music and restores normal ambience.
     */
    private void leaveSpecialScene() {

        specialMusicPlayer.stopAudio();

        musicPlayer.resumeAudio();

//        startSoundEffectThread();
    }


    /**
     * Returns to the main menu and resets navigation progress.
     */
    public void showMainMenu() {

        inGame = false;

        workingAngle = null;
        workingLocation = null;

        wasInSpecialScene = false;

//        stopSoundEffectThread();

        lastAngleIndex = new int[locations.length];

        specialMusicPlayer.stopAudio();

        if (musicPlayer.isPaused()) {
            musicPlayer.resumeAudio();
        } else {
            musicPlayer.playAudio(AMBIENCE_TRACK);
        }

        ui.setupMainMenu();
    }


    public AudioPlayer getMusicPlayer() {
        return musicPlayer;
    }


    /**
     * Toggles global audio mute state.
     */
    public void toggleMute() {

        boolean muted = !musicPlayer.isMuted();

        musicPlayer.setMuted(muted);
        specialMusicPlayer.setMuted(muted);
        soundEffectPlayer.setMuted(muted);

        ui.refreshMuteButton(muted);
    }


    /**
     * Changes location and restores its previously selected angle.
     */
    public void goToLocation(int locationIndex) {

        if (!isValidLocationIndex(locationIndex)) {
            System.err.println("Invalid location index: " + locationIndex);
            return;
        }

        workingLocation = locations[locationIndex];

        int previousAngleIndex = lastAngleIndex[locationIndex];

        goToAngle(previousAngleIndex);
    }


    public void mikesSpecialDialogMethod() {

//        stopSoundEffectThread();

        if (!musicPlayer.isPaused()) {

            musicPlayer.pauseAudio();

            specialMusicPlayer.playAudio(CAREFREE);
        }
    }


    private boolean isValidLocationIndex(int locationIndex) {

        return locations != null
                && locationIndex >= 0
                && locationIndex < locations.length
                && locations[locationIndex] != null;
    }


    // =========================================================
    // SOUND EFFECT THREAD MANAGEMENT
    // =========================================================

    /**
     * Starts the sound effect thread if one isn't already running.
     */
    private synchronized void startSoundEffectThread() {

        if (!inGame) {
            return;
        }

        if (wasInSpecialScene) {
            return;
        }

        // Prevent duplicate threads.
        if (soundEffectThread != null && soundEffectThread.isAlive()) {
            return;
        }

        soundEffectThreadRunning = true;

        soundEffectThread = new Thread(() -> {

            System.out.println("Sound effect thread started.");

            try {

                while (soundEffectThreadRunning
                        && !Thread.currentThread().isInterrupted()) {

                    Thread.sleep(
                            (long) (SECONDS_BETWEEN_SOUND_EFFECTS * 1000)
                    );

                    if (!soundEffectThreadRunning || !inGame) {
                        break;
                    }

                    if (Math.random() <= SOUND_EFFECT_PROBABILITY) {

                        if (SOUND_EFFECTS == null
                                || SOUND_EFFECTS.length == 0) {
                            continue;
                        }

                        int randomIndex =
                                (int) (Math.random() * SOUND_EFFECTS.length);

                        String sound = SOUND_EFFECTS[randomIndex];

                        soundEffectPlayer.playAudio(sound);
                    }
                }

            } catch (InterruptedException e) {

                // Expected when stopping the thread.
                Thread.currentThread().interrupt();

            } finally {

                synchronized (Game.this) {

                    // Only clear the reference if this is still
                    // the currently registered thread.
                    if (soundEffectThread == Thread.currentThread()) {
                        soundEffectThread = null;
                        soundEffectThreadRunning = false;
                    }
                }

                System.out.println("Sound effect thread stopped.");
            }

        });

        soundEffectThread.setName("SoundEffectThread");

        soundEffectThread.start();
    }


    /**
     * Stops the sound effect thread safely.
     */
    private synchronized void stopSoundEffectThread() {

        soundEffectThreadRunning = false;

        if (soundEffectThread != null) {

            soundEffectThread.interrupt();

            soundEffectThread = null;
        }
    }
}