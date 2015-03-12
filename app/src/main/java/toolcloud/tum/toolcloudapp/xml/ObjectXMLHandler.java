package toolcloud.tum.toolcloudapp.xml;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;


import toolcloud.tum.toolcloudapp.model.ToolCloudObject;

/**
 * Created by IBM on 28-Dec-14.
 */
public class ObjectXMLHandler extends DefaultHandler {
    Boolean currentEvent = false;
    String currentValue = "";
    ToolCloudObject toolCloudObject = null;

    // Called when tag starts
    @Override
    public void startElement(String uri, String localName, String qName,
                             Attributes attributes) throws SAXException {

        currentEvent = true;
        currentValue = "";
        if (localName.equals("Object")) {
            toolCloudObject = new ToolCloudObject();
        }

    }

    // Called when tag closing
    @Override
    public void endElement(String uri, String localName, String qName)
            throws SAXException {

        currentEvent = false;

        /** set value */

        if (localName.equalsIgnoreCase("name")) {
            toolCloudObject.setName(currentValue);
        } else if (localName.equalsIgnoreCase("type")) {
            toolCloudObject.setType(currentValue);
        } else if (localName.equalsIgnoreCase("id")) {
            toolCloudObject.setId(currentValue);
        } else if (localName.equalsIgnoreCase("aggregatedAsChild")) {
            toolCloudObject.setAggregatedAsChild((currentValue.equals("true")? true:false));
        } else if (localName.equalsIgnoreCase("aggregatedAsParent")) {
            toolCloudObject.setAggregatedAsParent((currentValue.equals("true")? true:false));
        }

    }

    // Called to get tag characters
    @Override
    public void characters(char[] ch, int start, int length)
            throws SAXException {

        if (currentEvent) {
            currentValue = currentValue + new String(ch, start, length);
        }

    }

    public ToolCloudObject getToolCloudObject() {
        return toolCloudObject;
    }
}
