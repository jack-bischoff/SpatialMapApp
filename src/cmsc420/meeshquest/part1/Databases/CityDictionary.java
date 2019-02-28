package cmsc420.meeshquest.part1.Databases;

import cmsc420.meeshquest.part1.DataObject.City;
import cmsc420.meeshquest.part1.CoordinateComparator;
import cmsc420.meeshquest.part1.DataObject.Failure;
import cmsc420.meeshquest.part1.DataObject.Response;
import cmsc420.meeshquest.part1.DataObject.Success;

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

    public City get(String name) {
        return mapByName.get(name);
    }

    public boolean contains(String name) { return get(name) != null; }

    public Response create(String name, int x, int y, int radius, String color) {
        Point2D.Float coord = new Point2D.Float(x, y);

        if (mapByCoords.containsKey(coord))
            return new Response("error", "duplicateCityCoordinates");
        if (mapByName.containsKey(name))
            return new Response("error", "duplicateCityName");
        //success
        City newCity = new City(name, x, y, radius, color);
        mapByCoords.put(coord, newCity);
        mapByName.put(name, newCity);
        return new Response("success", null);
    }

    public Response delete(String name) {
        if (!mapByName.containsKey(name))
            return new Response("error", "cityDoesNotExist");

        mapByCoords.remove(mapByName.get(name).getLocation());
        mapByName.remove(name);
        return new Response("success", null);
    }


    public Response list(String sortBy) {
        if (this.mapByName.size() < 1) return new Response("error", "noCitiesToList");
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
        return new Response("success", citiesList);
    }

    public void clearAll() {
        this.mapByName = new TreeMap<>();
        this.mapByCoords = new TreeMap<>(new CoordinateComparator());
    }
}


