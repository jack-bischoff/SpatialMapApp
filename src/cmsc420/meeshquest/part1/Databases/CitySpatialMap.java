package cmsc420.meeshquest.part1.Databases;

import cmsc420.meeshquest.part1.DataObject.City;
import cmsc420.meeshquest.part1.DataObject.Response;
import cmsc420.meeshquest.part1.Databases.Spatial.prQuadTree;

import java.awt.geom.Point2D;

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

}
