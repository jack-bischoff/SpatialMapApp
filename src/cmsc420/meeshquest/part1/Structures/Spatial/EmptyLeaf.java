package cmsc420.meeshquest.part1.Structures.Spatial;

import cmsc420.meeshquest.part1.DataObject.City;
import org.w3c.dom.Element;

public class EmptyLeaf extends Leaf {
    private static EmptyLeaf instance = new EmptyLeaf();

    private EmptyLeaf() {
        super(null);
    }

    static EmptyLeaf EmptyLeaf() {
        return instance;
    }

    boolean contains(City city) {
        return false;
    }
    Node delete(City city) { return instance; }

    public Element toXml() {
        return getBuilder().createElement("white");
    }
}
