public class Game {

    GameUI ui;
    Monster monster; // operates on a thread system
    Location[] locations;
    // audio https://www.youtube.com/watch?v=tmlZeYnfw7g

    final String JSON_FILE = "json.json";

    public Game() {
        locations = Location.loadLocations(JSON_FILE);
        ui = new GameUI();
    }

    public void start() {

        // show ui
        // set main screen
        ui.setVisible(true);


    }
}
