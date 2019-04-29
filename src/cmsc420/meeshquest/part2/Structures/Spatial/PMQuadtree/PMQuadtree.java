package cmsc420.meeshquest.part2.Structures.Spatial.PMQuadtree;

import cmsc420.geom.Geometry2D;
import cmsc420.meeshquest.part2.DataObject.City;
import cmsc420.meeshquest.part2.DataObject.Road;
import cmsc420.meeshquest.part2.Xmlable;
import org.w3c.dom.Element;
import org.w3c.dom.css.Rect;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import java.awt.*;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.EnumMap;
import java.util.TreeSet;

import static cmsc420.geom.Inclusive2DIntersectionVerifier.intersects;

public abstract class PMQuadtree implements Xmlable {
    private int width, height, size;
    private Validator V;
    private Node root;
    private final White White = new White();

    static abstract class Node {
        final int NW = 0, NE = 1, SW = 2, SE = 3;
        abstract Node map(Geometry2D g);
        Node unmap(Geometry2D g) { throw new NotImplementedException(); }
        abstract boolean contains(Geometry2D g);
        abstract Element toXml();
    }

    class White extends Node {
        Node map(Geometry2D g) {
            return new Black(g);
        }

        boolean contains(Geometry2D g) {
            return false;
        }

        Element toXml() {
            return getBuilder().createElement("white");
        }
    }

    class Grey extends Node {
        Node[] children = new Node[4];
        Rectangle[] quads = new Rectangle[4];

        Grey() {

        }

        Node map(Geometry2D g) {
            return null;
        }

        boolean contains(Geometry2D g) {
            Point2D vertex = ((City)g).getLocation();
            int Q = SE;
            if (intersects(vertex, quads[NW])) Q = NW;
            else if (intersects(vertex, quads[NE])) Q = NE;
            else if (intersects(vertex, quads[SW])) Q = SW;
            return children[Q].contains(g);
        }

        Element toXml() {
            Element gray = getBuilder().createElement("gray");
            gray.setAttribute("x", Double.toString(quads[NW].getX()));
            gray.setAttribute("y", Double.toString(quads[NW].getY()));
            for (Node child : children)
                gray.appendChild(child.toXml());

            return gray;
        }
    }

    class Black extends Node {
        City city;
        TreeSet<Road> roads;

        Black (Geometry2D g) {
            if (g instanceof City) this.city = (City)g;
            else {
                if (roads == null) roads = new TreeSet<>();
                roads.add((Road)g);
            }
        }

        Node map(Geometry2D g) {
            return null;
        }

        boolean contains(Geometry2D g) {
            if (g instanceof City) return city.equals(g);
            else return roads.contains(g);
        }

        Element toXml() {
            int cardinality = roads.size() + ((city == null) ? 0 : 1);
            Element black = getBuilder().createElement("black");

            black.setAttribute("cardinality", Integer.toString(cardinality));
            black.appendChild(city.toXml());

            for (Road r : roads.descendingSet())
                black.appendChild(r.toXml());

            return black;
        }
    }

    public PMQuadtree(int width, int height) {
        this.width = width;
        this.height = height;
        root = new White();
    }
    public boolean inRange(Point2D point) {
        return point.getX() <= width && point.getY() < height;
    }
    public int size() {
        return size;
    }
    public boolean isEmpty() {
        return size == 0;
    }
    public boolean contains(City city) {
        throw new NotImplementedException();
    }
    public boolean contains(Road road) {
        throw new NotImplementedException();
    }

    public boolean mapCity(City city) {
        root = root.map(city);
    }

    public boolean mapRoad(Road road) {
        throw new NotImplementedException();
    }

    public void clear() {
        root = White;
    }

    public void saveMap() {
        throw new NotImplementedException();
    }
    //Part3
    public boolean unmapCity(City city) {
        throw new NotImplementedException();
    }
    //Part3
    public boolean unmapRoad(Road road) {
        throw new NotImplementedException();
    }

    //Most likely will be iterative solutions
    public ArrayList<City> rangeCities(Point2D.Float here, int radius) {
        throw new NotImplementedException();
    }
    public ArrayList<Road> rangeRoads(Point2D.Float here, int radius) {
        throw new NotImplementedException();
    }
    public City nearestCity(Point2D.Float here) {
        throw new NotImplementedException();
    }
    public City nearestIsolatedCity(Point2D.Float here) {
        throw new NotImplementedException();
    }
    public Road nearestRoad(Point2D.Float here) {
        throw new NotImplementedException();
    }
    public City nearestCityToRoad(Point2D.Float here) {
        throw new NotImplementedException();
    }
    public Element shortestPath(City start, City end) {
        throw new NotImplementedException();
    }

    public Element toXml() {
        return root.toXml();
    }

}
