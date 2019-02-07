package cmsc420.meeshquest.part1;

import java.awt.geom.Point2D;
import java.util.TreeMap;

public class Cities {
    private TreeMap<String, City> mapByName;
    private TreeMap<Point2D.Float, City> mapByCoords;

    Cities() {
        this.mapByName = new TreeMap();
        this.mapByCoords = new TreeMap(CoordSort.compare);
    }

    public createCity(String name, int x, int y, int radius, String color) {
        Point2D.Float coord = new Point2D.Float(x, y);
        if (mapByName.containsKey(name)) {
            //fail
        }
        else if (mapByCoords.containsKey(coord)) {
            //fail
        } else{
            City newCity = new City(name, x, y, radius, color);
            mapByCoords.put(coord, newCity);
            mapByName.put(name, newCity);
            //success
        }
    }
}


