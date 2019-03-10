package cmsc420.meeshquest.part1.Structures;

import cmsc420.meeshquest.part1.DataObject.City;
import cmsc420.meeshquest.part1.DataObject.Response;
import cmsc420.meeshquest.part1.Structures.Spatial.prQuadTree;
import cmsc420.meeshquest.part1.VisualMap;

import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.Comparator;

public class CitySpatialMap {
    private prQuadTree spatialMap;

    public CitySpatialMap(int width, int height) {

        this.spatialMap = new prQuadTree(width, height);
    }

    public boolean contains(City city) {
        return spatialMap.contains(city);
    }

    public void clearAll() {
        this.spatialMap = this.spatialMap.clear();
    }

    public Response mapCity(City city){
        if (spatialMap.contains(city))
            return new Response("error", "cityAlreadyMapped");
        if (spatialMap.isOutOfBounds(city))
            return new Response("error", "cityOutOfBounds");

        this.spatialMap.insert(city);
        VisualMap.VisualMap().addPoint(city.getName(), city.getX(), city.getY());
        return new Response("success", null);
    }

    public Response unmapCity(City city) {
        if (!this.spatialMap.contains(city))
            return new Response("error", "cityNotMapped");

        this.spatialMap.delete(city);
        VisualMap.VisualMap().removePoint(city.getName(), city.getX(), city.getY());
        return new Response("success", null);
    }

    public Response printPRQuadTree() {
        if (spatialMap.isEmpty())
            return new Response("error", "mapIsEmpty");

        return new Response("success", spatialMap.toXml());
    }


    public Response nearestCity (Point2D.Float nearestTo) {
        if (spatialMap.isEmpty())
            return new Response("error", "mapIsEmpty");

        City c = spatialMap.nearest(nearestTo);
        if (c == null)
            return new Response("error", "Undefined error:nullNearest");

        return new Response("success", c);
    }

    public Response rangeCities (int x, int y, int radius) {
        ArrayList<City> citiesInRange = spatialMap.range(new Point2D.Float(x, y), radius);
        if (citiesInRange.isEmpty())
            return  new Response("error", "noCitiesExistInRange");
        citiesInRange.sort(new Comparator<City>() {
            //reverse ordering
            public int compare(City o1, City o2) {
                return -1 * (o1.getName().compareTo(o2.getName()));
            }
        });
        return new Response("success", citiesInRange);
    }

}
