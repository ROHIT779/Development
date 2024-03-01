package com.example.votingservice.model;

public class Creator {
  private String creatorId;
  private String creatorName;
  private String creatorInfo;

  public Creator() {}

  public Creator(String creatorId, String creatorName, String creatorInfo) {
    this.creatorId = creatorId;
    this.creatorName = creatorName;
    this.creatorInfo = creatorInfo;
  }

  public String getCreatorId() {
    return creatorId;
  }

  public void setCreatorId(String creatorId) {
    this.creatorId = creatorId;
  }

  public String getCreatorName() {
    return creatorName;
  }

  public void setCreatorName(String creatorName) {
    this.creatorName = creatorName;
  }

  public String getCreatorInfo() {
    return creatorInfo;
  }

  public void setCreatorInfo(String creatorInfo) {
    this.creatorInfo = creatorInfo;
  }
}
