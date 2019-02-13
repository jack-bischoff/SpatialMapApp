package cmsc420.meeshquest.part1;

import cmsc420.xml.XmlUtility;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;

import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.TreeMap;

public class CitiesLookup {
    private TreeMap<String, City> mapByName;
    private TreeMap<Point2D.Float, City> mapByCoords;

    CitiesLookup() {
        this.mapByName = new TreeMap<>();
        this.mapByCoords = new TreeMap<>(new CoordSort());
    }

    public Result createCity(String name, int x, int y, int radius, String color) {
        Point2D.Float coord = new Point2D.Float(x, y);

        if (mapByCoords.containsKey(coord)) return new Result(null, "duplicateCityCoordinates");
        if (mapByName.containsKey(name)) return new Result(null, "duplicateCityName");
        //success
        City newCity = new City(name, x, y, radius, color);
        mapByCoords.put(coord, newCity);
        mapByName.put(name, newCity);

        return new Result();
    }


    public Result listCities(String sortBy) {
        if (this.mapByName.size() < 1) return new Result(null, "noCitiesToList");
        ArrayList<City> citiesList = new ArrayList<>();
        if (sortBy.equals("name")) {
           for ( String name : mapByName.descendingKeySet() ) {
               citiesList.add(this.mapByName.get(name));
           }
        } else if (sortBy.equals("coordinate")) {
            for (Point2D.Float coord : this.mapByCoords.keySet()) {
                citiesList.add(this.mapByCoords.get(coord));
            }
        }
        return new Result(citiesList, null);
    }

    public Result clearAll() {
        this.mapByName = new TreeMap<>();
        this.mapByCoords = new TreeMap<>(new CoordSort());
        return new Result();
    }
}


