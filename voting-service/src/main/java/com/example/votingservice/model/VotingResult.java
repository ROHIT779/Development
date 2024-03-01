package com.example.votingservice.model;

import java.util.List;

public class VotingResult {

  private String eventId;

  private List<CandidateResult> finalResult;

  public VotingResult() {}

  public VotingResult(String eventId, List<CandidateResult> finalResult) {
    this.eventId = eventId;
    this.finalResult = finalResult;
  }

  public String getEventId() {
    return eventId;
  }

  public void setEventId(String eventId) {
    this.eventId = eventId;
  }

  public List<CandidateResult> getFinalResult() {
    return finalResult;
  }

  public void setFinalResult(List<CandidateResult> finalResult) {
    this.finalResult = finalResult;
  }
}
