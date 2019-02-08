package cmsc420.meeshquest.part1;

import java.io.IOException;
import java.util.Map;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;

import org.w3c.dom.*;
import org.xml.sax.SAXException;

import cmsc420.xml.XmlUtility;

public class MeeshQuest {
	private static parseAttrs(Element cmd) {
		NamedNodeMap attrs = cmd.getAttributes();
		for (int i = 0; i < attrs.getLength(); i++) {
			Node item = attrs.item(i);

		}
	}
    public static void main(String[] args) {
    	CitiesLookup cities = new CitiesLookup();
    	Document results = null;
    	
        try {
        	Document doc = XmlUtility.validateNoNamespace(System.in);
        	results = XmlUtility.getDocumentBuilder().newDocument();
        
        	Element commandNode = doc.getDocumentElement();

        	final NodeList nl = commandNode.getChildNodes();
        	for (int i = 0; i < nl.getLength(); i++) {
        		if (nl.item(i).getNodeType() == Document.ELEMENT_NODE) {
        			commandNode = (Element) nl.item(i);
        			switch (commandNode.getNodeName()) {
						case "createCity":
							NamedNodeMap attrs = commandNode.getAttributes();
							CitiesLookup.createCity(attrs);
							break;
						case "listAllCities":

							break;
						case "clearAll":

							break;
					}
                
        			/* TODO: Process your commandNode here */
        		}
        	}
        } catch (SAXException | IOException | ParserConfigurationException e) {
        	
        	/* TODO: Process fatal error here */
        	
		} finally {
            try {
				XmlUtility.print(results);
			} catch (TransformerException e) {
				e.printStackTrace();
			}
        }
    }
}
