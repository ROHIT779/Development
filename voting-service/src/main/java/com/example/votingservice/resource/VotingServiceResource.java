package com.example.votingservice.resource;

import com.example.votingservice.helpers.VotingServiceHelper;
import com.example.votingservice.model.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/service/voting")
public class VotingServiceResource {
  private final VotingServiceHelper helper;

  @Autowired
  public VotingServiceResource(VotingServiceHelper helper) {
    this.helper = helper;
  }

  @GetMapping("/events/{eventId}")
  public ResponseEntity getEvent(@PathVariable("eventId") String eventId) {
    EventWithNomination eventWithNomination = helper.getEvent(eventId);
    if (eventWithNomination != null) {
      return new ResponseEntity(eventWithNomination, HttpStatus.OK);
    } else {
      return new ResponseEntity(null, HttpStatus.BAD_REQUEST);
    }
  }

  @GetMapping("/events/{eventId}/result")
  public ResponseEntity getResult(@PathVariable("eventId") String eventId) {
    VotingResult result = helper.getResult(eventId);
    if (result != null) {
      return new ResponseEntity(result, HttpStatus.OK);
    } else {
      return new ResponseEntity(null, HttpStatus.BAD_REQUEST);
    }
  }

  @DeleteMapping("/delete-all")
  public ResponseEntity deleteAllData() {
    return new ResponseEntity<>(helper.deleteAllData(), HttpStatus.OK);
  }
}
