package com.example.votingvoter.helpers;

import com.example.votingvoter.jdbc.JDBCManager;
import com.example.votingvoter.model.EventWithNomination;
import com.example.votingvoter.model.Vote;
import com.example.votingvoter.model.Voter;
import java.sql.SQLException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

@Component
public class VoterHelper {

  //    @Autowired
  //    private RestTemplate restTemplate;

  @Autowired private WebClient.Builder webClientBuilder;

  private JDBCManager jdbcManager;

  @Autowired
  public VoterHelper(JDBCManager jdbcManager) {
    this.jdbcManager = jdbcManager;
  }

  public Voter createVoter(String eventId, Voter voter) {
    if(!jdbcManager.isEventLocked(eventId)){
      if (jdbcManager.validateId(eventId, null, null)) {
        voter.setEventId(eventId);
        voter.setVoterId(jdbcManager.addVoter(voter));
        return voter;
      } else {
        return null;
      }
    }else{
      return null;
    }
  }

  public Voter castVote(String eventId, String voterId, Vote vote) throws SQLException {
    if (!jdbcManager.isEventLocked(eventId)) {
      if (jdbcManager.validateId(eventId, voterId, null)
              && jdbcManager.validateId(eventId, null, vote.getCandidateId())) {
        return jdbcManager.postVote(voterId, eventId, vote);
      } else {
        return null;
      }
    }else{
      return null;
    }
  }

  public Voter getVoter(String eventId, String voterId) {
    if (jdbcManager.validateId(eventId, voterId, null)) {
      return jdbcManager.getVoter(voterId);
    } else {
      return null;
    }
  }

  public EventWithNomination getEventDetails(String eventId) {
    EventWithNomination eventWithNomination = null;
    if (jdbcManager.validateId(eventId, null, null)) {
      String creatorId = jdbcManager.getCreatorFromEvent(eventId);
      // eventWithNomination =
      // restTemplate.getForObject("http://localhost:8081/service/creators/"+creatorId+"/events/"+eventId+"/nominations", EventWithNomination.class);
      eventWithNomination =
          webClientBuilder
              .build()
              .get()
              .uri(
                  "http://localhost:8081/service/creators/"
                      + creatorId
                      + "/events/"
                      + eventId
                      + "/nominations")
              .retrieve()
              .bodyToMono(EventWithNomination.class)
              .block();
    }
    return eventWithNomination;
  }
}
