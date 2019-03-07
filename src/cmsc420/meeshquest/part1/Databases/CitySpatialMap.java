package cmsc420.meeshquest.part1.Databases;

import cmsc420.meeshquest.part1.DataObject.City;
import cmsc420.meeshquest.part1.DataObject.Response;
import cmsc420.meeshquest.part1.Databases.Spatial.Leaf;
import cmsc420.meeshquest.part1.Databases.Spatial.prQuadTree;

import java.awt.*;
import java.awt.geom.Point2D;
import java.util.Comparator;
import java.util.PriorityQueue;

public class CitySpatialMap {
    private prQuadTree spatialMap;
    private int width, height, cityCounter;

    public CitySpatialMap(int width, int height) {
        this.width = width;
        this.height = height;
        this.spatialMap = new prQuadTree(new Point2D.Float(width/2, height/2), 64);
    }

    public Response mapCity(City city){
        if (spatialMap.contains(city))
            return new Response("error", "cityAlreadyMapped");
        if ((int) city.getX() > width || (int) city.getY() > height)
            return new Response("error", "cityOutOfBounds");

        this.spatialMap = this.spatialMap.insert(city);
        this.cityCounter++;
        return new Response("success", null);
    }

    public Response unmapCity(City city) {
        if (!spatialMap.contains(city))
            return new Response("error", "cityNotMapped");
        this.spatialMap = this.spatialMap.delete(city);
        this.cityCounter--;

        return new Response("success", null);

    }

    public Response printPRQuadTree() {
        if (cityCounter == 0)
            return new Response("error", "mapIsEmpty");

        return new Response("success", spatialMap.toXml());
    }

    public boolean contains(City city) {
        return spatialMap.contains(city);
    }
    public Response nearestCity (Point2D.Float nearestTo) {
        if (this.cityCounter == 0)
            return new Response("error", "mapIsEmpty");

        City c = spatialMap.nearest(nearestTo);
        if (c == null)
            return new Response("error", "Undefined error:nullNearest");

        return new Response("success", c);
    }

    public Response rangeCities (int x, int y, int radius) {
        City[] citiesInRange = spatialMap.range(new Point2D.Float(x, y), radius);
        if (citiesInRange.length == 0)
            return  new Response("error", "noCitiesExistInRange");
        return new Response("success", citiesInRange);
    }
//    public void nearest(Point2D.Float nearestTo) {
//        class distCompare implements Comparator<prQuadTree>{
//            Point2D.Float point;
//            public distCompare(Point2D.Float point) {
//                this.point = point;
//            }
//            public int compare(prQuadTree t1, prQuadTree t2) {
//                double dist1 = t1.dist(point);
//                double dist2 = t2.dist(point);
//                if (dist1 < dist2) return -1;
//                else if (dist1 > dist2) return 1;
////                else return c1.getName().compareTo(c2.getName());
//                return 0;
//            }
//        }
//        PriorityQueue<prQuadTree> Q = new PriorityQueue<>(new distCompare(nearestTo));
//        Q.add(spatialMap);
//        while (!Q.isEmpty()) {
//            prQuadTree ele = Q.poll();
//            if (!(ele instanceof Leaf)) {
//                for (prQuadTree Quad : ele)
//            }
//        }
//    }

}
