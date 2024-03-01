package com.example.votingcreator.model;

import java.util.ArrayList;
import java.util.List;

public class Creator {
  private String creatorId;
  private String creatorName;
  private String creatorInfo;
  private List<EventWithNomination> eventWithNominationList;

  public Creator() {}

  public Creator(String creatorId, String creatorName, String creatorInfo) {
    this.creatorId = creatorId;
    this.creatorName = creatorName;
    this.creatorInfo = creatorInfo;
    this.eventWithNominationList = new ArrayList<>();
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

  public List<EventWithNomination> getEventWithNominationList() {
    return eventWithNominationList;
  }

  public void setEventWithNominationList(List<EventWithNomination> eventWithNominationList) {
    this.eventWithNominationList = eventWithNominationList;
  }

  public void addEventWithNomination(EventWithNomination eventWithNomination) {
    this.eventWithNominationList.add(eventWithNomination);
  }
}
