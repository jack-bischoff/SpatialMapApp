package cmsc420.meeshquest.part2.DataObject;

import cmsc420.geom.Geometry2D;
import cmsc420.meeshquest.part2.Xmlable;
import org.w3c.dom.Element;

import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

import static java.lang.Math.abs;

public class Road extends Line2D.Float implements Xmlable, Geometry2D {
    private City start, end;

    public Road(City start, City end) {
        this.start = start;
        this.end = end;
    }

    public Element toXml() {
        Element road = getBuilder().createElement("road");
        String end = this.end.getName(), start = this.start.getName();
        road.setAttribute("end", end);
        road.setAttribute("start", start);
        return road;
    }

    public Road sanitizeDirection() {
        if (this.end.getName().compareTo(this.start.getName()) < 0) {
            return new Road(this.end, this.start);
        }
        return this;
    }

    public City getStart() { return  start; }

    public City getEnd() { return end; }

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

    public Rectangle2D getBounds2D() {
        return new Rectangle2D.Double(start.getX(), start.getY(),abs(end.getX() - start.getX()),abs(end.getY()- start.getY()));
    }

    public double length() {
        return this.end.getLocation().distance(this.start.getLocation());
    }

    public int hashCode() {
        return start.hashCode() + end.hashCode();
    }

    public boolean equals(Object obj) {
        if (obj == null) return false;
        if (!(obj instanceof Road)) return false;
        Road other = (Road)obj;
        if (other == this) return true;
        return (
                ( other.getEnd().equals(this.getEnd()) && other.getStart().equals(this.getStart()) )
                ||
                ( other.getEnd().equals(this.getStart()) && other.getStart().equals(this.getEnd()) )
        );
    }
}
