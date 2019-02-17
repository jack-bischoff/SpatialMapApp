package cmsc420.meeshquest.part1;

import org.w3c.dom.*;
import cmsc420.xml.XmlUtility;

import javax.xml.parsers.ParserConfigurationException;

public interface xmlify {
    default Document getBuilder() {
        Document builder;
        try {
            builder = XmlUtility.getDocumentBuilder().newDocument();
        } catch (ParserConfigurationException e) {
            System.out.println("Fatal Error : ParserConfigurationException");
            System.exit(1);
        }
        return builder;
    }
   Element toXml();

}
