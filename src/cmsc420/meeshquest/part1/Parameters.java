package cmsc420.meeshquest.part1;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;


import java.util.LinkedHashMap;

public class Parameters {
    private LinkedHashMap<String, String> params = null;

    Parameters(NamedNodeMap attributes, String[] ordering) {
        if (attributes != null) {
            this.params = new LinkedHashMap<>();
            for (String param : ordering) {
                Node item = attributes.getNamedItem(param);
                params.put(item.getNodeName(), item.getNodeValue());
            }
        }
    }

    public String get(String name) {
        if (params == null) return null;
        return params.get(name);
    }

    public Element toXml(Document builder) {
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
