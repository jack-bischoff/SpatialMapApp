package cmsc420.meeshquest.part2.Structures.Spatial.PMQuadtree;

import cmsc420.geom.Circle2D;
import cmsc420.geom.Geometry2D;
import cmsc420.meeshquest.part2.DataObject.City;
import cmsc420.meeshquest.part2.DataObject.Road;
import cmsc420.meeshquest.part2.Xmlable;
import org.w3c.dom.Element;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import java.awt.*;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.*;
import java.util.List;

import static cmsc420.geom.Inclusive2DIntersectionVerifier.intersects;
import static java.lang.Math.*;

public abstract class PMQuadtree implements Xmlable {
    private int size;
    private Rectangle2D map;
    private Validator V;
    String type;
    private Node root;
    private final White White = new White();

    static abstract class Node {
        final int NW = 0, NE = 1, SW = 2, SE = 3;
        abstract Node map(Geometry2D g, Rectangle2D view);
        Node unmap(Geometry2D g) { throw new NotImplementedException(); }
        abstract boolean contains(Geometry2D g);
        abstract Element toXml();
        abstract double distance(Point2D to);
    }

    class White extends Node {
        Node map(Geometry2D g, Rectangle2D view) {
            return new Black(g);
        }

        boolean contains(Geometry2D g) {
            return false;
        }

        Element toXml() {
            return getBuilder().createElement("white");
        }

        double distance(Point2D to) {
            return Integer.MAX_VALUE;
        }
    }

    class Gray extends Node {
        final Node[] children = new Node[]{White, White, White, White};
        final Rectangle2D.Float[] quads = new Rectangle2D.Float[4];
        final Rectangle2D area;

        Gray(Rectangle2D area) {
            this.area = area;
            int x = (int) area.getX(), y = (int) area.getY(), w = (int) area.getWidth() / 2, h = (int) area.getHeight() / 2;
            quads[NW] = new Rectangle2D.Float(x, (y + h), w, h);
            quads[NE] = new Rectangle2D.Float((x + w), (y + h), w, h);
            quads[SW] = new Rectangle2D.Float(x, y, w, h);
            quads[SE] = new Rectangle2D.Float((x + w), y, w, h);
        }

        @Override
        Node map(Geometry2D g, Rectangle2D view) {
            for (int quad = NW; quad <= SE; quad++) {
                boolean geoInsideQuadrant = inside(g, quad);
                if (geoInsideQuadrant) children[quad] = children[quad].map(g, quads[quad]);
            }
            return this;
        }

        private boolean inside(Geometry2D g, Rectangle2D quad) {
            return (g instanceof City)
                    ? intersects((City)g, quad)
                    : intersects((Road)g, quad);
        }

        private boolean inside(Geometry2D g, int quad) {
            return inside(g, quads[quad]);
        }

        boolean contains(Geometry2D g) {
            for (int quad = NW; quad <= SE; quad++) {
                if (inside(g, quad)) return children[quad].contains(g);
            }
            return false;
        }

        Element toXml() {
            Element gray = getBuilder().createElement("gray");
            gray.setAttribute("x", Double.toString(quads[NW].getX()));
            gray.setAttribute("y", Double.toString(quads[NW].getY()));
            for (Node child : children)
                gray.appendChild(child.toXml());

            return gray;
        }

        double distance(Point2D to) {
            return to.distance(area.getCenterX(), area.getCenterY());
        }
    }

    class Black extends Node {
        City city;
        TreeSet<Road> roads = new TreeSet<>();

        Black (Geometry2D g) {
            if (g instanceof City) this.city = (City)g;
            else roads.add((Road)g);
        }

        Node map(Geometry2D g, Rectangle2D view) {
            return (g instanceof City)
                    ? map((City)g, view)
                    : map((Road)g, view);
        }

        private Node map(City newCity, Rectangle2D view) {
            Node next = this;
            if (this.city != null) {
                next = new Gray(view).map(newCity, null).map(this.city, view);
                for (Road r : roads) next.map(r, view);
            } else this.city = newCity;

            return next;
        }
        private Node map(Road newRoad, Rectangle2D view) {
            roads.add(newRoad);
            return this;
        }

        boolean contains(Geometry2D g) {
            if (g instanceof City) return city.equals(g);
            else return roads.contains(g);
        }

        Element toXml() {
            int cardinality = roads.size() + ((city == null) ? 0 : 1);

            Element black = getBuilder().createElement("black");
            black.setAttribute("cardinality", Integer.toString(cardinality));
            if (city != null) black.appendChild(city.toXml());

            for (Road r : roads.descendingSet())
                black.appendChild(r.toXml());

            return black;
        }

