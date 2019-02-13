package cmsc420.meeshquest.part1.DataObject;

import java.awt.geom.Point2D;

public class City extends Point2D.Float {
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


}
