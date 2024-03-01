package com.example.votingservice.model;

public class CandidateResult {

  private Candidate candidate;
  private int count;

  public CandidateResult() {}

  public CandidateResult(int count, Candidate candidate) {
    this.candidate = candidate;
    this.count = count;
  }

  public Candidate getCandidate() {
    return candidate;
  }

  public void setCandidate(Candidate candidate) {
    this.candidate = candidate;
  }

  public int getCount() {
    return count;
  }

  public void setCount(int count) {
    this.count = count;
  }
}
