package com.example.votingservice.helpers;

import com.example.votingservice.jdbc.JDBCManager;
import com.example.votingservice.model.EventWithNomination;
import com.example.votingservice.model.VotingResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

@Component
public class VotingServiceHelper {

  //    @Autowired
  //    private RestTemplate restTemplate;

  @Autowired private WebClient.Builder webClientBuilder;
  private JDBCManager jdbcManager;

  @Autowired
  public VotingServiceHelper(JDBCManager jdbcManager) {
    this.jdbcManager = jdbcManager;
  }

  public EventWithNomination getEvent(String eventId) {
    if (jdbcManager.validateId("event_id", eventId)) {
      String creatorId = jdbcManager.getCreatorFromEvent(eventId);
      // EventWithNomination eventWithNomination =
      // restTemplate.getForObject("http://localhost:8081/service/creators/"+creatorId+"/events/"+eventId+"/nominations", EventWithNomination.class);
      EventWithNomination eventWithNomination =
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
      return eventWithNomination;
    } else {
      return null;
    }
  }

  public VotingResult getResult(String eventId) {
    if (jdbcManager.isEventLocked(eventId)) {
      if (jdbcManager.validateId("event_id", eventId)) {
        return jdbcManager.getResult(eventId);
      } else {
        return null;
      }
    } else {
      return null;
    }
  }

  public String deleteAllData() {
    return jdbcManager.deleteAllData();
  }
}
