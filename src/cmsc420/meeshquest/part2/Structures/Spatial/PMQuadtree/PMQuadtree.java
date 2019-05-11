package cmsc420.meeshquest.part2.Structures.Spatial.PMQuadtree;

import cmsc420.geom.Circle2D;
import cmsc420.geom.Geometry2D;
import cmsc420.meeshquest.part2.Comparators.RoadDescendingOrder;
import cmsc420.meeshquest.part2.DataObject.City;
import cmsc420.meeshquest.part2.DataObject.Road;
import cmsc420.meeshquest.part2.Xmlable;
import org.w3c.dom.Element;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import javax.sound.sampled.Line;
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
        abstract double distanceFromPointToCity(Point2D to);
        abstract double minDistanceFromPointToRoad(Point2D point);
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

        double distanceFromPointToCity(Point2D to) {
            return Double.MAX_VALUE;
        }
        double minDistanceFromPointToRoad(Point2D to) { return Double.MAX_VALUE; }

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
            gray.setAttribute("x", Integer.toString((int)area.getCenterX()));
            gray.setAttribute("y", Integer.toString((int)area.getCenterY()));
            for (Node child : children)
                gray.appendChild(child.toXml());

            return gray;
        }

        double distanceFromPointToCity(Point2D point) {
            return distanceFromPointToRectangle(point, area);
        }
        double minDistanceFromPointToRoad(Point2D point) {
           return distanceFromPointToRectangle(point, area);
        }
    }

    class Black extends Node {
        City city;
        HashSet<Road> roads = new HashSet<>();

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
            if (city != null)
                black.appendChild(city.toXml( (city.isIsolated()) ? "isolatedCity" : "city" ));

            Road[] descending = roads.toArray(new Road[0]);
            Arrays.sort(descending, new RoadDescendingOrder());
            for (Road r : descending)
                black.appendChild(r.toXml());

            return black;
        }

        double distanceFromPointToCity(Point2D to) {
            if (city != null) return city.getLocation().distance(to);
            return Integer.MAX_VALUE;
        }

        double minDistanceFromPointToRoad(Point2D to) {
            if (!roads.isEmpty())
                return minRoad(to).ptSegDist(to);
            return Double.MAX_VALUE;
        }

        Road minRoad(Point2D center) {
            Road min = null;
            double dist, minDist = Double.MAX_VALUE;
            for (Road road : roads) {
                dist = road.ptSegDist(center);
                if (dist < minDist) {
                    minDist = dist;
                    min = road;
                } else if (dist == minDist) {
                    if (new RoadDescendingOrder().compare(road, min) < 0) {
                        min = road;
                    }
                }
            }
            return min;
        }

    }

    public PMQuadtree(int width, int height) {
        map = new Rectangle(0, 0, width, height);
        root = White;
    }

    public boolean inRange(Point2D point) {
        return intersects(point, map);
    }

    public boolean inRange(Line2D line) {
        return intersects(line.getP1(), map) && intersects(line.getP2(), map);
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
        LinkedList<City> citiesInRange = new LinkedList<>();
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
                if (black.city != null && !citiesInRange.contains(black.city) && intersects(black.city.getLocation(), searchCircle))
                    citiesInRange.add(black.city);
            }
        }
        return citiesInRange;
    }

    public List<Road> rangeRoads(Point2D.Float center, int radius) {
        LinkedList<Road> roadsInRange = new LinkedList<>();
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
                    if (!roadsInRange.contains(r) && circleIntersect(searchCircle, r))
                        roadsInRange.add(r);
                }
            }
        }
        return roadsInRange;
    }

    public City nearestCity(Point2D center, nearestValidator V) {
        PriorityQueue<Node> minHeap = new PriorityQueue<>(new PointDistanceComparator(center));

        minHeap.add(root);
        while (!minHeap.isEmpty()) {
            Node current = minHeap.poll();
            if (current instanceof Black && V.validate((Black)current))
                return ((Black) current).city;
            else if (current instanceof Gray)
                for (Node child : ((Gray) current).children) {
                    if (child instanceof Gray) minHeap.add(child);
                    else if (child instanceof Black && V.validate((Black)child)) minHeap.add(child);
                }
        }
        return null;
    }

    public City nearestCity(Point2D center) {
        nearestValidator V = new nearestValidator() {
            boolean validate(Black b) {
                return b.city != null && !b.city.isIsolated();
            }
        };
        return nearestCity(center, V);
    }

    public City nearestIsolatedCity(Point2D center) {
        nearestValidator V = new nearestValidator() {
            boolean validate(Black b) {
                return b.city != null && b.city.isIsolated();
            }
        };
        return nearestCity(center, V);
    }

    public Road nearestRoad(Point2D center) {
        PriorityQueue<Node> minHeap = new PriorityQueue<>(new RoadDistanceComparator(center));

        minHeap.add(root);
        while (!minHeap.isEmpty()) {
            Node current = minHeap.poll();
            if (current instanceof Black)
                return ((Black)current).minRoad(center);
            else if (current instanceof Gray)
                for (Node child : ((Gray) current).children)
                    if (child instanceof Gray) minHeap.add(child);
                    else if (child instanceof Black && !((Black) child).roads.isEmpty()) minHeap.add(child);
        }
        return null;
    }

    public City nearestCityToRoad(Road road) {
        throw new NotImplementedException();
    }

    public Element toXml() {
        Element quadtree = getBuilder().createElement("quadtree");
        quadtree.setAttribute("order", this.type);
        quadtree.appendChild(root.toXml());
        return quadtree;
    }

    //Helper methods and Comparators
    private boolean circleIntersect(Circle2D c, Line2D l) {
        Point2D start = l.getP1(), end = l.getP2(), center = c.getCenter();
        if (intersects(start, c) || intersects(end, c)) return true;
        else {
            double  radius = c.getRadius(),
                    cX = center.getX(), cY = center.getY(),
                    x1 = start.getX(), x2 = end.getX(), y1 = start.getY(), y2 = end.getY();
            double  dx = x2 - x1,
                    dy = y2 - y1,
                    dr2 = pow(dx, 2) + pow(dy, 2),
                    D = x1 * y2 - x2 * y1;

            double discriminant = pow(radius, 2) * dr2 - pow(D, 2);
            Line2D.Double cHorz = new Line2D.Double(c.getMinX(), cY, c.getMaxX(), cY);
            Line2D.Double cVert = new Line2D.Double(cX, c.getMinY(), cX , c.getMaxY());
            if (discriminant >= 0 && ( cHorz.intersectsLine(l) || cVert.intersectsLine(l))) return true;
            else return false;
        }

//        double top = abs( (y2 - y1)*cX - (x2 - x1)*cY + x2*y1 - y2*x1 );
//        double bottom =  sqrt( pow((y2-y1), 2) + pow((x2 - x1), 2) );
//        double distance = top / bottom;
//        return distance <= radius;


    }

    private double distanceFromPointToRectangle(Point2D point, Rectangle2D rect) {
        if (intersects(point, rect)) return 0;

        Line2D.Double top, bottom, left, right;
        double x = rect.getX(), y = rect.getY(), height = rect.getHeight(), width = rect.getWidth();
        top = new Line2D.Double(x, y + height, x + width, y + height);
        bottom = new Line2D.Double(x, y, x + width, y);
        left = new Line2D.Double(x, y, x, y + height);
        right = new Line2D.Double(x + width, y, x + width, y + height);
        return min( min(top.ptSegDist(point), bottom.ptSegDist(point)), min(left.ptSegDist(point), right.ptSegDist(point)) );

    }

    private class PointDistanceComparator implements Comparator<Node> {
        private Point2D center;
        PointDistanceComparator(Point2D center) {
            this.center = center;
        }
        public int compare(Node o1, Node o2) {
            double dist1 = o1.distanceFromPointToCity(center);
            double dist2 = o2.distanceFromPointToCity(center);
            if (dist1 < dist2) return -1;
            else if (dist1 > dist2) return 1;
            else if (dist1 == dist2 && o1 instanceof Black && o2 instanceof Black)
                return -1 * ((Black) o1).city.getName().compareTo(((Black) o2).city.getName());

            return 0;
        }
    }

    private abstract class nearestValidator {
        abstract boolean validate(Black b);
    }

    private class RoadDistanceComparator implements Comparator<Node> {
        private Point2D center;
        RoadDistanceComparator(Point2D center) {
            this.center = center;
        }

        public int compare(Node o1, Node o2) {
            double dist1 = o1.minDistanceFromPointToRoad(center);
            double dist2 = o2.minDistanceFromPointToRoad(center);
            if (dist1 < dist2) return -1;
            else if (dist1 > dist2) return 1;
            else {
                if (o1 instanceof Black && o2 instanceof Black)
                    return new RoadDescendingOrder().compare( ((Black) o1).minRoad(center), ((Black) o2).minRoad(center));
                else if ( o1 instanceof Gray && !(o2 instanceof Gray))
                    return -1;
                else if (o2 instanceof  Gray && !(o1 instanceof Gray))
                    return 1;
            }
            return 0;
        }
    }

}
