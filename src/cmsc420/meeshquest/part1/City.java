package cmsc420.meeshquest.part1;

import java.awt.geom.Point2D;

public class City extends Point2D.Float {
    private int radius;
    public String color, name;

    City(String name, int x, int y, int radius, String color) {
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

    public String toXML() {
        String cityXMLFormat = "<city name=\"%s\" x=\"%d\" y=\"%d\" radius=\"%d\" color=\"%s\" />";
        return String.format(cityXMLFormat, name, x, y, radius, color);
    }
}
