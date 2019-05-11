package cmsc420.meeshquest.part2;

import java.io.File;
import java.io.IOException;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;

import cmsc420.meeshquest.part2.DataObject.Command;
import cmsc420.meeshquest.part2.DataObject.Parameters;
import cmsc420.meeshquest.part2.DataObject.Result;
import org.w3c.dom.*;
import org.xml.sax.SAXException;

import cmsc420.xml.XmlUtility;

public class MeeshQuest {

    public static void main(String[] args) {
    	Document results = null;
    	CommandMiddleware mw;
        try {
            Document doc = XmlUtility.validateNoNamespace(new File("./xmltests/test2.xml"));
//        	Document doc = XmlUtility.validateNoNamespace(System.in);
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

					NamedNodeMap attrs = commandNode.getAttributes();
					Parameters params = new Parameters();
					Command command = new Command(commandNode.getNodeName(), commandNode.getAttribute("id"));
                    Result Output;
					Element Status;

                    switch (command.getName()) {
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
                        case "printTreap":
                            Output = mw.printTreap();
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
                        case "mapRoad":
                            params = new Parameters(attrs, new String[]{"start", "end"});
                            Output = mw.mapRoad(params);
                            break;
                        case "printPMQuadtree":
                            Output = mw.printPMQuadTree();
                            break;
                        case "saveMap":
                            params = new Parameters(attrs, new String[]{"name"});
                            Output = mw.saveMap(params);
                            break;
                        case "rangeCities":
                            params = new Parameters(attrs, new String[]{"x", "y", "radius", "saveMap"});
                            Output = mw.rangeCities(params);
                            break;
                        case "rangeRoads":
                            params = new Parameters(attrs, new String[]{"x", "y", "radius", "saveMap"});
                            Output = mw.rangeRoads(params);
                            break;
                        case "nearestCity":
                            params = new Parameters(attrs, new String[]{"x", "y"});
                            Output = mw.nearestCity(params);
                            break;
                        case "nearestIsolatedCity":
                            params = new Parameters(attrs, new String[]{"x", "y"});
                            Output = mw.nearestIsolatedCity(params);
                            break;
                        case "nearestRoad":
                            params = new Parameters(attrs, new String[]{"x", "y"});
                            Output = mw.nearestRoad(params);
                            break;
                        case "nearestCityToRoad":
                            params = new Parameters(attrs, new String[]{"start", "end"});
                            Output = mw.nearestCityToRoad(params);
                            break;
                        case "shortestPath":
                            params = new Parameters(attrs, new String[]{"start", "end", "saveMap", "saveHTML"});
                            Output = mw.shortestPath(params);
                            break;
                        default:
                            Output = new Result(results.createElement(Fault.commandError.toString()));

                    }

                    //Think of this like building the xml bottom up -- a stack almost.
                    Status = Output.toXml(); // Create wrapper xml with possible output tag & output
                    Status.insertBefore(params.toXml(), Status.getFirstChild()); //Insert parameters before output tag
                    Status.insertBefore(command.toXml(), Status.getFirstChild()); // Insert command before parameters
                    Root.appendChild(Status); // append total command result to root of xml
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
