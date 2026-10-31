
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

    private static final String AMBIENCE_TRACK =
            "/res/audio/horror_ambience.wav";

    private static final String CAREFREE =
            "/res/audio/carefree.wav";

    private static final String HOODIRONY =
            "/res/audio/hoodirony.wav";

    private static final String CLICK_SOUND =
            "/res/audio/typewriter.wav";

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

        // Ambience intentionally starts when the user unmutes.

        System.out.println("Game booted");
    }


    public void test(int locationIndex, int angleIndex) {

        if (!isValidLocationIndex(locationIndex)) {
            System.err.println("Invalid location index: " + locationIndex);
            return;
        }

        boot();

        startAtLocation(locationIndex, angleIndex);
    }


    public void start() {
        startAtLocation(0, 0);
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


    private void enterSpecialScene() {

        musicPlayer.pauseAudio();

        specialMusicPlayer.playAudio(HOODIRONY);
    }


    private void leaveSpecialScene() {

        specialMusicPlayer.stopAudio();

        if (!musicPlayer.isMuted()) {
            musicPlayer.resumeAudio();
        }
    }


    public void showMainMenu() {

        inGame = false;
        talkedToMike = false;

        workingAngle = null;
        workingLocation = null;

        wasInSpecialScene = false;

        stopSoundEffectThread();

        lastAngleIndex = new int[locations.length];

        specialMusicPlayer.stopAudio();

        if (!musicPlayer.isMuted()) {
            musicPlayer.stopAudio();
            musicPlayer.playAudio(AMBIENCE_TRACK);
        }

        ui.setupMainMenu();
    }


    public AudioPlayer getMusicPlayer() {
        return musicPlayer;
    }


    public void toggleMute() {

        boolean newMutedState = !musicPlayer.isMuted();

        musicPlayer.setMuted(newMutedState);
        specialMusicPlayer.setMuted(newMutedState);
        soundEffectPlayer.setMuted(newMutedState);

        if (!newMutedState) {

            if (wasInSpecialScene) {

                specialMusicPlayer.playAudio(HOODIRONY);

            } else if (musicPlayer.isPaused()) {

                musicPlayer.resumeAudio();

            } else {

                musicPlayer.stopAudio();
                musicPlayer.playAudio(AMBIENCE_TRACK);
            }

        } else {

            musicPlayer.stopAudio();
            specialMusicPlayer.stopAudio();
            soundEffectPlayer.stopAudio();
        }

        ui.refreshMuteButton(newMutedState);

        System.out.println("[Game] Audio muted: " + newMutedState);
    }


    public void goToLocation(int locationIndex) {

        if (!isValidLocationIndex(locationIndex)) {
            System.err.println("Invalid location index: " + locationIndex);
            return;
        }

        if (locationIndex == 16) {
            musicPlayer.pauseAudio();
        }
        else if (musicPlayer.isPaused()) {
            musicPlayer.resumeAudio();
        }

        workingLocation = locations[locationIndex];

        int previousAngleIndex = lastAngleIndex[locationIndex];

        goToAngle(previousAngleIndex);
    }


    boolean talkedToMike;

    public void mikesSpecialDialogMethod() {

        if (!talkedToMike) {
            specialMusicPlayer.playAudio(CAREFREE);
            talkedToMike = true;
        }

    }


    private boolean isValidLocationIndex(int locationIndex) {

        return locations != null
                && locationIndex >= 0
                && locationIndex < locations.length
                && locations[locationIndex] != null;
    }


    private synchronized void startSoundEffectThread() {

        if (!inGame || wasInSpecialScene) {
            return;
        }

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

                Thread.currentThread().interrupt();

            } finally {

                synchronized (Game.this) {

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


    private synchronized void stopSoundEffectThread() {

        soundEffectThreadRunning = false;

        if (soundEffectThread != null) {
            soundEffectThread.interrupt();
            soundEffectThread = null;
        }

        soundEffectPlayer.stopAudio();
    }
}
