package cmsc420.meeshquest.part3.Structures.Spatial.PRQuadTree;

import cmsc420.geom.Circle2D;
import cmsc420.geom.Inclusive2DIntersectionVerifier;
import cmsc420.meeshquest.part3.DataObject.City;
import cmsc420.meeshquest.part3.Xmlable;
import org.w3c.dom.Element;

import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.*;

public class prQuadTree implements Xmlable {
    private int maxX, maxY;
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
            if (n1 instanceof Leaf && n2 instanceof Leaf)
                return -1*((Leaf)n1).city.getName().compareTo(((Leaf)n2).city.getName());
            return 0;
        }
    }

    public prQuadTree(int width, int height) {
        this.maxX = width;
        this.maxY = height;
    }

    public boolean isEmpty() { return root instanceof EmptyLeaf; }
    public boolean isOutOfBounds(City city) { return city.getX() >= maxX || city.getY() >= maxY;}
    public prQuadTree clear() { return new prQuadTree(maxX, maxY); }
    //Unfortunately deep decomposition overflows Java stack
    //Don't know if tail-recursive insert is possible/optimized in Java
    //Could do prQuadTree with buckets, but...maybe later...complicates distance ranking
    public boolean insert(City city) {
        int nextQuad = 0;
        Node curr = root, prev = null;

        //Basic error checking
        //CitySpatialMap never passes in malformed data, since it handles errors
        //but the data structure shouldn't crash or devolve into an infinite loop, regardless.
        if (isOutOfBounds(city)) return false;
        if (root.contains(city)) return false;

        //Finding insertion quadrant. Insertion begins at a Leaf
        while (curr instanceof Internal) {
            nextQuad = curr.findQuad(city);
            prev = curr;
            curr = curr.quads[nextQuad];
        }
        //No city previously here.
        if (curr instanceof EmptyLeaf) {
            //EmptyLeaf can be replaced by a Leaf
            //Special case for head...probably could do some kind of dummy head node or something...
            if (prev == null) root = new Leaf(city);
            else prev.quads[nextQuad] = new Leaf(city);
        } else {
            //City already in quadrant. Decomposition of Leaf into more quadrants
            int[] middlePt;
            Leaf temp = (Leaf)curr;
            curr = prev;
            //if head was a Leaf then transform it into a quadrant
            if (curr == null) {
                root = new Internal(maxX / 2, maxY / 2, maxX, maxY);
                curr = root;
            }

            //Re-finding nextQuad in-case of head corner case
            nextQuad = curr.findQuad(city);
            while (nextQuad == curr.findQuad(temp.city)) {
                middlePt = curr.calcNextMiddle(nextQuad);
                Node n = new Internal(middlePt[0], middlePt[1],curr.width / 2, curr.height / 2);
                curr.quads[nextQuad] = n;
                curr = n;
                nextQuad = curr.findQuad(city);
            }
            curr.quads[nextQuad] = new Leaf(city);
            curr.quads[curr.findQuad(temp.city)] = new Leaf(temp.city);
        }
        return true;
    }
    public void delete(City city) {
        root = root.delete(city);
    }

    public City nearest(Point2D.Float nearestTo) {
        PriorityQueue<Node> Q = new PriorityQueue<>(new distCompare(nearestTo));
        Q.add(root);
        while (!Q.isEmpty()) {
            Node ele = Q.poll();
            if (ele instanceof Internal) {
                for (Node child : ele.quads)
                    if (!(child instanceof EmptyLeaf)) Q.add(child);
            } else if (!(ele instanceof EmptyLeaf)) {
                return ((Leaf)ele).city;
            }
        }
        return null;
    }

    public ArrayList<City> range(Point2D.Float from, int radius) {
        ArrayList<City> citiesInRange = new ArrayList<>();
        Circle2D.Double searchCircle = new Circle2D.Double(from.getX(), from.getY(), radius);

        LinkedList<Node> FIFO = new LinkedList<>();
        FIFO.add(root);
        while (!FIFO.isEmpty()) {
            Node ele = FIFO.poll();
            if (ele instanceof Internal) {
                int lowerLeftX = (ele.xBound - ele.width/2);
                int lowerLeftY = (ele.yBound - ele.height/2);
                Rectangle2D.Float thisQuad =
                        new Rectangle2D.Float(lowerLeftX, lowerLeftY, ele.width, ele.height);
                if (thisQuad.contains(from) || searchCircle.intersects(thisQuad)) {
                    for (Node child : ele.quads) {
                        if (!(child instanceof EmptyLeaf)) FIFO.add(child);
                    }
                }
            } else if (!(ele instanceof EmptyLeaf)) {
                Leaf l = (Leaf) ele;
                if (Inclusive2DIntersectionVerifier.intersects(l.city.getLocation() ,searchCircle)) {
                    citiesInRange.add(l.city);
                }
            }
        }
        return citiesInRange;
    }

    public boolean contains(City city) {
        Node next = (root instanceof Leaf) ? root : root.quads[root.findQuad(city)];
        return next.contains(city);
    }

    public Element toXml() {
        return root.toXml();
    }
}
