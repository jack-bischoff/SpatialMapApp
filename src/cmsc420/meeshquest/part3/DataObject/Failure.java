package cmsc420.meeshquest.part2.DataObject;

import cmsc420.meeshquest.part2.Xmlable;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class Failure extends Result implements Xmlable {
    private String msg;

    public Failure(Object o) {this.msg = o.toString();}

    public Element toXml() {
        Document builder = getBuilder();
        Element root = builder.createElement("error");
        root.setAttribute("type", msg);
        return root;
    }

}
