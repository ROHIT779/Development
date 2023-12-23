package com.example.votingservice.helpers;

import com.example.votingservice.jdbc.JDBCManager;
import com.example.votingservice.model.EventWithNomination;
import com.example.votingservice.model.VotingResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.client.RestTemplate;

@Component
public class VotingServiceHelper {
    private JDBCManager jdbcManager;

    @Autowired
    public VotingServiceHelper(JDBCManager jdbcManager){
        this.jdbcManager=jdbcManager;
    }

    public EventWithNomination getEvent(String eventId){
        RestTemplate restTemplate = new RestTemplate();
        if(jdbcManager.validateId("event_id",eventId)){
            String creatorId = jdbcManager.getCreatorFromEvent(eventId);
            EventWithNomination eventWithNomination = restTemplate.getForObject("http://localhost:8081/creator/"+creatorId+"/event/"+eventId+"/nomination", EventWithNomination.class);
            return eventWithNomination;
        }else{
            return null;
        }
    }

    public VotingResult getResult(String eventId){
        if(jdbcManager.validateId("event_id",eventId)){
            return jdbcManager.getResult(eventId);
        }else{
            return null;
        }
    }

    public String deleteAllData(){
        return jdbcManager.deleteAllData();
    }
}