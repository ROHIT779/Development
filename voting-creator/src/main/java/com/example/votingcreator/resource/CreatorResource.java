package com.example.votingcreator.resource;

import com.example.votingcreator.generator.IDGenerator;
import com.example.votingcreator.jdbc.JDBCManager;
import com.example.votingcreator.model.Candidate;
import com.example.votingcreator.model.Creator;
import com.example.votingcreator.model.Event;
import com.example.votingcreator.model.Nomination;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/creator")
public class CreatorResource {

    @Value("${database.url}")
    private String databaseUrl;

    @Value("${database.name}")
    private String databaseName;

    @Value("${database.username}")
    private String userName;

    @Value("${database.password}")
    private String password;
    @PostMapping("/")
    public ResponseEntity createCreator(@RequestBody Creator creator){
        creator.setCreatorId(String.valueOf(IDGenerator.getRandomId()));
        System.out.println("Database Url: "+databaseUrl);
        System.out.println("Database Name: "+databaseName);
        System.out.println("Database Username: "+userName);
        System.out.println("Database Password: "+password);

        JDBCManager jdbcManager = new JDBCManager(databaseUrl, databaseName, userName, password);
        jdbcManager.addCreator(creator);
        return new ResponseEntity<>(creator,HttpStatus.CREATED);
    }
    @GetMapping("/{creatorId}")
    public ResponseEntity getCreator(@PathVariable("creatorId") String creatorId){
        JDBCManager jdbcManager = new JDBCManager(databaseUrl, databaseName, userName, password);
        if(jdbcManager.validateId("creator_id",creatorId)){
            return new ResponseEntity(jdbcManager.getCreator(creatorId), HttpStatus.OK);
        }else{
            return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping("/{creatorId}/event")
    public ResponseEntity createEvent(@PathVariable("creatorId") String creatorId, @RequestBody Event event){
        event.setCreatorId(creatorId);
        event.setEventId(String.valueOf(IDGenerator.getRandomId()));
        JDBCManager jdbcManager = new JDBCManager(databaseUrl, databaseName, userName, password);
        if(jdbcManager.validateId("creator_id",creatorId)){
            jdbcManager.addEvent(event);
            return new ResponseEntity(event,HttpStatus.CREATED);
        }
        return new ResponseEntity(null, HttpStatus.BAD_REQUEST);
    }

    @GetMapping("/{creatorId}/event/{eventId}")
    public ResponseEntity getEvent(@PathVariable("creatorId") String creatorId, @PathVariable("eventId") String eventId){
        JDBCManager jdbcManager = new JDBCManager(databaseUrl, databaseName, userName, password);
        if(jdbcManager.validateId("creator_id",creatorId) && jdbcManager.validateId("event_id",eventId)){
            return new ResponseEntity(jdbcManager.getEvent(creatorId, eventId),HttpStatus.OK);
        }else{
            return new ResponseEntity(null, HttpStatus.BAD_REQUEST);
        }
    }
    @PostMapping("/{creatorId}/event/{eventId}/nomination")
    public ResponseEntity createNomination(@PathVariable("creatorId") String creatorId,
                                           @PathVariable("eventId") String eventId,
                                           @RequestBody Nomination nomination){
        System.out.println(creatorId);
        System.out.println(eventId);
        nomination.setEventId(eventId);
        for(Candidate candidate:nomination.getCandidateList()){
            candidate.setCandidateId(IDGenerator.getRandomId());
        }
        JDBCManager jdbcManager = new JDBCManager(databaseUrl, databaseName, userName, password);
        if(jdbcManager.validateId("creator_id",creatorId) && jdbcManager.validateId("event_id", eventId)){
            jdbcManager.addNomination(creatorId, nomination);
            return new ResponseEntity(nomination, HttpStatus.CREATED);
        }else{
            return new ResponseEntity(null, HttpStatus.BAD_REQUEST);
        }
    }
}
