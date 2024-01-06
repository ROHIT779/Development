package com.example.votingcreator.resource;

import com.example.votingcreator.generator.IDGenerator;
import com.example.votingcreator.helpers.CreatorHelper;
import com.example.votingcreator.jdbc.JDBCManager;
import com.example.votingcreator.model.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/service/creators")
public class CreatorResource {

    private final CreatorHelper helper;

    @Autowired
    public CreatorResource(CreatorHelper helper){
        this.helper=helper;
    }
    @PostMapping("/")
    public ResponseEntity createCreator(@RequestBody Creator creator){
        helper.createCreator(creator);
        return new ResponseEntity<>(creator,HttpStatus.CREATED);
    }
    @GetMapping("/{creatorId}")
    public ResponseEntity getCreator(@PathVariable("creatorId") String creatorId){
        Creator creator=helper.getCreator(creatorId);
        if(creator!=null){
            return new ResponseEntity(creator,HttpStatus.OK);
        }else{
            return new ResponseEntity(null,HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping("/{creatorId}/events")
    public ResponseEntity createEvent(@PathVariable("creatorId") String creatorId, @RequestBody Event event){
        Event eventReply=helper.createEvent(creatorId, event);
        if(eventReply!=null){
            return new ResponseEntity(eventReply, HttpStatus.CREATED);
        }
        return new ResponseEntity(null, HttpStatus.BAD_REQUEST);
    }

    @GetMapping("/{creatorId}/events/{eventId}")
    public ResponseEntity getEvent(@PathVariable("creatorId") String creatorId, @PathVariable("eventId") String eventId){
        Event event=helper.getEvent(creatorId, eventId);
        if(event!=null){
            return new ResponseEntity<>(event, HttpStatus.OK);
        }else{
            return new ResponseEntity(null, HttpStatus.BAD_REQUEST);
        }
    }
    @PostMapping("/{creatorId}/events/{eventId}/nominations")
    public ResponseEntity createNomination(@PathVariable("creatorId") String creatorId,
                                           @PathVariable("eventId") String eventId,
                                           @RequestBody Nomination nomination){
        Nomination nominationReply=helper.createNomination(creatorId, eventId, nomination);
        if(nominationReply!=null){
            return new ResponseEntity(nominationReply, HttpStatus.CREATED);
        }else{
            return new ResponseEntity(null,HttpStatus.BAD_REQUEST);
        }
    }
    @GetMapping("/{creatorId}/events/{eventId}/nominations")
    public ResponseEntity getNomination(@PathVariable("creatorId") String creatorId,
                                           @PathVariable("eventId") String eventId){
        EventWithNomination eventWithNomination=helper.getNomination(creatorId, eventId);
        if(eventWithNomination!=null){
            return new ResponseEntity(eventWithNomination, HttpStatus.OK);
        }else{
            return new ResponseEntity(eventWithNomination, HttpStatus.BAD_REQUEST);
        }
    }
}
