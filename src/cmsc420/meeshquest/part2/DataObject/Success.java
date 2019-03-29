package cmsc420.meeshquest.part2.DataObject;

import cmsc420.meeshquest.part2.Xmlable;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class Success extends Result implements Xmlable {

    public Success() {
        super();
    }

    public Success(Element output) {
        super(output);
    }

    public Element toXml() {
        Document builder = getBuilder();
        Element Status = builder.createElement("success");
        Status.appendChild(this.xmlOutput);
        return Status;
    }
}
