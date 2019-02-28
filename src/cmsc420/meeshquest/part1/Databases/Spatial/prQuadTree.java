package cmsc420.meeshquest.part1.Databases.Spatial;

import cmsc420.meeshquest.part1.DataObject.City;
import cmsc420.meeshquest.part1.Xmlable;
import org.w3c.dom.Element;
import sun.invoke.empty.Empty;

import java.awt.geom.Point2D;

public class prQuadTree implements Xmlable {
    private prQuadTree[] quads; //Array indices correspond to quadrants in a cartesian plane.
    private int xBound, yBound,size;

    prQuadTree() {}
    public prQuadTree(Point2D.Float middle, int size) {
        float x = (float) middle.getX();
        float y = (float) middle.getY();
        int ds = size / 2;

        this.size = size;
        this.xBound = (int)x;
        this.yBound = (int)y;

        //In clockwise order:
        quads = new prQuadTree[]{
                new EmptyLeaf(new Point2D.Float(x - ds, y + ds), ds), //NW
                new EmptyLeaf(new Point2D.Float(x + ds, y + ds), ds), //NE
                new EmptyLeaf(new Point2D.Float(x + ds, y - ds), ds), //SE
                new EmptyLeaf(new Point2D.Float(x - ds, y - ds), ds)  //SW
        };
    }

    public prQuadTree insert(City city) {
        int xCoord = (int) city.getX(), yCoord = (int) city.getY(), quad;
        quad = findQuad(xCoord, yCoord);
        quads[quad] = quads[quad].insert(city);
        return this;
    }

    public prQuadTree delete(City city) {
        int quad = findQuad((int)city.getX(), (int)city.getY());
        quads[quad] = quads[quad].delete(city);
        //suboptimal cleanup in all sense of the word
        if (
            quads[0] instanceof EmptyLeaf &&
            quads[1] instanceof EmptyLeaf &&
            quads[2] instanceof EmptyLeaf &&
            quads[3] instanceof EmptyLeaf
        ) {
            return new EmptyLeaf(new Point2D.Float(xBound, yBound), this.size);
        }
        return this;
    }

    public Element toXml() {
        Element treeNode = getBuilder().createElement("gray");
        treeNode.setAttribute("x", Integer.toString(this.xBound));
        treeNode.setAttribute("y", Integer.toString(this.yBound));
        for (prQuadTree child : this.quads) {
            treeNode.appendChild(child.toXml());
        }
        return treeNode;
    }

    public boolean contains(City city) {
        int quad = findQuad((int) city.getX(), (int) city.getY());
        return quads[quad].contains(city);
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
}
