package cmsc420.meeshquest.part1.Databases.Spatial;

import java.awt.geom.Point2D;

public class Node{
    Node[] quadrants = new Node[]{null, null, null, null}; //Array indices correspond to quadrants in a cartesian plane.
    Point2D.Float origin, end;
    Node(int width, int height) {

    }


}
