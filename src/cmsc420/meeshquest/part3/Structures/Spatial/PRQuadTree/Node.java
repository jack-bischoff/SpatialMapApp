package cmsc420.meeshquest.part3.Structures.Spatial.PRQuadTree;

import cmsc420.meeshquest.part3.DataObject.City;
import cmsc420.meeshquest.part3.Xmlable;
import org.w3c.dom.Element;

import java.awt.geom.Point2D;

public abstract class Node implements Xmlable {
    Node[] quads; //Array indices correspond to quadrants in a cartesian plane.
    int xBound, yBound, width, height;

    abstract double dist(Point2D.Float point);
    public abstract Element toXml();
    abstract int findQuad(City city);
    abstract boolean contains(City city);
    abstract int[] calcNextMiddle(int nextQuad);
    abstract Node delete(City city);
}
