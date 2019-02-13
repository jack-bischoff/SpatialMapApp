package cmsc420.meeshquest.part1;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;

import org.w3c.dom.*;
import org.xml.sax.SAXException;

import cmsc420.xml.XmlUtility;

public class MeeshQuest {

    public static void main(String[] args) {
    	Document results = null;
    	Middleware mw;
        try {
        	Document doc = XmlUtility.validateNoNamespace(System.in);
        	results = XmlUtility.getDocumentBuilder().newDocument();
        	mw = new Middleware(results);
        	Element Root = results.createElement("results");
        	Element commandNode = doc.getDocumentElement();

        	final NodeList nl = commandNode.getChildNodes();
        	for (int i = 0; i < nl.getLength(); i++) {
        		if (nl.item(i).getNodeType() == Document.ELEMENT_NODE) {
        			commandNode = (Element) nl.item(i);
        			String commandName = commandNode.getNodeName();
					NamedNodeMap attrs = commandNode.getAttributes();
					Parameters params;
					Element Status = null;

        			switch (commandName) {
						case "createCity":
							params = new Parameters(attrs, new String[]{"name", "x", "y", "radius", "color"});
							Status = mw.createCity(params);
							break;
						case "listCities":
							params = new Parameters(attrs, new String[]{"sortBy"});
							Status = mw.listCities(params);
							break;
						case "clearAll":
							params = new Parameters(null, null);
							Status = mw.clearAll(params);
							break;
						default:
							Status = results.createElement("undefinedError");

					}
					Root.appendChild(Status);
        		}

        	}
        	results.appendChild(Root);
        } catch (SAXException | IOException | ParserConfigurationException e) {
			try {
				results = XmlUtility.getDocumentBuilder().newDocument();
				results.appendChild(results.createElement("fatalError"));
			} catch (Exception err) {
				err.printStackTrace();
			}
		} finally {
            try {
				XmlUtility.print(results);
			} catch (TransformerException e) {
				e.printStackTrace();
			}
        }
    }
}
