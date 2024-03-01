package com.example.votingvoter.resource;

import com.example.votingvoter.helpers.VoterHelper;
import com.example.votingvoter.jdbc.JDBCManager;
import com.example.votingvoter.model.Candidate;
import com.example.votingvoter.model.EventWithNomination;
import com.example.votingvoter.model.Vote;
import com.example.votingvoter.model.Voter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.sql.SQLException;

@RestController
@RequestMapping("/service/events/{eventId}")
public class VoterResource {

    private final VoterHelper helper;

    @Autowired
    public VoterResource(VoterHelper helper){
        this.helper=helper;
    }

    @PostMapping("/voters")
    public ResponseEntity createVoter(@PathVariable("eventId") String eventId, @RequestBody Voter voter){
        Voter voterReply=helper.createVoter(eventId,voter);
        if(voterReply!=null){
            return new ResponseEntity(voterReply,HttpStatus.OK);
        }else{
            return new ResponseEntity(null, HttpStatus.BAD_REQUEST);
        }
    }
    @PostMapping("/voters/{voterId}/vote")
    public ResponseEntity postVote(@PathVariable("eventId") String eventId, @PathVariable("voterId") String voterId, @RequestBody Vote vote) throws SQLException {
        Voter voter=helper.castVote(eventId,voterId,vote);
        if(voter!=null){
            return new ResponseEntity(voter, HttpStatus.OK);
        }else{
            return new ResponseEntity(null, HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("/voters/{voterId}")
    public ResponseEntity getVoter(@PathVariable("eventId") String eventId, @PathVariable("voterId") String voterId){
        Voter voter=helper.getVoter(eventId,voterId);
        if(voter!=null){
            return new ResponseEntity(voter,HttpStatus.OK);
        }else{
            return new ResponseEntity(null,HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("/")
    public ResponseEntity getEventDetails(@PathVariable("eventId") String eventId){
        EventWithNomination eventWithNomination = helper.getEventDetails(eventId);
        if(eventWithNomination!=null){
            return new ResponseEntity(eventWithNomination,HttpStatus.OK);
        }else{
            return new ResponseEntity(null,HttpStatus.BAD_REQUEST);
        }
    }
}
