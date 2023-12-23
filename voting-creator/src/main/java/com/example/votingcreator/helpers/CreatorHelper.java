package com.example.votingcreator.helpers;

import com.example.votingcreator.generator.IDGenerator;
import com.example.votingcreator.jdbc.JDBCManager;
import com.example.votingcreator.model.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

@Component
public class CreatorHelper {
    private JDBCManager jdbcManager;

    @Autowired
    public CreatorHelper(JDBCManager jdbcManager){
        this.jdbcManager=jdbcManager;
    }

    public void createCreator(Creator creator){
        creator.setCreatorId(String.valueOf(IDGenerator.getRandomId()));
        jdbcManager.addCreator(creator);
    }
    public Creator getCreator(String creatorId){
        if(jdbcManager.validateId(creatorId,null)){
                return jdbcManager.getCreator(creatorId);
            }
        return  null;
    }

    public Event createEvent(String creatorId, Event event){
        event.setCreatorId(creatorId);
        event.setEventId(String.valueOf(IDGenerator.getRandomId()));
        if(jdbcManager.validateId(creatorId, null)){
            jdbcManager.addEvent(event);
            return event;
        }
        return null;
    }
    public Event getEvent(String creatorId, String eventId){
        if(jdbcManager.validateId(creatorId, eventId)){
            return jdbcManager.getEvent(creatorId, eventId);
        }
        return null;
    }
    public Nomination createNomination(String creatorId, String eventId, Nomination nomination){
        System.out.println(creatorId);
        System.out.println(eventId);
        nomination.setEventId(eventId);
        for(Candidate candidate:nomination.getCandidateList()){
            candidate.setCandidateId(IDGenerator.getRandomId());
        }
        if(jdbcManager.validateId(creatorId, eventId)){
            jdbcManager.addNomination(creatorId, nomination);
            return nomination;
        }else{
            return null;
        }
    }
    public EventWithNomination getNomination(String creatorId, String eventId){
        System.out.println(creatorId);
        System.out.println(eventId);
        if(jdbcManager.validateId(creatorId, eventId)){
            EventWithNomination eventWithNomination=jdbcManager.getNominations(eventId);
            return eventWithNomination;
        }else{
            return null;
        }
    }
}
