package com.example.votingvoter.model;

public class Candidate {
    private String candidateId;
    private String candidateName;
    private String candidateInfo;

    public Candidate() {
    }

    public Candidate(String candidateId, String candidateName, String candidateInfo) {
        this.candidateId = candidateId;
        this.candidateName = candidateName;
        this.candidateInfo = candidateInfo;
    }

    public String getCandidateId() {
        return candidateId;
    }

    public void setCandidateId(String candidateId) {
        this.candidateId = candidateId;
    }

    public String getCandidateName() {
        return candidateName;
    }

    public void setCandidateName(String candidateName) {
        this.candidateName = candidateName;
    }

    public String getCandidateInfo() {
        return candidateInfo;
    }

    public void setCandidateInfo(String candidateInfo) {
        this.candidateInfo = candidateInfo;
    }
}
