package cmsc420.meeshquest.part1.Databases.Spatial;

import cmsc420.meeshquest.part1.DataObject.City;

import cmsc420.meeshquest.part1.Xmlable;
import org.w3c.dom.Element;

import java.awt.geom.Point2D;

public class Leaf extends prQuadTree implements Xmlable {
    private City city;
    Point2D.Float nextMiddle;
    int sizeFromMiddle;

    Leaf(City city, Point2D.Float nextMiddle, int sizeFromMiddle) {
        this.nextMiddle = nextMiddle;
        this.sizeFromMiddle = sizeFromMiddle;
        this.city = city;
    }

    public prQuadTree insert(City newCity) {
        prQuadTree newQuad = new prQuadTree(nextMiddle, sizeFromMiddle);
        newQuad = newQuad.insert(this.city);
        return newQuad.insert(newCity);
    }

    public EmptyLeaf delete(City toDelete) {
        return new EmptyLeaf(nextMiddle, sizeFromMiddle);
    }

    public boolean contains(City city) {
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