        double distance(Point2D to) {
            if (city != null) return city.getLocation().distance(to);
            return Integer.MAX_VALUE;
        }
    }

    public PMQuadtree(int width, int height) {
        map = new Rectangle(0, 0, width, height);
        root = new White();
    }

    public boolean inRange(Point2D point) {
        return map.contains(point);
    }

    public boolean inRange(Line2D line) {
        return map.contains(line.getP1()) && map.contains(line.getP2());
    }

    public int size() {
        return size;
    }

    public boolean isEmpty() {
        return size == 0;
    }

    public boolean contains(Geometry2D g) {
        return root.contains(g);
    }

    public boolean mapCity(City city) {
        root = root.map(city, map);
        size++;
        return true;
    }

    public boolean mapRoad(Road road) {
        root = root.map(road, map);
        return true;
    }

    public void clear() {
        size = 0;
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
    public List<City> rangeCities(Point2D.Float center, int radius) {
        LinkedList<City> inRange = new LinkedList<>();
        Circle2D.Double searchCircle = new Circle2D.Double(center.getX(), center.getY(), radius);
        LinkedList<Node> Queue = new LinkedList<>();
        Queue.add(root);
        while (!Queue.isEmpty()) {
            Node current = Queue.poll();
            if (current instanceof Gray) {
                Gray gray = (Gray)current;
                if (intersects(gray.area, searchCircle))
                    Queue.addAll( Arrays.asList(gray.children));
            } else if (current instanceof Black) {
                Black black = (Black)current;
                if (black.city != null && !inRange.contains(black.city) && intersects(black.city.getLocation(), searchCircle))
                    inRange.add(black.city);
            }
        }
        return inRange;
    }

    public List<Road> rangeRoads(Point2D.Float center, int radius) {
        LinkedList<Road> inRange = new LinkedList<>();
        Circle2D.Double searchCircle = new Circle2D.Double(center.getX(), center.getY(), radius);
        LinkedList<Node> Queue = new LinkedList<>();

        Queue.add(root);
        while (!Queue.isEmpty()) {
            Node current = Queue.poll();
            if (current instanceof Gray) {
                Gray gray = (Gray)current;
                if (intersects(gray.area, searchCircle))
                    Queue.addAll( Arrays.asList(gray.children));
            } else if (current instanceof Black) {
                Black black = (Black)current;
                for (Road r : black.roads) {
                    if (!inRange.contains(r) && circleIntersect(searchCircle, r))
                        inRange.add(r);
                }
            }
        }
        return inRange;
    }
    public City nearestCity(Point2D.Float center) {
        PriorityQueue<Node> minHeap = new PriorityQueue<>(new Comparator<Node>() {
            public int compare(Node o1, Node o2) {
                double dist1 = o1.distance(center);
                double dist2 = o2.distance(center);
                if (dist1 < dist2) return -1;
                else if (dist1 > dist2) return 1;
                if (o1 instanceof Black && o2 instanceof Black)
                    return -1 * ((Black) o1).city.getName().compareTo(((Black) o2).city.getName());

                return 0;
            }
        });

        minHeap.add(root);
        while (!minHeap.isEmpty()) {
            Node current = minHeap.poll();
            if (current instanceof Black)
                return ((Black) current).city;
            else if (current instanceof Gray)
                for (Node child : ((Gray) current).children) {
                    if (child instanceof Gray) minHeap.add(child);
                    if (child instanceof Black && ((Black) child).city != null && !((Black) child).city.isIsolated())
                        minHeap.add(child);
                }
        }
        return null;
    }
    public City nearestIsolatedCity(Point2D.Float center) {
        throw new NotImplementedException();
    }
    public Road nearestRoad(Point2D.Float center) {
        throw new NotImplementedException();
    }
    public City nearestCityToRoad(Point2D.Float center) {
        throw new NotImplementedException();
    }
    public Element shortestPath(City start, City end) {
        throw new NotImplementedException();
    }

    public Element toXml() {
        Element quadtree = getBuilder().createElement("quadtree");
        quadtree.setAttribute("order", this.type);
        quadtree.appendChild(root.toXml());
        return quadtree;
    }

    private boolean circleIntersect(Circle2D c, Line2D l) {
        Point2D start = l.getP1(), end = l.getP2(), center = c.getCenter();
        Double radius = c.getRadius(), cX = center.getX(), cY = center.getY(),
                x1 = start.getX(), x2 = end.getX(), y1 = start.getY(), y2 = end.getY();

        Double distance =
                ( abs((y2 - y1)*cX - (x2 - x1)*cY + x2*y1 - y2*x1) ) / sqrt( pow((y2-y1), 2) + pow((x2 - x1), 2) );
        return distance <= radius;

    }

}
