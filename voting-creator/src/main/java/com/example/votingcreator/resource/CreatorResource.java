package com.example.votingcreator.resource;

import com.example.votingcreator.generator.IDGenerator;
import com.example.votingcreator.jdbc.JDBCManager;
import com.example.votingcreator.model.Candidate;
import com.example.votingcreator.model.Creator;
import com.example.votingcreator.model.Event;
import com.example.votingcreator.model.Nomination;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/creator")
public class CreatorResource {
    @PostMapping("/")
    public Creator createCreator(@RequestBody Creator creator){
        creator.setCreatorId(String.valueOf(IDGenerator.getRandomId()));
        JDBCManager.addCreator(creator);
        return creator;
    }
    @GetMapping("/{creatorId}")
    public Creator getCreator(@PathVariable("creatorId") String creatorId){
        return JDBCManager.getCreator(creatorId);
    }

    @PostMapping("/{creatorId}/event")
    public Event createEvent(@PathVariable("creatorId") String creatorId, @RequestBody Event event){
        event.setCreatorId(creatorId);
        event.setEventId(String.valueOf(IDGenerator.getRandomId()));
        JDBCManager.addEvent(event);
        return event;
    }

    @GetMapping("/{creatorId}/event/{eventId}")
    public Event getEvent(@PathVariable("creatorId") String creatorId, @PathVariable("eventId") String eventId){
        return JDBCManager.getEvent(creatorId, eventId);
    }
    @PostMapping("/{creatorId}/event/{eventId}/nomination")
    public Nomination createNomination(@PathVariable("creatorId") String creatorId,
                                       @PathVariable("eventId") String eventId,
                                       @RequestBody Nomination nomination){
        System.out.println(creatorId);
        System.out.println(eventId);
        nomination.setEventId(eventId);
        for(Candidate candidate:nomination.getCandidateList()){
            candidate.setCandidateId(IDGenerator.getRandomId());
        }
        JDBCManager.addNomination(creatorId, nomination);
        return nomination;
    }
}
