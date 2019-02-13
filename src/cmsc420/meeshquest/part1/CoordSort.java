package cmsc420.meeshquest.part1;

import java.util.Comparator;
import java.awt.geom.Point2D;
public class CoordSort implements Comparator<Point2D.Float> {
    public int compare(Point2D.Float p1, Point2D.Float p2) {
        if (p1.getY() < p2.getY()) return -1;
        if (p1.getY() > p2.getY()) return 1;
        if (p1.getX() < p2.getX()) return -1;
        if (p1.getX() > p2.getX()) return 1;
        return 0;
    }
}
