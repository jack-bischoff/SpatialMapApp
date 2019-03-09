package cmsc420.meeshquest.part1;
import cmsc420.meeshquest.part1.DataObject.*;
import cmsc420.meeshquest.part1.Structures.CityDictionary;

import cmsc420.meeshquest.part1.Structures.CitySpatialMap;
import org.w3c.dom.*;

import java.awt.geom.Point2D;
import java.util.ArrayList;

//CommandMiddleware translate parsed xml into java-tized data and handles the plumbing between the I/O and internal data structures.
//TODO: Extend Response object to handle output, naming...
//TODO: Consider refactoring params into Parameter Object with paramsOrdering field.
public class CommandMiddleware {
    private CityDictionary cityDictionary;
    private CitySpatialMap spatialMap;
    private Document builder;

    CommandMiddleware(Document builder, int width, int height) {
        this.builder = builder;
        this.cityDictionary = new CityDictionary();
        this.spatialMap = new CitySpatialMap(width, height);
    }

    Result createCity(Parameters params)  {
        String
                name = params.get("name"),
                color = params.get("color");
        int
                x = Integer.parseInt(params.get("x")),
                y = Integer.parseInt(params.get("y")),
                radius = Integer.parseInt(params.get("radius"));

        Response res = cityDictionary.create(name, x, y, radius, color);
        if (res.status.equals("error"))
            return new Failure((String) res.payload);
        return new Success();
    }


    Result listCities(Parameters params) {
        String sortBy = params.get("sortBy");
        Response res = cityDictionary.list(sortBy);

        if (res.status.equals("error"))
            return new Failure((String) res.payload);

        Element cityList = builder.createElement("cityList");
        ArrayList<City> cities = (ArrayList<City>) res.payload;
        for (City city : cities) {
            cityList.appendChild(city.toXml());
        }
        return new Success(cityList);
    }

    Result clearAll() {
        cityDictionary.clearAll();
        return new Success();
    }

    Result deleteCity(Parameters params) {
        String name = params.get("name");
        Response res;
        Element output = null;

        if (!cityDictionary.contains(name))
            return new Failure("cityDoesNotExist");

        City toDelete = cityDictionary.get(name);
        if (spatialMap.contains(toDelete)) {
            spatialMap.unmapCity(toDelete);
            output = toDelete.toXml();
            builder.renameNode(output,"","cityUnmapped");
        }

        res = cityDictionary.delete(name);
        if (res.status.equals("error"))
            return new Failure((String) res.payload);

        return new Success(output);
    }

    Result mapCity(Parameters params) {
        String name = params.get("name");
        City city = cityDictionary.get(name);

        if (city == null)
            return new Failure("nameNotInDictionary");

        Response res = spatialMap.mapCity(city);
        if (res.status.equals("error"))
            return new Failure(res.payload.toString());
        return new Success();
    }

    Result unmapCity(Parameters params) {
        String name = params.get("name");
        City city = cityDictionary.get(name);

        if (city == null)
            return new Failure("nameNotInDictionary");

        Response res = spatialMap.unmapCity(city);
        if (res.status.equals("error"))
            return new Failure(res.payload.toString());

        return new Success();
    }

    Result saveMap(Parameters params) {
        String name = params.get("name");

        //do stuff
        return new Success();
    }
    Result printPRQuadTree() {
        Response res = spatialMap.printPRQuadTree();
        if (res.status.equals("error"))
            return new Failure(res.payload.toString());

        Element tree = builder.createElement("quadtree");
        tree.appendChild((Element)res.payload);
        return new Success(tree);
    }
    Result nearestCity(Parameters params) {
       Point2D.Float point = null; // = params.get("point");
        Response res = spatialMap.nearestCity(point);
        if (res.status.equals("error"))
            return new Failure(res.payload.toString());
        return new Success(((City)res.payload).toXml());
    }

    Result rangeCities(Parameters params) {
        int x = Integer.parseInt(params.get("x"));
        int y = Integer.parseInt(params.get("y"));
        int radius = Integer.parseInt(params.get("radius"));
        //Do something with optional saveMap
        Response res = spatialMap.rangeCities(x, y, radius);
        if (res.status.equals("error"))
            return new Failure(res.payload.toString());

        City[] citiesInRange = (City[])res.payload;
        Element cityList = builder.createElement("cityList");
        for (City city : citiesInRange) {
            cityList.appendChild(city.toXml());
        }
        return new Success(cityList);
    }
}
