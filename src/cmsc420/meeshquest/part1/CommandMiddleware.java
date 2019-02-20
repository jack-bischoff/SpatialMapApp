package cmsc420.meeshquest.part1;
import cmsc420.meeshquest.part1.DataObject.*;
import cmsc420.meeshquest.part1.Databases.CityDictionary;

import cmsc420.meeshquest.part1.Databases.CitySpatialMap;
import org.w3c.dom.*;

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

    public Result createCity(Parameters params)  {
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


    public Result listCities(Parameters params) {
        String sortBy = params.get("sortBy");
        Response res = cityDictionary.list(sortBy);

        if (res.status.equals("error"))
            return new Failure((String) res.payload);

        Element cityList = builder.createElement("cityList");
        City[] cities = (City[]) res.payload;
        for (City city : cities) {
            cityList.appendChild(city.toXml());
        }
        return new Success(cityList);
    }

    public Result clearAll() {
        cityDictionary.clearAll();
        return new Success();
    }

    public Result deleteCity(Parameters params) {
        String name = params.get("name");
        Response res;

        res = cityDictionary.delete(name);
        if (res.status.equals("error"))
            return new Failure((String) res.payload);
        res = spatialMap.unmapCity(name);
        if (res.payload != null) {
            Element city = ((City)res.payload).toXml();
            builder.renameNode(city,"","cityUnmapped");
            return new Success(city);
        }

        return new Success();
    }

    public Result mapCity(Parameters params) {
        String name = params.get("name");
        City city = cityDictionary.get(name);
        if (city == null)
            return new Failure("nameNotInDictionary");
        Response res = spatialMap.mapCity(city);
        if (res.status.equals("error"))
            return new Failure((String) res.payload);
        return new Success();
    }
}
