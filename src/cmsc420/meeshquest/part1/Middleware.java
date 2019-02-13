package cmsc420.meeshquest.part1;
import cmsc420.xml.XmlUtility;
import org.w3c.dom.*;

import java.util.ArrayList;
import java.util.HashMap;

//Middleware translate parsed xml into java-tized data and handles the plumbing between the I/O and internal data structures.
//TODO: Extend Result object to handle output, naming...
//TODO: Consider refactoring params into Parameter Object with paramsOrdering field.
public class Middleware {
    private Document builder;
    private CitiesLookup cities;
    Middleware(Document builder) {
        this.builder = builder;
        this.cities = new CitiesLookup();
    }

    private Element buildXml(Result res, String command, Parameters params, Element Output) {
        Element Status, Command, Params;

        Command = builder.createElement("command");
        Command.setAttribute("name", command);
        Params = params.toXml(this.builder);
        if (res.err != null) {
            Status = builder.createElement("error");
            Status.setAttribute("type", res.err);
        } else {
            Status = builder.createElement("success");
        }
        Status.appendChild(Command);
        Status.appendChild(Params);
        if (res.err == null) Status.appendChild(Output);
        return Status;
    }


    public Element createCity(Parameters params) {
        String
                name = params.get("name"),
                color = params.get("color");
        int
                x = Integer.parseInt(params.get("x")),
                y = Integer.parseInt(params.get("y")),
                radius = Integer.parseInt(params.get("radius"));

        Result res = cities.createCity(name, x, y, radius, color);
        return buildXml(res, "createCity", params, builder.createElement("output"));
    }


    public Element listCities(Parameters params) {
        String sortBy = params.get("sortBy");
        Result res = cities.listCities(sortBy);
        Element Output = builder.createElement("output");
        Element CityList = builder.createElement("cityList");
        if (res.payload != null) {

            for (City city : res.payload) {
                Element nextCity = builder.createElement("city");
                nextCity.setAttribute("name", city.getName());
                nextCity.setAttribute("x", Integer.toString((int) city.getX()));
                nextCity.setAttribute("y", Integer.toString((int) city.getY()));
                nextCity.setAttribute("radius", Integer.toString(city.getRadius()));
                nextCity.setAttribute("color", city.getColor());
                CityList.appendChild(nextCity);
            }
        }
        Output.appendChild(CityList);
        return buildXml(res, "listCities", params, Output);
    }

    public Element clearAll(Parameters params) {
        Result res = cities.clearAll();
        return buildXml(res, "clearAll", params, builder.createElement("output"));
    }
}
