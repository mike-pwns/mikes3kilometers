import java.util.Arrays;

public class Game {

    GameUI ui;
    Monster monster; // operates on a thread system
    Thread gameThread, soundEffectThread;
    Location[] locations;
    Location workingLocation;
    Angle workingAngle;
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

    // start game
    // start game
    public void start() {
        workingLocation = locations[0];
        goToAngle(0);
    }

    // switches to an angle of the current location and redraws the screen
    public void goToAngle(int angleIndex) {
        if (angleIndex < 0 || angleIndex >= workingLocation.getAngles().length) {
            System.out.println("no angle " + angleIndex + " in location " + workingLocation.locationIndex);
            return;
        }

        workingAngle = workingLocation.getAngle(angleIndex);

        ui.setGameScreen(
                new GameUI.BackgroundPanel(workingAngle.getImgFilepath()),
                workingAngle.getClickables(),
                Angle.buildAngleString(workingLocation, workingAngle, workingLocation.getAngles()));
    }

    public void toggleGameRunning() {
        // pauses all threads
    }
}
