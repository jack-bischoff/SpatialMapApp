package cmsc420.meeshquest.part1.Structures.Spatial;

import cmsc420.meeshquest.part1.DataObject.City;
import org.w3c.dom.Element;

import java.awt.geom.Point2D;

public class Internal extends Node {

    Internal(int xBound, int yBound, int size) {
       this.quads = new Node[]{
               EmptyLeaf.EmptyLeaf(),
               EmptyLeaf.EmptyLeaf(),
               EmptyLeaf.EmptyLeaf(),
               EmptyLeaf.EmptyLeaf()
       };

       this.xBound = xBound;
       this.yBound = yBound;
       this.sizeOfQuad = size;
    }


    public double dist(Point2D.Float point) {
        return point.distance(new Point2D.Double(xBound, yBound));
    }

    boolean contains(City city) {
        return quads[findQuad(city)].contains(city);
    }

    int findQuad(City city) {
        return findQuad((int)city.getX(), (int)city.getY());
    }
    private int findQuad(int x, int y) {
        if (y <= yBound) {
            if (x <= xBound) return 3;
            else return 2;
        } else {
            if (x <= xBound) return 0;
            else return 1;
        }
    }

    public Element toXml() {
        Element treeNode = getBuilder().createElement("gray");
        treeNode.setAttribute("x", Integer.toString(this.xBound));
        treeNode.setAttribute("y", Integer.toString(this.yBound));
        for (Node child : this.quads) {
            treeNode.appendChild(child.toXml());
        }
        return treeNode;
    }
}
