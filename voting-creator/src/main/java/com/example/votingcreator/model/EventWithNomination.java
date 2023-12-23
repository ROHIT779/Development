package com.example.votingcreator.model;

import java.util.List;

public class EventWithNomination {

    private String eventId;
    private String eventName;
    private String eventInfo;
    private String creatorId;

    private List<Candidate> candidateList;
    public EventWithNomination() {
    }

    public String getCreatorId() {
        return creatorId;
    }

    public void setCreatorId(String creatorId) {
        this.creatorId = creatorId;
    }

    public List<Candidate> getCandidateList() {
        return candidateList;
    }

    public void setCandidateList(List<Candidate> candidateList) {
        this.candidateList = candidateList;
    }

    public EventWithNomination(String eventId, String eventName, String eventInfo, String creatorId, List<Candidate> candidateList) {
        this.eventId = eventId;
        this.eventName = eventName;
        this.eventInfo = eventInfo;
        this.creatorId = creatorId;
        this.candidateList = candidateList;
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
