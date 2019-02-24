package cmsc420.meeshquest.part1.Databases.Spatial;

import cmsc420.meeshquest.part1.DataObject.City;

import java.awt.geom.Point2D;

public class prQuadTree {
    prQuadTree[] quads; //Array indices correspond to quadrants in a cartesian plane.
    Point2D.Float origin, end;

    public class Leaf extends prQuadTree {
        City city;
        Leaf(City city, Point2D.Float origin, Point2D.Float end) {
            super();
            this.city = city;
        }

        public prQuadTree insert(City newCity) {
            prQuadTree newQuad = new prQuadTree();
            newQuad = newQuad.insert(this.city);
            return newQuad.insert(newCity);
        }
        public boolean contains(City city) { return true; }
    }
    public class EmptyLeaf extends prQuadTree {
        EmptyLeaf(Point2D.Float origin, Point2D.Float end) {
            super(origin, end);
        }
        public Leaf insert(City city) {
            return new Leaf(city, origin, end);
        }
        public boolean contains(City city) { return false; }
    }


    public prQuadTree(Point2D.Float origin, Point2D.Float end) {
        this.origin = origin;
        this.end = end;
        quads = new prQuadTree[]{
                new EmptyLeaf(new Point2D.Float((float)origin.getX()/2, (float)origin.getY()/2), end),
                new EmptyLeaf(new Point2D.Float(), new Point2D.Float()),
                new EmptyLeaf(origin, new Point2D.Float()),
                new EmptyLeaf(new Point2D.Float(), new Point2D.Float()),
        };

    }

    public prQuadTree insert(City city) {
        int xCoord = (int) city.getX(), yCoord = (int) city.getY(), quad;
        quad = this.findQuad(xCoord, yCoord);
        quads[quad] = quads[quad].insert(city);
        return this;
    }

    public boolean contains(City city) {
        int quad = findQuad((int) city.getX(), (int) city.getY());
        return quads[quad].contains(city);
    }
    private int findQuad(int x, int y) {
        int xBounds, yBounds;
        xBounds = (int)(end.getX() - origin.getX()) / 2;
        yBounds = (int)(end.getY() - origin.getY()) / 2;
        if (y <= yBounds) {
            if (x <= xBounds) return 2;
            else return 3;
        } else {
            if (x <= xBounds) return 1;
            else return 0;
        }
    }
}
