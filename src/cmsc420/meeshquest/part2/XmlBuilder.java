package cmsc420.meeshquest.part2;

import cmsc420.xml.XmlUtility;
import org.w3c.dom.Document;
import javax.xml.parsers.ParserConfigurationException;

 class XmlBuilder {
    private static Document builder;
    static {
        try {
            builder = XmlUtility.getDocumentBuilder().newDocument();
        } catch (ParserConfigurationException e) {
            throw new ExceptionInInitializerError(e);
        }
    }

    static Document getBuilder() {
         return builder;
    }
}

