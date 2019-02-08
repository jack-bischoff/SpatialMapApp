package cmsc420.meeshquest.part1;

import org.w3c.dom.NamedNodeMap;

import java.awt.geom.Point2D;
import java.util.TreeMap;

public class CitiesLookup {
    private TreeMap<String, City> mapByName;
    private TreeMap<Point2D.Float, City> mapByCoords;

    CitiesLookup() {
        this.mapByName = new TreeMap();
        this.mapByCoords = new TreeMap(CoordSort.compare);
    }

    public int createCity(String name, int x, int y, int radius, String color) {
        Point2D.Float coord = new Point2D.Float(x, y);
        if (mapByName.containsKey(name)) {
            //fail
        }
        else if (mapByCoords.containsKey(coord)) {
            //fail
        } else{
            City newCity = new City(name, x, y, radius, color);
            String a = newCity.;
            mapByCoords.put(coord, newCity);
            mapByName.put(name, newCity);
            //success
        }
        return 1;
    }

    public int createCity(NamedNodeMap attrs) {
//        return createCity(
//                attrs.getNamedItem("name"),
//                attrs.getNamedItem("x"),
//                attrs.getNamedItem("y"),
//                attrs.getNamedItem("radius"),
//                attrs.getNamedItem("color")
//                )
    }

    public listCities(String sortBy) {
        if (this.mapByName.size() < 1) return noCitiesToList;
        if (sortBy.equals("name")) {
        } else if (sortBy.equals("coordinate")) {
            for (City city : mapByCoords.entrySet()) {
                city.toXML();
            }
        }
    }

    public void clearAll() {

    }
}


