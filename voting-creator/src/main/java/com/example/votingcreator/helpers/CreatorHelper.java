package com.example.votingcreator.helpers;

import com.example.votingcreator.jdbc.JDBCManager;
import com.example.votingcreator.model.*;
import java.util.Iterator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class CreatorHelper {
  private JDBCManager jdbcManager;

  @Autowired
  public CreatorHelper(JDBCManager jdbcManager) {
    this.jdbcManager = jdbcManager;
  }

  public Creator createCreator(Creator creator) {
    creator.setCreatorId(jdbcManager.addCreator(creator));
    return creator;
  }

  public Creator getCreator(String creatorId) {
    if (jdbcManager.validateId(creatorId, null)) {
      return jdbcManager.getCreator(creatorId);
    }
    return null;
  }

  public Event createEvent(String creatorId, Event event) {
    event.setCreatorId(creatorId);
    if (jdbcManager.validateId(creatorId, null)) {
      event.setEventId(jdbcManager.addEvent(event));
      return event;
    }
    return null;
  }

  public Event getEvent(String creatorId, String eventId) {
    if (jdbcManager.validateId(creatorId, eventId)) {
      return jdbcManager.getEvent(creatorId, eventId);
    }
    return null;
  }

  public Nomination createNomination(String creatorId, String eventId, Nomination nomination) {
    System.out.println(creatorId);
    System.out.println(eventId);
    nomination.setEventId(eventId);
    if(!jdbcManager.isEventLocked(eventId)){
      if (jdbcManager.validateId(creatorId, eventId)) {
        Iterator<String> candidateIdsIterator =
                jdbcManager.addNomination(creatorId, nomination).iterator();
        for (Candidate candidate : nomination.getCandidateList()) {
          if (candidateIdsIterator.hasNext()) {
            candidate.setCandidateId(candidateIdsIterator.next());
          }
        }
        return nomination;
      } else {
        return null;
      }
    }else{
      return null;
    }
  }

  public EventWithNomination getNomination(String creatorId, String eventId) {
    System.out.println(creatorId);
    System.out.println(eventId);
    if (jdbcManager.validateId(creatorId, eventId)) {
      EventWithNomination eventWithNomination = jdbcManager.getNominations(eventId);
      return eventWithNomination;
    } else {
      return null;
    }
  }

  public boolean updateEvent(String creatorId, String eventId, Event event){
    boolean eventLocked = false;
    if (jdbcManager.validateId(creatorId, eventId)) {
      System.out.println("Event in PUT request: "+event);
      if(event.getEventName().isEmpty() && event.getEventInfo().isEmpty()){
        eventLocked = jdbcManager.lockEvent(eventId);
      }
    }
    return eventLocked;
  }
}
