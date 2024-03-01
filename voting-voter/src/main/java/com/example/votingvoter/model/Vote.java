package com.example.votingvoter.model;

public class Vote {

  private String candidateId;

  public Vote() {}

  public Vote(String candidateId) {
    this.candidateId = candidateId;
  }

  public String getCandidateId() {
    return candidateId;
  }

  public void setCandidateId(String candidateId) {
    this.candidateId = candidateId;
  }
}
