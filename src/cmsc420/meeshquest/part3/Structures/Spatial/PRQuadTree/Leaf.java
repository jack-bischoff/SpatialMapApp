package cmsc420.meeshquest.part3.Structures.Spatial.PRQuadTree;

import cmsc420.meeshquest.part3.DataObject.City;
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

    Node delete(City city) {
        if (city.getName().equals(this.city.getName())) return EmptyLeaf.EmptyLeaf();
        return this;
    }

    int[] calcNextMiddle(int nextQuad) {
        return new int[0];
    }

    public Element toXml() {
        Element cityNode = getBuilder().createElement("black");
        cityNode.setAttribute("name", city.getName());
        cityNode.setAttribute("x", Integer.toString((int)city.getX()));
        cityNode.setAttribute("y", Integer.toString((int)city.getY()));
        return cityNode;
    }
}