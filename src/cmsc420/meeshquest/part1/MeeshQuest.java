package cmsc420.meeshquest.part1;

import java.io.IOException;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;

import cmsc420.meeshquest.part1.DataObject.Parameters;
import cmsc420.meeshquest.part1.DataObject.Result;
import cmsc420.meeshquest.part1.Errors.Failure;
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
        	mw = new CommandMiddleware(results);
        	Element Root = results.createElement("results");
        	Element commandNode = doc.getDocumentElement();

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
                        default:
                            params = new Parameters();
                            Output = new Result(results.createElement("undefinedError"));
                    }
                    Status = Output.toXml();
                    Output.prepend(Command);
                    Output.prepend(params.toXml());

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
