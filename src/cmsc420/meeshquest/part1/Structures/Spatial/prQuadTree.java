package cmsc420.meeshquest.part1.Structures.Spatial;

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

    private Node root = EmptyLeaf.EmptyLeaf();

    class distCompare implements Comparator<Node> {
        Point2D.Float point;
        public distCompare(Point2D.Float point) {
            this.point = point;
        }
        public int compare(Node n1, Node n2) {
            double dist1 = n1.dist(point);
            double dist2 = n2.dist(point);
            if (dist1 < dist2) return -1;
            else if (dist1 > dist2) return 1;
//                else return c1.getName().compareTo(c2.getName());
            return 0;
        }
    }

    prQuadTree() {}
    public prQuadTree(int width, int height, int size) {
        int x = width;
        this.y = height;
        this.size = size;
        int ds = size / 2;
//
//        //In clockwise order:
//        quads = new prQuadTree[]{
//                new EmptyLeaf(new Point2D.Float(x - ds, y + ds), ds), //NW
//                new EmptyLeaf(new Point2D.Float(x + ds, y + ds), ds), //NE
//                new EmptyLeaf(new Point2D.Float(x + ds, y - ds), ds), //SE
//                new EmptyLeaf(new Point2D.Float(x - ds, y - ds), ds)  //SW
//        };
    }
    private int[] calcBounds(Node parent, int intoThisQuad) {
        int[] nextMiddle = null;
        int x = parent.xBound, y = parent.yBound, ds = parent.size / 2;

        if (intoThisQuad == 0)       nextMiddle = new int[]{x - ds, y + ds}; //NW
        else if (intoThisQuad == 1)  nextMiddle = new int[]{x + ds, y + ds}; //NE
        else if (intoThisQuad == 2)  nextMiddle = new int[]{x + ds, y - ds}; //SE
        else if (intoThisQuad == 3)  nextMiddle = new int[]{x - ds, y - ds}; //SW

        return nextMiddle;
    }

    public void insert(City city) {
        int nextQuad = 0;
        Node curr = root, prev = null;
        while (!(curr instanceof Leaf)) {
            nextQuad = curr.findQuad(city);
            prev = curr;
            curr = curr.quads[nextQuad];
        }

        if (curr instanceof EmptyLeaf) {
            //EmptyLeaf can be replaced by a Leaf
            //Special case for head
            if (prev == null) root = new Leaf(city);
            else prev.quads[nextQuad] = new Leaf(city);
        } else {
            //Decomposition of Leaf into more quadrants
            Leaf temp = (Leaf)curr;
            int[] middlePt;
            curr = prev;
            while (nextQuad == curr.findQuad(temp.city)) {
                middlePt = calcBounds(curr, nextQuad);
                Node n = new Internal(middlePt[0], middlePt[1],curr.size / 2);
                curr.quads[nextQuad] = n;
                curr = n;
                nextQuad = curr.findQuad(city);
            }
            curr.quads[nextQuad] = new Leaf(city);
            curr.quads[curr.findQuad(temp.city)] = new Leaf(temp.city);
        }
    }

    public boolean delete(City city) {
        Node curr = root, prev = null;
        int nextQuad = 0;
        while (!(curr instanceof Leaf)){
            nextQuad = curr.findQuad(city);
            prev = curr;
            curr = curr.quads[nextQuad];
        }
        if (((Leaf) curr).contains(city)) {
            if (prev == null) {
                root = EmptyLeaf.EmptyLeaf();
            }
            else{
                prev.quads[nextQuad] = EmptyLeaf.EmptyLeaf();
                int nonEmpty = 4;
                for (Node child : prev.quads) {
                    if (child instanceof EmptyLeaf) nonEmpty--;
                }

            }
        } else return false;

        return true;
    }

    public City nearest(Point2D.Float nearestTo) {
        PriorityQueue<Node> Q = new PriorityQueue<>(new distCompare(nearestTo));
        Q.add(root);
        while (!Q.isEmpty()) {
            Node ele = Q.poll();
            if (!(ele instanceof Leaf)) {
                for (Node Quad : ele.quads) Q.add(Quad);
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

        PriorityQueue<Node> Q = new PriorityQueue<>();
        Q.add(root);
        while (!Q.isEmpty()) {
            Node ele = Q.poll();
            if (!(ele instanceof Leaf)) {
                float upperLeftX = (float)(ele.xBound - (ele.size / 2));
                float upperLeftY = (float)(ele.yBound + (ele.size / 2));
                Rectangle2D.Float thisQuad =
                        new Rectangle2D.Float(upperLeftX, upperLeftY, (float)ele.size, (float)ele.size);
                if (searchCircle.intersects(thisQuad)) {
                    for (Node child : ele.quads) {
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
        return root.toXml();
    }

    public boolean contains(City city) {
        return root.quads[root.findQuad(city)].contains(city);
    }
}
