package co.inc.board.domain.entities;

/**
 * Created by julian on 8/14/15.
 */
public class MapCoordinate {

    private final double latitude;
    private final double longitude;

    public MapCoordinate(double latitude, double longitude) {
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }
}
