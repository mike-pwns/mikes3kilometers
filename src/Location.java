import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.io.File;
import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Objects;

public class Location {

    int locationIndex;
    Angle[] angles; // list of all pics



    private static final String LOCATIONS_DATA_DIR_FILEPATH = "res/locations/";

//    public Location(int locationIndex, Angle[] angles) {
//        this.locationIndex = locationIndex;
//        this.angles = angles;
//    }

    public static Location[] loadLocations() {

        // big idea: just read all the files
        // how many locations are there? just ask the directory!

        try {

            // init gson
            Gson gson = new Gson();

            // get number of locations by the amount of files in the locations dir (yea not foolproof, but sue me this is a calm luh project)
             int numLocations = Objects.requireNonNull(new File(LOCATIONS_DATA_DIR_FILEPATH).list()).length;

            Location[] locations = new Location[numLocations];

            for (int location_index = 0; location_index < numLocations; location_index++) {
                // read each file as "location_" + index + ".json"

                // read as Location objects.
                String path = LOCATIONS_DATA_DIR_FILEPATH + "location_" + location_index + ".json";
                Location location = gson.fromJson(Files.readString(Path.of(path)), Location.class);
//                System.out.println(json.toString());

                locations[location_index] = location;

                System.out.println(location.toString());
            }

            return locations;


        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;

    }

    @Override
    public String toString() {
        return "Location " + locationIndex + "\n" + Arrays.toString(angles);
    }

    public Angle[] getAngles() {
        return angles;
    }

    public Angle getAngle(int angleIndex) {
        return angles[angleIndex];
    }

}
