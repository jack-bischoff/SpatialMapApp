package cmsc420.meeshquest.part1.Databases.Spatial;

import cmsc420.meeshquest.part1.DataObject.City;
import cmsc420.meeshquest.part1.Xmlable;
import org.w3c.dom.Element;

import java.awt.geom.Ellipse2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.PriorityQueue;

public class prQuadTree implements Xmlable {
    private prQuadTree[] quads; //Array indices correspond to quadrants in a cartesian plane.
    private int xBound, yBound,size;
    class distCompare implements Comparator<prQuadTree> {
        Point2D.Float point;
        public distCompare(Point2D.Float point) {
            this.point = point;
        }
        public int compare(prQuadTree t1, prQuadTree t2) {
            double dist1 = t1.dist(point);
            double dist2 = t2.dist(point);
            if (dist1 < dist2) return -1;
            else if (dist1 > dist2) return 1;
//                else return c1.getName().compareTo(c2.getName());
            return 0;
        }
    }

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

    public double dist(Point2D.Float point) {
            return point.distance(new Point2D.Double(xBound, yBound));
    }

    public City nearest(Point2D.Float nearestTo) {

        PriorityQueue<prQuadTree> Q = new PriorityQueue<>(new distCompare(nearestTo));
        Q.add(this);
        while (!Q.isEmpty()) {
            prQuadTree ele = Q.poll();
            if (!(ele instanceof Leaf)) {
                for (prQuadTree Quad : ele.quads) Q.add(Quad);
            } else if (!(ele instanceof EmptyLeaf)) {
                return ((Leaf)ele).city;
            }
        }
        return null;
    }

    public ArrayList<City> range(Point2D.Float from, int radius) {
        ArrayList<City> citiesInRange = new ArrayList<>();
        Ellipse2D.Float searchCircle =
                new Ellipse2D.Float( ((int)from.getX() + radius), ((int)from.getY() + radius), radius, radius );

        PriorityQueue<prQuadTree> Q = new PriorityQueue<>();
        Q.add(this);
        while (!Q.isEmpty()) {
            prQuadTree ele = Q.poll();
            if (!(ele instanceof Leaf)) {
                float upperLeftX = (float)(ele.xBound - (size / 2));
                float upperLeftY = (float)(ele.yBound + (size / 2));
                Rectangle2D.Float thisQuad =
                        new Rectangle2D.Float(upperLeftX, upperLeftY, (float)size, (float)size);
                if (searchCircle.intersects(thisQuad)) {
                    for (prQuadTree child : ele.quads) {
                        Q.add(child);
                    }
                }
            } else if (!(ele instanceof EmptyLeaf)) {
                if (searchCircle.contains(((Leaf) ele).city.getLocation())) {
                    citiesInRange.add(((Leaf) ele).city);
                }
            }
        }
        return citiesInRange;
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
