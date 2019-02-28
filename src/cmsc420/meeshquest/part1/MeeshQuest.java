package cmsc420.meeshquest.part1;

import java.io.IOException;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;

import cmsc420.meeshquest.part1.DataObject.Parameters;
import cmsc420.meeshquest.part1.DataObject.Result;
import org.w3c.dom.*;
import org.xml.sax.SAXException;

import cmsc420.xml.XmlUtility;

public class MeeshQuest {

    public static void main(String[] args) {
    	Document results = null;
    	CommandMiddleware mw;
        try {
        	Document doc = XmlUtility.validateNoNamespace(System.in);
        	results = XmlUtility.getDocumentBuilder().newDocument();

        	Element Root = results.createElement("results");
        	Element commandNode = doc.getDocumentElement();
            int spatialWidth = Integer.parseInt(commandNode.getAttribute("spatialWidth")),
                    spatialHeight = Integer.parseInt(commandNode.getAttribute("spatialHeight"));
            mw = new CommandMiddleware(results, spatialWidth, spatialHeight);

        	final NodeList nl = commandNode.getChildNodes();
        	for (int i = 0; i < nl.getLength(); i++) {
        		if (nl.item(i).getNodeType() == Document.ELEMENT_NODE) {
        			commandNode = (Element) nl.item(i);
        			String commandName = commandNode.getNodeName();
					NamedNodeMap attrs = commandNode.getAttributes();
					Parameters params = null;
                    Result Output;
					Element Status, Command = results.createElement("command");
					Command.setAttribute("name", commandName);

                    switch (commandName) {
                        case "createCity":
                            params = new Parameters(attrs, new String[]{"name", "x", "y", "radius", "color"});
                            Output = mw.createCity(params);
                            break;
                        case "deleteCity":
                            params = new Parameters(attrs, new String[]{"name"});
                            Output = mw.deleteCity(params);
                            break;
                        case "listCities":
                            params = new Parameters(attrs, new String[]{"sortBy"});
                            Output = mw.listCities(params);
                            break;
                        case "clearAll":
                            params = new Parameters();
                            Output = mw.clearAll();
                            break;
                        case "mapCity":
                            params = new Parameters(attrs, new String[]{"name"});
                            Output = mw.mapCity(params);
                            break;
                        case "unmapCity":
                            params = new Parameters(attrs, new String[]{"name"});
                            Output = mw.unmapCity(params);
                            break;
                        default:
                            params = new Parameters();
                            Output = new Result(results.createElement("undefinedError"));
                    }

                    Status = Output.toXml();
                    Status.insertBefore(params.toXml(), Status.getFirstChild());
                    Status.insertBefore(Command, Status.getFirstChild());
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
