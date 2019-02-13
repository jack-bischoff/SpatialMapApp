package cmsc420.meeshquest.part1.Databases;

import cmsc420.meeshquest.part1.DataObject.City;
import cmsc420.meeshquest.part1.CoordinateComparator;
import cmsc420.meeshquest.part1.DataObject.Result;
import cmsc420.meeshquest.part1.Errors.DuplicateCityCoordinatesFailure;
import cmsc420.meeshquest.part1.Errors.DuplicateCityNameFailure;
import cmsc420.meeshquest.part1.Errors.Failure;

import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.TreeMap;

public class CityDictionary {
    private TreeMap<String, City> mapByName;
    private TreeMap<Point2D.Float, City> mapByCoords;

    public CityDictionary() {
        this.mapByName = new TreeMap<>();
        this.mapByCoords = new TreeMap<>(new CoordinateComparator());
    }

    public void create(String name, int x, int y, int radius, String color) throws Failure {
        Point2D.Float coord = new Point2D.Float(x, y);

        if (mapByCoords.containsKey(coord)) throw new DuplicateCityCoordinatesFailure();
        if (mapByName.containsKey(name)) throw new DuplicateCityNameFailure();
        //success
        City newCity = new City(name, x, y, radius, color);
        mapByCoords.put(coord, newCity);
        mapByName.put(name, newCity);
    }

    public Result delete(String name) {
        if (this.mapByName.containsKey("name")) {
            if
        }
    }


    public Result list(String sortBy) {
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
        this.mapByCoords = new TreeMap<>(new CoordinateComparator());
        return new Result();
    }
}


