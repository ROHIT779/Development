package com.example.votingservice.model;

import java.util.List;

public class Nomination {

  private String eventId;
  private List<Candidate> candidateList;

  public String getEventId() {
    return eventId;
  }

  public void setEventId(String eventId) {
    this.eventId = eventId;
  }

  public Nomination() {}

  public Nomination(String eventId, List<Candidate> candidateList) {
    this.eventId = eventId;
    this.candidateList = candidateList;
  }

  public List<Candidate> getCandidateList() {
    return candidateList;
  }

  public void setCandidateList(List<Candidate> candidateList) {
    this.candidateList = candidateList;
  }
}
