package cmsc420.meeshquest.part2.Structures;

import cmsc420.meeshquest.part2.DataObject.City;
import cmsc420.meeshquest.part2.DataObject.Response;
import cmsc420.meeshquest.part2.DataObject.Road;
import cmsc420.meeshquest.part2.Fault;
import cmsc420.meeshquest.part2.Structures.Spatial.PMQuadtree.PM3Quad;
import cmsc420.meeshquest.part2.Structures.Spatial.prQuadTree;
import cmsc420.meeshquest.part2.VisualMap;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.Comparator;

public class CitySpatialMap {
    private PM3Quad spatialMap;

    public CitySpatialMap(int width, int height) {
        this.spatialMap = new PM3Quad(width, height);
    }

    public boolean contains(City city) {
        return spatialMap.contains(city);
    }
    public boolean contains(Road road) { return spatialMap.contains(road); }
    public void clearAll() {
        this.spatialMap.clear();
    }

    public Response mapCity(City city){
        if (spatialMap.contains(city))
            return new Response(true, Fault.cityAlreadyMapped);
        if (spatialMap.inRange(city.getLocation()))
            return new Response(true, Fault.cityOutOfBounds);

        this.spatialMap.mapCity(city);
        VisualMap.VisualMap().addPoint(city.getName(), city.getX(), city.getY());
        return new Response();
    }

    public Response unmapCity(City city) {
        throw new NotImplementedException();
//        if (!this.spatialMap.contains(city))
//            return new Response(true, Fault.cityNotMapped);
//
//        this.spatialMap.unmapCity(city);
//        VisualMap.VisualMap().removePoint(city.getName(), city.getX(), city.getY());
//        return new Response();
    }

    public Response printPRQuadTree() {
        if (spatialMap.isEmpty())
            return new Response(true, Fault.mapIsEmpty);

        return new Response(spatialMap.toXml());
    }


    public Response nearestCity (Point2D.Float nearestTo) {
        if (spatialMap.isEmpty())
            return new Response(true, Fault.mapIsEmpty);

        City c = spatialMap.nearestCity(nearestTo);
        if (c == null)
            return new Response(true, Fault.undefined);

        return new Response(c);
    }

    public Response rangeCities (int x, int y, int radius) {
        ArrayList<City> citiesInRange = spatialMap.rangeCities(new Point2D.Float(x, y), radius);
        if (citiesInRange.isEmpty())
            return  new Response(true, Fault.noCitiesExistInRange);
        citiesInRange.sort(new Comparator<City>() {
            //reverse ordering
            public int compare(City o1, City o2) {
                return -1 * (o1.getName().compareTo(o2.getName()));
            }
        });
        return new Response(citiesInRange);
    }

}
