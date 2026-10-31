
import com.google.gson.Gson;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;

public class Location {

    int locationIndex;
    Angle[] angles;

    private static final int LOCATION_COUNT = 17;
    private static final String LOCATIONS_DATA_DIR = "/res/locations/";

    public static Location[] loadLocations() {

        try {
            Gson gson = new Gson();
            Location[] locations = new Location[LOCATION_COUNT];

            for (int i = 0; i < LOCATION_COUNT; i++) {

                String resourcePath =
                        LOCATIONS_DATA_DIR + "location_" + i + ".json";

                try (InputStream stream =
                             Location.class.getResourceAsStream(resourcePath)) {

                    if (stream == null) {
                        throw new IllegalStateException(
                                "Missing resource: " + resourcePath
                        );
                    }

                    try (Reader reader = new InputStreamReader(
                            stream, StandardCharsets.UTF_8)) {

                        locations[i] = gson.fromJson(reader, Location.class);
                    }
                }
            }

            return locations;

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public String toString() {
        return "Location " + locationIndex;
    }

    public Angle[] getAngles() {
        return angles;
    }

    public int getLocationIndex() {
        return locationIndex;
    }

    public Angle getAngle(int angleIndex) {
        if (angles == null || angleIndex < 0 || angleIndex >= angles.length) {
            return null;
        }

        return angles[angleIndex];
    }
}
