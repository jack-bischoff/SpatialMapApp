package cmsc420.meeshquest.part1.Databases;

import cmsc420.meeshquest.part1.DataObject.City;
import cmsc420.meeshquest.part1.DataObject.Response;
import cmsc420.meeshquest.part1.Databases.Spatial.prQuadTree;

public class CitySpatialMap {
    private prQuadTree spatialMap;
    private int width, height;

    public CitySpatialMap(int width, int height) {
        this.width = width;
        this.height = height;
        this.spatialMap = new prQuadTree();
    }

    public Response mapCity(City city){
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
