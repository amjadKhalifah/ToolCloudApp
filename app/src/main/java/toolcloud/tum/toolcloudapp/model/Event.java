package toolcloud.tum.toolcloudapp.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by IBM on 27-Dec-14.
 */
public class Event {

    private String eventTime;
    private String recordTime;
    private String eventTimeZoneOffset;
    private String parentID;
    private List<String> childEPCs;
    private String action;
    private String readPoint;

    public Event(){
        childEPCs = new ArrayList<>();

    }

    public Event(String eventTime, String recordTime, String eventTimeZoneOffset, String parentID, String action, String readPoint) {
        this.eventTime = eventTime;
        this.recordTime = recordTime;
        this.eventTimeZoneOffset = eventTimeZoneOffset;
        this.parentID = parentID;
//        this.childEPCs = childEPCs;
        this.action = action;
        this.readPoint = readPoint;
    }

    public Event(Event event) {
        this(event.getEventTime(),event.getRecordTime(),event.getEventTimeZoneOffset(),event.getParentID(),event.getAction(),event.getReadPoint());
        childEPCs = new ArrayList<>();
    }

    public String getEventTime() {
        return eventTime;
    }

    public void setEventTime(String eventTime) {
        this.eventTime = eventTime;
    }

    public String getRecordTime() {
        return recordTime;
    }

    public void setRecordTime(String recordTime) {
        this.recordTime = recordTime;
    }

    public String getEventTimeZoneOffset() {
        return eventTimeZoneOffset;
    }

    public void setEventTimeZoneOffset(String eventTimeZoneOffset) {
        this.eventTimeZoneOffset = eventTimeZoneOffset;
    }

    public String getParentID() {
        return parentID;
    }

    public void setParentID(String parentID) {
        this.parentID = parentID;
    }

    public List<String> getChildEPCs() {
        return childEPCs;
    }

    public void setChildEPCs(List<String> childEPCs) {
        this.childEPCs = childEPCs;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public String getReadPoint() {
        return readPoint;
    }

    public void setReadPoint(String readPoint) {
        this.readPoint = readPoint;
    }





    @Override
    public String toString() {
        return "Event{" +
//                "eventTime='" + eventTime + '\'' +
                ", recordTime='" + recordTime + '\'' +
//                ", eventTimeZoneOffset='" + eventTimeZoneOffset + '\'' +
//                ", parentID='" + parentID + '\'' +
                ", childEPCs=" + childEPCs +
//                ", action='" + action + '\'' +
//                ", readPoint='" + readPoint + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Event)) return false;

        Event event = (Event) o;

        if (!childEPCs.equals(event.childEPCs)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return childEPCs.hashCode();
    }
}
