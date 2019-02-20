package cmsc420.meeshquest.part1.Databases.Spatial;

import cmsc420.meeshquest.part1.DataObject.City;

import java.awt.geom.Point2D;

public class prQuadTree {
    prQuadTree[] quads =
            new prQuadTree[]{new EmptyLeaf(), new EmptyLeaf(), new EmptyLeaf(), new EmptyLeaf()}; //Array indices correspond to quadrants in a cartesian plane.
    Point2D.Float center;

    public class Leaf extends prQuadTree {
        City city;
        Leaf(City city) {
            super();
            this.city = city;
        }

        public prQuadTree insert(City newCity) {
            prQuadTree newQuad = new prQuadTree();
            newQuad = newQuad.insert(this.city);
            return newQuad.insert(newCity);
        }
    }
    public class EmptyLeaf extends prQuadTree {
        EmptyLeaf() {
            super();
        }
        public Leaf insert(City city) {
            return new Leaf(city);
        }
    }

    public prQuadTree() {

    }


    public prQuadTree insert(City city) {
        int xBounds, yBounds, xCoord = (int) city.getX(), yCoord = (int) city.getY(), quadName;
        if (yCoord <= yBounds) {
            if (xCoord <= xBounds) quadName = 2;
            else quadName = 3;
        } else {
            if (xCoord <= xBounds) quadName = 1;
            else quadName = 0;
        }
        quads[quadName] = quads[quadName].insert(city);
        return this;
    }
}
