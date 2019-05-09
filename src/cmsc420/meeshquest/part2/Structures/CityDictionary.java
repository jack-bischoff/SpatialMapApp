package cmsc420.meeshquest.part2.Structures;

import cmsc420.meeshquest.part2.DataObject.City;
import cmsc420.meeshquest.part2.Comparators.CoordinateComparator;
import cmsc420.meeshquest.part2.DataObject.Response;
import cmsc420.meeshquest.part2.Fault;
import cmsc420.sortedmap.Treap;
import org.w3c.dom.Element;

import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.Map;

public class CityDictionary {
//    private TreeMap<String, City> mapByName;
//    private TreeMap<Point2D.Float, City> mapByCoords;
    private Treap<String, City> mapByName;
    private Treap<Point2D.Float, City> mapByCoords;
    public CityDictionary() {
        this.mapByName = new Treap<>();
        this.mapByCoords = new Treap<>(new CoordinateComparator());
    }

    public int size() {
        return mapByName.size();
    }

    public boolean isEmpty() {
        return mapByName.isEmpty();
    }

    public City get(String name) {
        return mapByName.get(name);
    }
    public City get(Point2D coord) { return mapByCoords.get(coord); }

    public boolean contains(String name) { return get(name) != null; }

    public Response create(String name, int x, int y, int radius, String color) {
        Point2D.Float coord = new Point2D.Float(x, y);

        if (mapByCoords.containsKey(coord))
            return new Response(true, Fault.duplicateCityCoordinates);
        if (mapByName.containsKey(name))
            return new Response(true, Fault.duplicateCityName);
        //success
        City newCity = new City(name, x, y, radius, color);
        mapByCoords.put(coord, newCity);
        mapByName.put(name, newCity);
        return new Response();
    }

    public Response delete(String name) {
        if (!mapByName.containsKey(name))
            return new Response(true, Fault.cityDoesNotExist);

        mapByCoords.remove(mapByName.get(name).getLocation());
        mapByName.remove(name);
        return new Response();
    }


    public Response list(String sortBy) {
        if (this.mapByName.size() < 1) return new Response(true, Fault.noCitiesToList);
        ArrayList<City> citiesList = new ArrayList<>();
        if (sortBy.equals("name")) {
            Map.Entry<String, City> entries[] = mapByName.toArray();
            for (int i = entries.length-1; i >= 0; i--) {
                citiesList.add((entries[i].getValue()));
            }
        } else if (sortBy.equals("coordinate")) {
            for (Map.Entry<Point2D.Float, City> entry : this.mapByCoords.entrySet()) {
                citiesList.add(entry.getValue());
            }
        }
        return new Response(citiesList);
    }

    public void clearAll() {
        this.mapByName = new Treap<>();
        this.mapByCoords = new Treap<>(new CoordinateComparator());
    }

    public Element print() {
        return mapByName.toXml();
    }
}


