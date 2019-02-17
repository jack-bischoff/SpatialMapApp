package cmsc420.meeshquest.part1.Databases;

import cmsc420.meeshquest.part1.DataObject.City;
import cmsc420.meeshquest.part1.DataObject.Response;

public class CitySpatialMap {
    private prQuadTree spatialMap;

    public CitySpatialMap() {
        this.spatialMap = new prQuadTree();
    }

    public Response mapCity(City city){

    }

    public Response unmapCity(String name) {
        City unmappedCity = prQuadTree.pop(name);
        return new Response("success", unmappedCity);
    }
    public Response print(){}
    public Response save(){}
    public Response range(){}
    public Response nearest(){}
}
