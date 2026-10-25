public class Game {

    GameUI ui;
    Monster monster; // operates on a thread system
    Thread soundEffectThread;
    Location[] locations;
    Location working_location;
    // audio https://www.youtube.com/watch?v=tmlZeYnfw7g



    final String HEAD_FILE = "json.json";

    public Game() {
        locations = Location.loadLocations(HEAD_FILE);
        ui = new GameUI(this::start);
        soundEffectThread = new Thread(() -> {});
    }

    public void boot() {
        ui.setVisible(true);
    }

    // start game
    public void start() {

        // set main screen
//        soundEffectThread.start();
//        working_location = locations[0];

    }

    private void loadWorkingLocation() {

    }
}
