package toolcloud.tum.toolcloudapp.xml;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import toolcloud.tum.toolcloudapp.model.Event;

/**
 * Created by IBM on 28-Dec-14.
 */
public class EventXMLHandler extends DefaultHandler {
    Boolean currentEvent = false;
    String currentValue = "";
    Event event = null;
    private Set<Event> eventsList = new HashSet<Event>();

    public ArrayList<Event> getEventsList() {
        return    new ArrayList<Event>(eventsList);
    }

    // Called when tag starts
    @Override
    public void startElement(String uri, String localName, String qName,
                             Attributes attributes) throws SAXException {

        currentEvent = true;
        currentValue = "";
        if (localName.equals("AggregationEvent")) {
            event = new Event();
        }

    }

    // Called when tag closing
    @Override
    public void endElement(String uri, String localName, String qName)
            throws SAXException {

        currentEvent = false;

        /** set value */


        if (localName.equalsIgnoreCase("eventTime")) {
            event.setEventTime(currentValue);
        } else if (localName.equalsIgnoreCase("recordTime")) {
            event.setRecordTime(currentValue);
        }else if (localName.equalsIgnoreCase("eventTimeZoneOffset")) {
            event.setEventTimeZoneOffset(currentValue);
        }
        else if (localName.equalsIgnoreCase("parentID")) {
            event.setParentID(currentValue);
        }
        else if (localName.equalsIgnoreCase("action")) {
            event.setAction(currentValue);
        }
        else if (localName.equalsIgnoreCase("readPoint")) {
            event.setReadPoint(currentValue);
        }
        else if (localName.equalsIgnoreCase("epc"))
            event.getChildEPCs().add(currentValue);
        else if (localName.equalsIgnoreCase("AggregationEvent"))
        {
            if (event.getChildEPCs().size()>1) {
                for (int i = 0; i < event.getChildEPCs().size() - 1; i++) {
                    // will create same event with no epcs
                    Event newEvent = new Event(event);
                    newEvent.getChildEPCs().add(event.getChildEPCs().get(i));
//                    event.getChildEPCs().remove(i);
                    eventsList.add(newEvent);
                }

                String lastEPC = event.getChildEPCs().get(event.getChildEPCs().size() - 1);
                event.getChildEPCs().clear();
                event.getChildEPCs().add(lastEPC);
            }
            eventsList.add(event);

        }





    }

    // Called to get tag characters
    @Override
    public void characters(char[] ch, int start, int length)
            throws SAXException {

        if (currentEvent) {
            currentValue = currentValue +  new String(ch, start, length);
        }

    }
}
