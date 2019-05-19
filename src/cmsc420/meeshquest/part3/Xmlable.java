package cmsc420.meeshquest.part2;

import org.w3c.dom.*;

public interface Xmlable {
    default Document getBuilder() {
        return XmlBuilder.getBuilder();
    }
    Element toXml();
}
