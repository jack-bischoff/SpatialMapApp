package cmsc420.meeshquest.part2.DataObject;

import cmsc420.geom.Geometry2D;
import cmsc420.meeshquest.part2.Xmlable;
import org.w3c.dom.Element;

import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

import static java.lang.Math.abs;

public class Road extends Line2D.Float implements Xmlable, Geometry2D, Comparable {
    City end, start;

    Road(City end, City start) {
        this.end = end;
        this.start = start;
    }

    public Element toXml() {
        Element road = getBuilder().createElement("road");
        road.setAttribute("end", this.end.getName());
        road.setAttribute("start", this.start.getName());
        return road;
    }

    public int getType() {
        return 1;
    }

    public double getX1() {
        return start.getX();
    }

    public double getY1() {
        return start.getY();
    }

    public Point2D getP1() {
        return start.getLocation();
    }

    public double getX2() {
        return end.getX();
    }

    public double getY2() {
        return end.getY();
    }

    public Point2D getP2() {
        return end.getLocation();
    }

    public void setLine(double x1, double y1, double x2, double y2) {

    }

    public Rectangle2D getBounds2D() {
        return new Rectangle2D.Double(start.getX(), start.getY(),abs(end.getX() - start.getX()),abs(end.getY()- start.getY()));
    }

    public int compareTo(Object o) {
        Road r = (Road)o;
        int res = this.start.getName().compareTo(r.start.getName());
        if (res == 0)
            res = this.end.getName().compareTo(r.end.getName());
        return -1*res;
    }
}
