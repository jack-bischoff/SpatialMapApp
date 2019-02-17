package cmsc420.meeshquest.part1.DataObject;

import cmsc420.meeshquest.part1.Xmlable;
import org.w3c.dom.Element;
public class Result implements Xmlable {
    Element xmlOutput;

    public Result(Element output) {
        this.xmlOutput = output;
    }
    public Result() {
        this.xmlOutput = getBuilder().createElement("output");
    }

    public Element toXml() {
        return xmlOutput;
    }

    public Element prepend(Element newChild) {

    }

}
