package cmsc420.meeshquest.part1.Structures.Spatial;

import cmsc420.meeshquest.part1.DataObject.City;
import org.w3c.dom.Element;
import java.awt.geom.Point2D;

class Leaf extends Node {
    City city;

    Leaf(City city) {
        this.city = city;
    }

    double dist(Point2D.Float point) {
        return point.distance(city.getLocation());
    }

    int findQuad(City city) { return -1; }

    boolean contains(City city) {
        return city.getName().equals(this.city.getName());
    }

    public Element toXml() {
        Element cityNode = getBuilder().createElement("black");
        cityNode.setAttribute("name", city.getName());
        cityNode.setAttribute("x", Double.toString(city.getX()));
        cityNode.setAttribute("y", Double.toString(city.getY()));
        return cityNode;
    }
}