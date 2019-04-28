package cmsc420.meeshquest.part2.DataObject;

import cmsc420.meeshquest.part2.Xmlable;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import java.awt.geom.Point2D;

public class City extends Point2D.Float implements Xmlable {
    private int radius;
    private String color, name;

    public City(String name, int x, int y, int radius, String color) {
        super(x, y);
        this.radius = radius;
        this.color = color;
        this.name = name;
    }

    public int getRadius() {
        return this.radius;
    }

    public String getName() {
        return this.name;
    }

    public String getColor() {
        return this.color;
    }

    public Point2D.Float getLocation() { return new Point2D.Float((float)getX(), (float)getY());}

    public Element toXml() {
        String[] names = {"name", "x", "y", "radius", "color"};
        String[] values =
                {getName(), Integer.toString((int)getX()), Integer.toString((int)getY()), Integer.toString(getRadius()), getColor()};
        Document builder = getBuilder();
        Element city = builder.createElement("city");
        for (int i = 0; i < names.length; i++) {
            city.setAttribute(names[i], values[i]);
        }
        return city;
    }

    public String toString() {
        return toXml().toString();
    }
}
