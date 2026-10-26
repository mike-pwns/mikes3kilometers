import java.util.Arrays;

// angle is just like any screen pretty much
public class Angle {

    // -- FIELDS

    String imgFilepath; // got its background
    Clickable[] clickables; // and da clickables
    int[][] monsterSpawnCoords; // cant forget about the monster spawn coords (IF he or it spawns O.O)

    // -- METHODS

    // Just for debugging yknow
    @Override
    public String toString() {
        return "  Angle " + imgFilepath
                + "\n    clickables: " + Arrays.toString(clickables)
                + "\n    monsterSpawnCoords: " + Arrays.deepToString(monsterSpawnCoords) + "\n";
    }
}
