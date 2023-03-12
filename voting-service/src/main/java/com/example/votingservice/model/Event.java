package com.example.votingservice.model;

public class Event {
    private String eventId;
    private String eventName;
    private String eventInfo;
    private String creatorId;
    public Event() {
    }

    public String getCreatorId() {
        return creatorId;
    }

    public void setCreatorId(String creatorId) {
        this.creatorId = creatorId;
    }

    public Event(String eventId, String eventName, String eventInfo, String creatorId) {
        this.eventId = eventId;
        this.eventName = eventName;
        this.eventInfo = eventInfo;
        this.creatorId = creatorId;
    }

    public String getEventId() {
        return eventId;
    }

    public void setEventId(String eventId) {
        this.eventId = eventId;
    }

    public String getEventName() {
        return eventName;
    }

    public void setEventName(String eventName) {
        this.eventName = eventName;
    }

    public String getEventInfo() {
        return eventInfo;
    }

    public void setEventInfo(String eventInfo) {
        this.eventInfo = eventInfo;
    }
}
