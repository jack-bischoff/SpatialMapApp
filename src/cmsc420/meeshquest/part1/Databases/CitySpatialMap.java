package cmsc420.meeshquest.part1.Databases;

import cmsc420.meeshquest.part1.DataObject.City;
import cmsc420.meeshquest.part1.DataObject.Response;
import cmsc420.meeshquest.part1.Databases.Spatial.prQuadTree;

import java.awt.geom.Point2D;

public class CitySpatialMap {
    private prQuadTree spatialMap;
    private int width, height;

    public CitySpatialMap(int width, int height) {
        this.width = width;
        this.height = height;
        this.spatialMap = new prQuadTree(new Point2D.Float(0,0), new Point2D.Float(width, height));
    }

    public Response mapCity(City city){
        if (spatialMap.contains(city))
            return new Response("error", "cityAlreadyMapped");
        if ((int) city.getX() > width || (int) city.getY() > height)
            return new Response("error", "cityOutOfBounds");

        this.spatialMap = this.spatialMap.insert(city);
        return new Response("success", null);
    }

    public Response unmapCity(String name) {

    }
    public Response print(){}
    public Response save(){}
    public Response range(){}
    public Response nearest(){}
}
