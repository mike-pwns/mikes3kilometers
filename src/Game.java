public class Game {

    GameUI ui;
    Monster monster; // operates on a thread system
    Thread gameThread, soundEffectThread;
    Location[] locations;
    Location workingLocation;
    Angle workingAngle;
    boolean muted = false;
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

        ui = new GameUI(this);
//        soundEffectThread = new Thread(() -> {});
    }

    public void boot() {
        ui.setVisible(true);
        // TODO start music here
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
    }

    // switches to an angle of the current location and redraws the screen
    public void goToAngle(int angleIndex) {

        workingAngle = workingLocation.getAngle(angleIndex);

        ui.setGameScreen(
                new GameUI.BackgroundPanel("res/images/location_" + workingLocation.getLocationIndex() + "/angle" + workingAngle.getAngleIndex() + ".jpg"),
                workingAngle.getClickables(),
                Angle.buildAngleString(workingLocation, workingAngle, workingLocation.getAngles()));
    }

    // back to the main menu. progress resets, same as a fresh START
    public void showMainMenu() {
        inGame = false;
        workingAngle = null;
        ui.setupMainMenu();
    }

    public boolean isMuted() {
        return muted;
    }

    public void toggleMute() {
        muted = !muted;
        // TODO actually mute/unmute the audio here once sound is in
        ui.refreshMuteButton(muted); // only the button text changes, no screen rebuild
    }

    public void goToLocation(int locationIndex) {
        workingLocation = locations[locationIndex];
        goToAngle(0);
    }
}