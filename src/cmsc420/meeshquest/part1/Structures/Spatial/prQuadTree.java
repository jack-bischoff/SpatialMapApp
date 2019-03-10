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
import java.util.Stack;

public class prQuadTree implements Xmlable {
    private int maxX, maxY, initialSize;
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

    public prQuadTree(int width, int height) {
        this.maxX = width;
        this.maxY = height;
        this.initialSize = width;
    }

    public boolean isEmpty() { return root instanceof EmptyLeaf; }
    public boolean isOutOfBounds(City city) { return city.getX() > maxX || city.getY() > maxY;}

    private int[] calcBounds(Node parent, int intoThisQuad) {
        int[] nextMiddle = null;
        int x = parent.xBound, y = parent.yBound, ds = parent.sizeOfQuad / 2;

        if (intoThisQuad == 0)       nextMiddle = new int[]{x - ds, y + ds}; //NW
        else if (intoThisQuad == 1)  nextMiddle = new int[]{x + ds, y + ds}; //NE
        else if (intoThisQuad == 2)  nextMiddle = new int[]{x + ds, y - ds}; //SE
        else if (intoThisQuad == 3)  nextMiddle = new int[]{x - ds, y - ds}; //SW

        return nextMiddle;
    }

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
        while (!(curr instanceof Leaf)) {
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
                root = new Internal(maxX, maxY, maxX / 2);
                curr = root;
            }

            //Re-finding nextQuad in-case of head corner case
            nextQuad = curr.findQuad(city);
            while (nextQuad == curr.findQuad(temp.city)) {
                middlePt = calcBounds(curr, nextQuad);
                Node n = new Internal(middlePt[0], middlePt[1],curr.sizeOfQuad / 2);
                curr.quads[nextQuad] = n;
                curr = n;
                nextQuad = curr.findQuad(city);
            }
            curr.quads[nextQuad] = new Leaf(city);
            curr.quads[curr.findQuad(temp.city)] = new Leaf(temp.city);
        }
        return true;
    }

    public boolean delete(City city) {
        Stack<Node> stack = new Stack<>();
        Node curr = root, prev = null;
        int nextQuad = 0;

        while (!(curr instanceof Leaf)){
            stack.push(curr);
            nextQuad = curr.findQuad(city);
            curr = curr.quads[nextQuad];
        }
        //Will check if Leaf.city == city or if EmptyLeaf then false
        if (((Leaf) curr).contains(city)) {
            if (stack.isEmpty()) {
                root = EmptyLeaf.EmptyLeaf();
            }
            else {
                boolean done = false;
                Node parent = stack.pop();
                parent.quads[nextQuad] = EmptyLeaf.EmptyLeaf();
                //Cleanup
                while (!(stack.isEmpty()) && !done) {
                    Leaf leaf = null;
                    for (Node child : parent.quads) {
                        if (child instanceof Internal) done = true;
                        else if (!(child instanceof EmptyLeaf)) {
                            if (leaf == null) leaf = (Leaf) child;
                            else done = true;
                        }
                    }
                    if (!done) {
                        parent = stack.pop();
                        parent.quads[parent.findQuad(leaf.city)] = leaf;
                    }
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
                float upperLeftX = (float)(ele.xBound - (ele.sizeOfQuad / 2));
                float upperLeftY = (float)(ele.yBound + (ele.sizeOfQuad / 2));
                Rectangle2D.Float thisQuad =
                        new Rectangle2D.Float(upperLeftX, upperLeftY, (float)ele.sizeOfQuad, (float)ele.sizeOfQuad);
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

    public boolean contains(City city) {
        return root.quads[root.findQuad(city)].contains(city);
    }

    public Element toXml() {
        return root.toXml();
    }
}
