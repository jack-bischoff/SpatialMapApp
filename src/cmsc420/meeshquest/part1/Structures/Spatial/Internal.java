package cmsc420.meeshquest.part1.Structures.Spatial;

import cmsc420.meeshquest.part1.DataObject.City;
import cmsc420.meeshquest.part1.VisualMap;
import org.w3c.dom.Element;

import java.awt.geom.Point2D;

public class Internal extends Node {

    Internal(int xBound, int yBound, int width, int height) {
        this.quads = new Node[]{
               EmptyLeaf.EmptyLeaf(),
               EmptyLeaf.EmptyLeaf(),
               EmptyLeaf.EmptyLeaf(),
               EmptyLeaf.EmptyLeaf()
       };

        this.xBound = xBound;
        this.yBound = yBound;
        this.width = width;
        this.height = height;
        VisualMap.VisualMap().addCross(xBound, yBound, width, height);
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
            if (x <= xBound) return 2;
            else return 3;
        } else {
            if (x <= xBound) return 0;
            else return 1;
        }
    }

    int[] calcNextMiddle(int intoThisQuad) {
        int[] nextMiddle = null;
        int x = xBound, y = yBound, dsx = width / 4, dsy = height / 4;

        if (intoThisQuad == 0)       nextMiddle = new int[]{x - dsx, y + dsy}; //NW
        else if (intoThisQuad == 1)  nextMiddle = new int[]{x + dsx, y + dsy}; //NE
        else if (intoThisQuad == 2)  nextMiddle = new int[]{x - dsx, y - dsy}; //SW
        else if (intoThisQuad == 3)  nextMiddle = new int[]{x + dsx, y - dsy}; //SE

        return nextMiddle;
    }

    Node delete(City city) {
        boolean collapse = true;
        Leaf leaf = null;

        int nextQuad = findQuad(city);
        quads[nextQuad] = quads[nextQuad].delete(city);
        for (Node child : quads) {
            if (child instanceof Internal) return this;
            else if(!(child instanceof EmptyLeaf)) {
                if (leaf == null) leaf = (Leaf)child;
                else return this;
            }
        }
        return leaf;
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
