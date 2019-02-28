package cmsc420.meeshquest.part1.DataObject;

import cmsc420.meeshquest.part1.Xmlable;
import org.w3c.dom.Element;
public class Result implements Xmlable {
    Element xmlOutput;

    public Result() {
        this(null);
    }

    public Result(Element output) {
        this.xmlOutput = getBuilder().createElement("output");
        this.xmlOutput.appendChild(output);
    }

    public Element toXml() {
        return xmlOutput;
    }
}
