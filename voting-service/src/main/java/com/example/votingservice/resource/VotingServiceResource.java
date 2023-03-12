package com.example.votingservice.resource;

import com.example.votingservice.jdbc.JDBCManager;
import com.example.votingservice.model.Candidate;
import com.example.votingservice.model.CandidateResult;
import com.example.votingservice.model.EventWithNomination;
import com.example.votingservice.model.VotingResult;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/voting")
public class VotingServiceResource {

    @Value("${database.url}")
    private String databaseUrl;

    @Value("${database.name}")
    private String databaseName;

    @Value("${database.username}")
    private String userName;

    @Value("${database.password}")
    private String password;

    @GetMapping("/event/{eventId}")
    public ResponseEntity getEvent(@PathVariable("eventId") String eventId){
        JDBCManager jdbcManager = new JDBCManager(databaseUrl,databaseName,userName,password);
        if(jdbcManager.validateId("event_id",eventId)){
            return new ResponseEntity<>(jdbcManager.getEvent(eventId), HttpStatus.OK);
        }else{
            return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("/event/{eventId}/result")
    public ResponseEntity getResult(@PathVariable("eventId") String eventId){
        JDBCManager jdbcManager = new JDBCManager(databaseUrl,databaseName,userName,password);
        if(jdbcManager.validateId("event_id",eventId)){
            return new ResponseEntity<>(jdbcManager.getResult(eventId), HttpStatus.OK);
        }else{
            return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        }
    }
}
