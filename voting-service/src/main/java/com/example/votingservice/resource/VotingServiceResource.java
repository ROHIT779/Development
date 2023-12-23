package com.example.votingservice.resource;

import com.example.votingservice.helpers.VotingServiceHelper;
import com.example.votingservice.jdbc.JDBCManager;
import com.example.votingservice.model.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/voting")
public class VotingServiceResource {
    private final VotingServiceHelper helper;

    @Autowired
    public VotingServiceResource(VotingServiceHelper helper){
        this.helper=helper;
    }

    @GetMapping("/event/{eventId}")
    public ResponseEntity getEvent(@PathVariable("eventId") String eventId){
        EventWithNomination eventWithNomination = helper.getEvent(eventId);
        if(eventWithNomination!=null){
            return new ResponseEntity(eventWithNomination, HttpStatus.OK);
        }else{
            return new ResponseEntity(null, HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("/event/{eventId}/result")
    public ResponseEntity getResult(@PathVariable("eventId") String eventId){
        VotingResult result=helper.getResult(eventId);
        if(result!=null){
            return new ResponseEntity(result, HttpStatus.OK);
        }else{
            return new ResponseEntity(null, HttpStatus.BAD_REQUEST);
        }
    }

    @DeleteMapping("/delete-all")
    public ResponseEntity deleteAllData(){
            return new ResponseEntity<>(helper.deleteAllData(), HttpStatus.OK);
    }
}
