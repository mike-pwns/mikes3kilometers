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
    public void start() {

        // start all the threads
        // monster thread
        //
        workingLocation = locations[0];
        workingAngle = workingLocation.getAngle(0);


        ui.setGameScreen(new GameUI.BackgroundPanel(workingAngle.getImgFilepath()), workingAngle.getClickables());



    }

    public void toggleGameRunning() {
        // pauses all threads
    }
}
