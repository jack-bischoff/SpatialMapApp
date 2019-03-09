package cmsc420.meeshquest.part1.Databases.Spatial;

import cmsc420.meeshquest.part1.DataObject.City;
import cmsc420.meeshquest.part1.Xmlable;
import org.w3c.dom.Element;

import java.awt.geom.Point2D;

public class EmptyLeaf extends Leaf implements Xmlable {

    EmptyLeaf(Point2D.Float nextMiddle, int sizeFromMiddle) {
        super(null, nextMiddle, sizeFromMiddle);
    }

    public Leaf insert(City city) {
        return new Leaf(city, nextMiddle, sizeFromMiddle);
    }

    public boolean contains(City city) {
        return false;
    }

    public Element toXml() {
        return getBuilder().createElement("white");
    }
}
