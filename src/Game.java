public class Game {

    GameUI ui;
    Monster monster; // operates on a thread system
    Thread gameThread, soundEffectThread;
    Location[] locations;
    Location working_location;
    // audio https://www.youtube.com/watch?v=tmlZeYnfw7g

    public Game() {
        locations = Location.loadLocations();

        ui = new GameUI(this);
//        soundEffectThread = new Thread(() -> {});
    }

    public void boot() {
        ui.setVisible(true);
        System.out.println("game booted");
    }

    // start game
    public void start() {

        // start all the threads

        //

    }

    public void toggleGameRunning() {
        // pauses all threads
    }
}
