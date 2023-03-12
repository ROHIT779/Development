package com.example.votingvoter.resource;

import com.example.votingvoter.generator.IDGenerator;
import com.example.votingvoter.jdbc.JDBCManager;
import com.example.votingvoter.model.Candidate;
import com.example.votingvoter.model.Vote;
import com.example.votingvoter.model.Voter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.sql.SQLException;

@RestController
@RequestMapping("/event/{eventId}/voter")
public class VoterResource {

    @Value("${database.url}")
    private String databaseUrl;

    @Value("${database.name}")
    private String databaseName;

    @Value("${database.username}")
    private String userName;

    @Value("${database.password}")
    private String password;

    @PostMapping("/")
    public ResponseEntity createVoter(@PathVariable("eventId") String eventId, @RequestBody Voter voter){
        JDBCManager jdbcManager = new JDBCManager(databaseUrl,databaseName,userName,password);
        if(jdbcManager.validateId("event_id",eventId)){
            voter.setVoterId(IDGenerator.getRandomId());
            voter.setEventId(eventId);
            jdbcManager.addVoter(voter);
            return new ResponseEntity(voter, HttpStatus.CREATED);
        }else{
            return new ResponseEntity(null, HttpStatus.BAD_REQUEST);
        }
    }
    @PostMapping("/{voterId}/vote")
    public ResponseEntity postVote(@PathVariable("eventId") String eventId, @PathVariable("voterId") String voterId, @RequestBody Vote vote) throws SQLException {
        JDBCManager jdbcManager = new JDBCManager(databaseUrl,databaseName,userName,password);
        if(jdbcManager.validateId("event_id",eventId) && jdbcManager.validateId("voter_id",voterId) && jdbcManager.validateId("candidate_id",vote.getCandidateId())) {
            return new ResponseEntity(jdbcManager.postVote(voterId, eventId, vote), HttpStatus.CREATED);
        }else{
            return new ResponseEntity(null,HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("/{voterId}")
    public ResponseEntity getVoter(@PathVariable("eventId") String eventId, @PathVariable("voterId") String voterId){
        JDBCManager jdbcManager = new JDBCManager(databaseUrl,databaseName,userName,password);
        if(jdbcManager.validateId("event_id",eventId) && jdbcManager.validateId("voter_id",voterId)) {
            return new ResponseEntity(jdbcManager.getVoter(voterId),HttpStatus.OK);
        }else{
            return new ResponseEntity(null,HttpStatus.BAD_REQUEST);
        }
    }
}
