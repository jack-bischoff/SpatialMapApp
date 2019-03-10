package cmsc420.meeshquest.part1;

import java.io.File;
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
//            Document doc = XmlUtility.validateNoNamespace(new File("./xmltest/part1.input.xml"));
        	Document doc = XmlUtility.validateNoNamespace(System.in);
            results = XmlBuilder.getBuilder();
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
					Parameters params = new Parameters();
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
                        case "printPRQuadtree":
                            Output = mw.printPRQuadTree();
                            break;
                        case "saveMap":
                            params = new Parameters(attrs, new String[]{"name"});
                            Output = mw.saveMap(params);
                            break;
                        case "rangeCities":
                            //figure out optional param saveMap
                            params = new Parameters(attrs, new String[]{"x", "y", "radius", "saveMap"});
                            Output = mw.rangeCities(params);
                            break;
                        case "nearestCity":
                            params = new Parameters(attrs, new String[]{"x", "y"});
                            Output = mw.nearestCity(params);
                            break;
                        default:
                            Output = new Result(results.createElement("undefinedError"));

                    }

                    Status = Output.toXml();
                    Status.insertBefore(params.toXml(), Status.getFirstChild());
                    Status.insertBefore(Command, Status.getFirstChild());
                    Root.appendChild(Status);
        		}

        	}
        	results.appendChild(Root);
        } catch (SAXException | IOException | ExceptionInInitializerError | ParserConfigurationException e) {
			try {
				results = XmlUtility.getDocumentBuilder().newDocument();
				results.appendChild(results.createElement("fatalError"));
			} catch (Exception err) {
				err.printStackTrace();
			}
		} finally {
            try {
                if (VisualMap.isInitalized()) VisualMap.VisualMap().dispose();
				XmlUtility.print(results);
			} catch (TransformerException e) {
				e.printStackTrace();
			}
        }
    }
}
