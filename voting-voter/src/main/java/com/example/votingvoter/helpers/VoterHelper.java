package com.example.votingvoter.helpers;

import com.example.votingvoter.generator.IDGenerator;
import com.example.votingvoter.jdbc.JDBCManager;
import com.example.votingvoter.model.EventWithNomination;
import com.example.votingvoter.model.Vote;
import com.example.votingvoter.model.Voter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.client.RestTemplate;

import java.sql.SQLException;

@Component
public class VoterHelper {

    @Autowired
    private RestTemplate restTemplate;

    private JDBCManager jdbcManager;

    @Autowired
    public VoterHelper(JDBCManager jdbcManager){
        this.jdbcManager=jdbcManager;
    }

    public Voter createVoter(String eventId, Voter voter){
        if(jdbcManager.validateId(eventId,null,null)){
            voter.setVoterId(IDGenerator.getRandomId());
            voter.setEventId(eventId);
            jdbcManager.addVoter(voter);
            return voter;
        }else{
            return null;
        }
    }

    public Voter castVote(String eventId, String voterId, Vote vote) throws SQLException {
        if(jdbcManager.validateId(eventId,voterId,null) && jdbcManager.validateId(eventId,null,vote.getCandidateId())) {
            return jdbcManager.postVote(voterId,eventId,vote);
        }else{
            return null;
        }
    }

    public Voter getVoter(String eventId, String voterId){
        if(jdbcManager.validateId(eventId,voterId,null)) {
            return jdbcManager.getVoter(voterId);
        }else{
            return null;
        }
    }

    public EventWithNomination getEventDetails(String eventId){
        EventWithNomination eventWithNomination = null;
        if(jdbcManager.validateId(eventId,null,null)){
            String creatorId = jdbcManager.getCreatorFromEvent(eventId);
            eventWithNomination = restTemplate.getForObject("http://localhost:8081/service/creators/"+creatorId+"/events/"+eventId+"/nominations", EventWithNomination.class);
        }
        return eventWithNomination;
    }
}
