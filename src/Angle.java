import java.util.Arrays;

public class Angle {
    String imgFilepath;
    Clickable[] clickables;
    int[][] monsterSpawnCoords;

    @Override
    public String toString() {
        return "  Angle " + imgFilepath
                + "\n    clickables: " + Arrays.toString(clickables)
                + "\n    monsterSpawnCoords: " + Arrays.deepToString(monsterSpawnCoords) + "\n";
    }
}
