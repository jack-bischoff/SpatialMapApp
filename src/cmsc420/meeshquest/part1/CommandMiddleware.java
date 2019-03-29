package cmsc420.meeshquest.part1;
import cmsc420.drawing.CanvasPlus;
import cmsc420.meeshquest.part1.DataObject.*;
import cmsc420.meeshquest.part1.Structures.CityDictionary;

import cmsc420.meeshquest.part1.Structures.CitySpatialMap;
import org.w3c.dom.*;

import java.awt.*;
import java.awt.geom.Point2D;
import java.io.IOException;
import java.util.ArrayList;
import java.util.function.Function;

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

    private Element cityListBuilder(Iterable<City> cities) {
        Element cityList = builder.createElement("cityList");
        for (City city : cities) {
            cityList.appendChild(city.toXml());
        }
        return cityList;
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
        if (res.error)
            return new Failure(res.payload);
        return new Success();
    }


    Result listCities(Parameters params) {
        String sortBy = params.get("sortBy");
        Response res = cityDictionary.list(sortBy);

        if (res.error)
            return new Failure(res.payload);
        return new Success(cityListBuilder((Iterable<City>)res.payload));
    }

    Result clearAll() {
        cityDictionary.clearAll();
        spatialMap.clearAll();
        return new Success();
    }

    Result deleteCity(Parameters params) {
        String name = params.get("name");
        Response res;
        Element output = null;

        if (!cityDictionary.contains(name))
            return new Failure(Fault.cityDoesNotExist);

        City toDelete = cityDictionary.get(name);
        if (spatialMap.contains(toDelete)) {
            spatialMap.unmapCity(toDelete);
            output = toDelete.toXml();
            output = (Element) builder.renameNode(output,null,"cityUnmapped");
        }

        res = cityDictionary.delete(name);
        if (res.error)
            return new Failure(res.payload);

        return new Success(output);
    }

    Result mapCity(Parameters params) {
        String name = params.get("name");

        City city = cityDictionary.get(name);
        if (city == null)
            return new Failure(Fault.nameNotInDictionary);

        Response res = spatialMap.mapCity(city);
        if (res.error)
            return new Failure(res.payload);
        return new Success();
    }

    Result unmapCity(Parameters params) {
        String name = params.get("name");

        City city = cityDictionary.get(name);
        if (city == null)
            return new Failure(Fault.nameNotInDictionary);

        Response res = spatialMap.unmapCity(city);
        if (res.error)
            return new Failure(res.payload);

        return new Success();
    }

    Result saveMap(Parameters params) throws IOException {
        return saveMap(params.get("name"));
    }

    private Result saveMap(String name) throws IOException {
        VisualMap.VisualMap().save(name);
        return new Success();
    }

    Result printPRQuadTree() {
        Response res = spatialMap.printPRQuadTree();
        if (res.error)
            return new Failure(res.payload);

        Element tree = builder.createElement("quadtree");
        tree.appendChild((Element)res.payload);
        return new Success(tree);
    }
    Result nearestCity(Parameters params) {
       Point2D.Float point = new Point2D.Float(
                       Integer.parseInt(params.get("x")),
                       Integer.parseInt(params.get("y"))
       );

        Response res = spatialMap.nearestCity(point);
        if (res.error)
            return new Failure(res.payload);
        return new Success(((City)res.payload).toXml());
    }

    Result rangeCities(Parameters params) throws IOException {
        int x = Integer.parseInt(params.get("x"));
        int y = Integer.parseInt(params.get("y"));
        int radius = Integer.parseInt(params.get("radius"));

        Response res = spatialMap.rangeCities(x, y, radius);
        if (params.get("saveMap") != null){
            VisualMap.VisualMap().addCircle(x, y, radius, Color.BLUE,false);
            saveMap(params.get("saveMap"));
        }
        if (res.error)
            return new Failure(res.payload);

        return new Success(cityListBuilder((Iterable<City>) res.payload));
    }
}
