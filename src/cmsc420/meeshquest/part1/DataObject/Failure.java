package cmsc420.meeshquest.part1.DataObject;

import cmsc420.meeshquest.part1.Xmlable;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class Failure extends Result implements Xmlable {
    public Failure(String msg) {
       super(msg);
    }

    public Element toXml() {
        Document builder = getBuilder();
        Element root = builder.createElement("error");
        root.setAttribute("type", (String) payload);
        return root;
    }

}
