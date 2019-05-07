package cmsc420.meeshquest.part2.Structures;

import cmsc420.meeshquest.part2.DataObject.City;
import cmsc420.meeshquest.part2.DataObject.Response;
import cmsc420.meeshquest.part2.DataObject.Road;
import cmsc420.meeshquest.part2.Fault;
import cmsc420.meeshquest.part2.Structures.AdjacencyList.AdjacencyList;
import cmsc420.meeshquest.part2.Structures.Spatial.PMQuadtree.PM3Quad;
import cmsc420.meeshquest.part2.VisualMap;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class CitySpatialMap {
    private PM3Quad spatialMap;
    private AdjacencyList graph = new AdjacencyList();

    public CitySpatialMap(int width, int height) {
        this.spatialMap = new PM3Quad(width, height);
    }

    public boolean contains(City city) {
        return graph.containsKey(city);
    }
    public boolean contains(Road road) { return graph.hasEdge(road); }
    public void clearAll() {
        this.spatialMap.clear();
    }

    public Response mapCity(City city){
        if (graph.containsKey(city))
            return new Response(true, Fault.cityAlreadyMapped);
        if (!spatialMap.inRange(city.getLocation()))
            return new Response(true, Fault.cityOutOfBounds);

        city.setIsolated(true);
        graph.add(city);
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

    public Response mapRoad(Road road) {
        if (graph.hasEdge(road))
            return new Response(true, Fault.roadAlreadyMapped);
        if (!spatialMap.inRange(road))
            return new Response(true, Fault.roadOutOfBounds);

        if (!graph.containsKey(road.getStart()))
            spatialMap.mapCity(road.getStart());
        if (!graph.containsKey(road.getEnd()))
            spatialMap.mapCity(road.getEnd());

        graph.addEdge(road);
        spatialMap.mapRoad(road);

        return new Response(road);
    }

    public Response print() {
        if (spatialMap.isEmpty())
            return new Response(true, Fault.mapIsEmpty);

        return new Response(spatialMap.toXml());
    }


    public Response nearestCity (Point2D.Float nearestTo) {
        if (spatialMap.isEmpty())
            return new Response(true, Fault.cityNotFound);

        City c = spatialMap.nearestCity(nearestTo);
        if (c == null)
            return new Response(true, Fault.undefined);

        return new Response(c);
    }

    public Response rangeCities (int x, int y, int radius) {
        List<City> citiesInRange = spatialMap.rangeCities(new Point2D.Float(x, y), radius);
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

    public Response rangeRoads(int x, int y, int radius) {
        List<Road> roadsInRange = spatialMap.rangeRoads(new Point2D.Float(x, y), radius);
        if (roadsInRange.isEmpty())
            return new Response(true, Fault.noRoadsExistInRange);
        roadsInRange.sort(new Comparator<Road>() {
            public int compare(Road o1, Road o2) { return o1.compareTo(o2); }
        });
        return new Response(roadsInRange);
    }

}
