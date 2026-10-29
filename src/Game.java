public class Game {

    GameUI ui;
    AudioPlayer musicPlayer, specialMusicPlayer;
    AudioPlayer soundEffectPlayer; Thread soundEffectThread;
    Location[] locations;
    Location workingLocation;
    Angle workingAngle;
    int[] lastAngleIndex; // bandaid: for going back
    boolean inGame = false; // false while on the main menu
    // audio https://www.youtube.com/watch?v=tmlZeYnfw7g

    public Game() {
        locations = Location.loadLocations();

        if (locations == null) {
            System.out.println("locations null");
        }
        if (locations[0] == null) {
            System.out.println("loc 0 is null dude!");
        }

        musicPlayer = new AudioPlayer();
        specialMusicPlayer = new AudioPlayer();
        soundEffectPlayer = new AudioPlayer();
        soundEffectPlayer.setLoop(false);
        soundEffectThread = new Thread(() -> {

        });
        lastAngleIndex = new int[locations.length];
        ui = new GameUI(this);
    }

    public void boot() {
        ui.setVisible(true);
        musicPlayer.playAudio("horror_ambience.wav");
        System.out.println("game booted");
    }

    public void test(int locationIndex, int angleIndex) {
        boot();
        inGame = true;
        workingLocation = locations[locationIndex];
        goToAngle(angleIndex);
    }

    // start game
    public void start() {
        inGame = true;
        workingLocation = locations[0];
        goToAngle(0);
//        soundEffectThread.start();

    }

    // switches to an angle of the current location and redraws the screen
    public void goToAngle(int angleIndex) {
        workingAngle = workingLocation.getAngle(angleIndex);
        lastAngleIndex[workingLocation.getLocationIndex()] = angleIndex;

        GameUI.BackgroundPanel backgroundPanel;
        if (workingLocation.getLocationIndex() == 12 && workingAngle.getAngleIndex() == 1) { // another bandaid solution for sum bs
            backgroundPanel = new GameUI.BackgroundPanel("res/images/location_" + workingLocation.getLocationIndex() + "/angle" + workingAngle.getAngleIndex() + ".gif");

            // play hood irony music
            musicPlayer.pauseAudio();
            specialMusicPlayer.playAudio("hoodirony.wav");
        }
        else if (workingLocation.getLocationIndex() == 12 && workingAngle.getAngleIndex() == 0) { // another bandaid solution for sum bs
            backgroundPanel = new GameUI.BackgroundPanel("res/images/location_" + workingLocation.getLocationIndex() + "/angle" + workingAngle.getAngleIndex() + ".jpg");
            // resume the serious music
            specialMusicPlayer.stopAudio();
            musicPlayer.resumeAudio();


        }
        else {
            backgroundPanel = new GameUI.BackgroundPanel("res/images/location_" + workingLocation.getLocationIndex() + "/angle" + workingAngle.getAngleIndex() + ".jpg");
        }

        ui.setGameScreen(
                backgroundPanel,
                workingAngle.getClickables(),
                Angle.buildAngleString(workingLocation, workingAngle, workingLocation.getAngles()));
    }

    // back to the main menu. progress resets, same as a fresh START
    public void showMainMenu() {
        inGame = false;
        workingAngle = null;
        lastAngleIndex = new int[locations.length];

        specialMusicPlayer.stopAudio();
        musicPlayer.resumeAudio();

        ui.setupMainMenu();
    }


    public AudioPlayer getMusicPlayer() {
        return musicPlayer;
    }


    public void toggleMute() {
        boolean muted = musicPlayer.toggleMuted();
        soundEffectPlayer.toggleMuted(); // both r mutted but well just track the music one
        ui.refreshMuteButton(muted); // only the button text changes, no screen rebuild
    }

    public void goToLocation(int locationIndex) {
        workingLocation = locations[locationIndex];
        goToAngle(lastAngleIndex[locationIndex]);
    }

    public void mikesSpecialDialogMethod() {
        musicPlayer.pauseAudio();
        specialMusicPlayer.playAudio("");
    }

}