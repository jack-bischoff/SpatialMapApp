package cmsc420.meeshquest.part2.Structures.Spatial.PRQuadTree;

import cmsc420.meeshquest.part2.DataObject.City;
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
