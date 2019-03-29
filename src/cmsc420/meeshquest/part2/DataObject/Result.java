package cmsc420.meeshquest.part2.DataObject;

import cmsc420.meeshquest.part2.Xmlable;
import org.w3c.dom.Element;
public class Result implements Xmlable {
    Element xmlOutput;

    public Result() {
        this.xmlOutput = getBuilder().createElement("output");
    }

    public Result(Element output) {
        this();
        if (output != null) this.xmlOutput.appendChild(output);
    }

    public Element toXml() {
        return xmlOutput;
    }
}
