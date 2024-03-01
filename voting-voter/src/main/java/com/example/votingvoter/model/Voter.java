package com.example.votingvoter.model;

public class Voter {
  private String voterId;
  private String voterName;
  private Vote vote;

  private String eventId;

  public Voter() {}

  public Voter(String voterId, String voterName, String eventId, Vote vote) {
    this.voterId = voterId;
    this.voterName = voterName;
    this.eventId = eventId;
    this.vote = vote;
  }

  public String getVoterId() {
    return voterId;
  }

  public void setVoterId(String voterId) {
    this.voterId = voterId;
  }

  public String getVoterName() {
    return voterName;
  }

  public void setVoterName(String voterName) {
    this.voterName = voterName;
  }

  public Vote getVote() {
    return vote;
  }

  public void setVote(Vote vote) {
    this.vote = vote;
  }

  public String getEventId() {
    return eventId;
  }

  public void setEventId(String eventId) {
    this.eventId = eventId;
  }
}
