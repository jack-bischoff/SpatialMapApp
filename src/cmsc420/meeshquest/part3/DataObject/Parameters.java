package cmsc420.meeshquest.part3.DataObject;

import cmsc420.meeshquest.part3.Xmlable;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;


import java.util.LinkedHashMap;

public class Parameters implements Xmlable {
    private LinkedHashMap<String, String> params;

    public Parameters() {
        this.params = null;
    }
    public Parameters(NamedNodeMap attributes, String[] ordering) {
        if (attributes != null) {
            this.params = new LinkedHashMap<>();
            for (String param : ordering) {
                Node item = attributes.getNamedItem(param);
                if (item != null) params.put(item.getNodeName(), item.getNodeValue());
            }
        }
    }

    public String get(String name) {
        if (params == null) return null;
        return params.get(name);
    }

    public Element toXml() {
        Document builder = getBuilder();
        Element Parameters = builder.createElement("parameters");
        if (params != null) {
            for (String paramName : params.keySet()) {
                Element nextParam = builder.createElement(paramName);
                nextParam.setAttribute("value", params.get(paramName));
                Parameters.appendChild(nextParam);
            }
        }
        return Parameters;
    }
}
