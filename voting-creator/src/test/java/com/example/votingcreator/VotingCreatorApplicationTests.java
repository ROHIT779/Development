package com.example.votingcreator;

import com.example.votingcreator.model.Candidate;
import com.example.votingcreator.model.Creator;
import com.example.votingcreator.model.Event;
import com.example.votingcreator.model.Nomination;
import com.example.votingcreator.resource.CreatorResource;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;

import java.net.ConnectException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
class VotingCreatorApplicationTests {

	private String votingServicePort="8083";
	private String port= "8081";

	private RestTemplate restTemplate = new RestTemplate();

	@AfterEach
	void tearDown() throws URISyntaxException {
		try{
			restTemplate.delete(new URI("http://localhost:"+votingServicePort+"/service/voting/delete-all/"));
		}catch(ResourceAccessException e){
			Logger.getAnonymousLogger().log(Level.WARNING, "Unable to delete test-data as voting-service refused to connect at Port: "+votingServicePort);
		}
	}

	@Test
	void testCreatorSuccessfulWorkflow() throws URISyntaxException {
		Creator creator = new Creator();
		creator.setCreatorName("creator 1");
		creator.setCreatorInfo("The first creator");
		Creator creatorReply= this.restTemplate.postForObject(new URI("http://localhost:"+port+"/service/creators/"), creator, Creator.class);
		assertThat(creatorReply.getCreatorName()).isEqualTo("creator 1");
		assertThat(creatorReply.getCreatorInfo()).isEqualTo("The first creator");

		Creator creatorGetReply= this.restTemplate.getForObject(new URI("http://localhost:"+port+"/service/creators/"+creatorReply.getCreatorId()), Creator.class);
		assertThat(creatorGetReply.getCreatorName()).isEqualTo(creatorReply.getCreatorName());
		assertThat(creatorGetReply.getCreatorInfo()).isEqualTo(creatorReply.getCreatorInfo());
		assertThat(creatorGetReply.getCreatorId()).isEqualTo(creatorReply.getCreatorId());

		Event event = new Event();
		event.setEventName("event 1");
		event.setEventInfo("first event");
		Event eventReply= this.restTemplate.postForObject(new URI("http://localhost:"+port+"/service/creators/"+creatorReply.getCreatorId()+"/events"), event, Event.class);
		assertThat(eventReply.getEventName()).isEqualTo("event 1");
		assertThat(eventReply.getEventInfo()).isEqualTo("first event");

		Event eventGetReply= this.restTemplate.getForObject(new URI("http://localhost:"+port+"/service/creators/"+creatorReply.getCreatorId()+"/events/"+eventReply.getEventId()), Event.class);
		assertThat(eventGetReply.getEventName()).isEqualTo(eventReply.getEventName());
		assertThat(eventGetReply.getEventInfo()).isEqualTo(eventReply.getEventInfo());
		assertThat(eventGetReply.getEventId()).isEqualTo(eventReply.getEventId());

		List<Candidate> candidateList = new ArrayList<>();
		Candidate candidate1 = new Candidate();
		candidate1.setCandidateName("X1");
		candidate1.setCandidateInfo("Experienced");
		Candidate candidate2 = new Candidate();
		candidate2.setCandidateName("X2");
		candidate2.setCandidateInfo("Beginner");
		candidateList.add(candidate1);
		candidateList.add(candidate2);
		Nomination nomination = new Nomination();
		nomination.setCandidateList(candidateList);

		Nomination nominationReply= this.restTemplate.postForObject(new URI("http://localhost:"+port+"/service/creators/"+creatorReply.getCreatorId()+"/events/"+eventReply.getEventId()+"/nominations"), nomination, Nomination.class);
		assertThat(nominationReply.getCandidateList().size()).isEqualTo(2);
		assertThat(nominationReply.getEventId()).isEqualTo(eventReply.getEventId());
		assertThat(nominationReply.getCandidateList().get(0).getCandidateName()).isEqualTo(candidate1.getCandidateName());
		assertThat(nominationReply.getCandidateList().get(0).getCandidateInfo()).isEqualTo(candidate1.getCandidateInfo());
		assertThat(nominationReply.getCandidateList().get(1).getCandidateName()).isEqualTo(candidate2.getCandidateName());
		assertThat(nominationReply.getCandidateList().get(1).getCandidateInfo()).isEqualTo(candidate2.getCandidateInfo());

		Nomination nominationGetReply= this.restTemplate.getForObject(new URI("http://localhost:"+port+"/service/creators/"+creatorReply.getCreatorId()+"/events/"+eventReply.getEventId()+"/nominations"), Nomination.class);
		assertThat(nominationGetReply.getCandidateList().size()).isEqualTo(2);
		assertThat(nominationGetReply.getEventId()).isEqualTo(eventReply.getEventId());
		assertThat(nominationGetReply.getCandidateList().get(0).getCandidateName()).isEqualTo(candidate1.getCandidateName());
		assertThat(nominationGetReply.getCandidateList().get(0).getCandidateInfo()).isEqualTo(candidate1.getCandidateInfo());
		assertThat(nominationGetReply.getCandidateList().get(1).getCandidateName()).isEqualTo(candidate2.getCandidateName());
		assertThat(nominationGetReply.getCandidateList().get(1).getCandidateInfo()).isEqualTo(candidate2.getCandidateInfo());
	}

	@Test
	void testInvalidCreatorIdBadRequest() throws URISyntaxException {
		Creator creator = new Creator();
		creator.setCreatorName("creator 1");
		creator.setCreatorInfo("The first creator");
		Creator creatorReply= this.restTemplate.postForObject(new URI("http://localhost:"+port+"/service/creators/"), creator, Creator.class);
		assertThat(creatorReply.getCreatorName()).isEqualTo("creator 1");
		assertThat(creatorReply.getCreatorInfo()).isEqualTo("The first creator");

		assertThatThrownBy(()->this.restTemplate.getForObject(new URI("http://localhost:"+port+"/service/creators/1234"), Creator.class)).isInstanceOf(HttpClientErrorException.BadRequest.class);
	}

	@Test
	void testValidCreatorIdInvalidExistingEventIdBadRequest() throws URISyntaxException {
		Creator creator1 = new Creator();
		creator1.setCreatorName("creator 1");
		creator1.setCreatorInfo("The first creator");
		Creator creator1Reply= this.restTemplate.postForObject(new URI("http://localhost:"+port+"/service/creators/"), creator1, Creator.class);

		Event event1 = new Event();
		event1.setEventName("event 1");
		event1.setEventInfo("first event");
		Event event1Reply= this.restTemplate.postForObject(new URI("http://localhost:"+port+"/service/creators/"+creator1Reply.getCreatorId()+"/events"), event1, Event.class);

		Creator creator2 = new Creator();
		creator2.setCreatorName("creator 1");
		creator2.setCreatorInfo("The first creator");
		Creator creator2Reply= this.restTemplate.postForObject(new URI("http://localhost:"+port+"/service/creators/"), creator2, Creator.class);

		Event event2 = new Event();
		event2.setEventName("event 1");
		event2.setEventInfo("first event");
		Event event2Reply= this.restTemplate.postForObject(new URI("http://localhost:"+port+"/service/creators/"+creator2Reply.getCreatorId()+"/events"), event1, Event.class);

		assertThatThrownBy(()->this.restTemplate.getForObject(new URI("http://localhost:"+port+"/service/creators/"+creator1Reply.getCreatorId()+"/events/"+event2Reply.getEventId()), Event.class)).isInstanceOf(HttpClientErrorException.BadRequest.class);
	}

	@Test
	void testValidCreatorIdInvalidExistingEventIdCreateNominationBadRequest() throws URISyntaxException {
		Creator creator1 = new Creator();
		creator1.setCreatorName("creator 1");
		creator1.setCreatorInfo("The first creator");
		Creator creator1Reply= this.restTemplate.postForObject(new URI("http://localhost:"+port+"/service/creators/"), creator1, Creator.class);

		Event event1 = new Event();
		event1.setEventName("event 1");
		event1.setEventInfo("first event");
		Event event1Reply= this.restTemplate.postForObject(new URI("http://localhost:"+port+"/service/creators/"+creator1Reply.getCreatorId()+"/events"), event1, Event.class);

		Creator creator2 = new Creator();
		creator2.setCreatorName("creator 1");
		creator2.setCreatorInfo("The first creator");
		Creator creator2Reply= this.restTemplate.postForObject(new URI("http://localhost:"+port+"/service/creators/"), creator2, Creator.class);

		Event event2 = new Event();
		event2.setEventName("event 1");
		event2.setEventInfo("first event");
		Event event2Reply= this.restTemplate.postForObject(new URI("http://localhost:"+port+"/service/creators/"+creator2Reply.getCreatorId()+"/events"), event1, Event.class);

		List<Candidate> candidateList=new ArrayList<>();
		Candidate candidate1=new Candidate();
		candidate1.setCandidateName("candidate 1");
		candidate1.setCandidateInfo("Experienced");

		Candidate candidate2=new Candidate();
		candidate2.setCandidateName("candidate 2");
		candidate2.setCandidateInfo("Beginner");
		candidateList.add(candidate1);
		candidateList.add(candidate2);
		Nomination nomination=new Nomination();
		nomination.setCandidateList(candidateList);
		assertThatThrownBy(()->this.restTemplate.postForObject(new URI("http://localhost:"+port+"/service/creators/"+creator1Reply.getCreatorId()+"/events/"+event2Reply.getEventId()+"/nominations"), nomination, Nomination.class)).isInstanceOf(HttpClientErrorException.BadRequest.class);
	}

	@Test
	void testValidCreatorIdInvalidExistingEventIdFetchNominationBadRequest() throws URISyntaxException {
		Creator creator1 = new Creator();
		creator1.setCreatorName("creator 1");
		creator1.setCreatorInfo("The first creator");
		Creator creator1Reply= this.restTemplate.postForObject(new URI("http://localhost:"+port+"/service/creators/"), creator1, Creator.class);

		Event event1 = new Event();
		event1.setEventName("event 1");
		event1.setEventInfo("first event");
		Event event1Reply= this.restTemplate.postForObject(new URI("http://localhost:"+port+"/service/creators/"+creator1Reply.getCreatorId()+"/events"), event1, Event.class);

		Creator creator2 = new Creator();
		creator2.setCreatorName("creator 1");
		creator2.setCreatorInfo("The first creator");
		Creator creator2Reply= this.restTemplate.postForObject(new URI("http://localhost:"+port+"/service/creators/"), creator2, Creator.class);

		Event event2 = new Event();
		event2.setEventName("event 1");
		event2.setEventInfo("first event");
		Event event2Reply= this.restTemplate.postForObject(new URI("http://localhost:"+port+"/service/creators/"+creator2Reply.getCreatorId()+"/events"), event1, Event.class);

		List<Candidate> candidateList=new ArrayList<>();
		Candidate candidate1=new Candidate();
		candidate1.setCandidateName("candidate 1");
		candidate1.setCandidateInfo("Experienced");

		Candidate candidate2=new Candidate();
		candidate2.setCandidateName("candidate 2");
		candidate2.setCandidateInfo("Beginner");
		candidateList.add(candidate1);
		candidateList.add(candidate2);
		Nomination nomination=new Nomination();
		nomination.setCandidateList(candidateList);
		Nomination nominationReply=this.restTemplate.postForObject(new URI("http://localhost:"+port+"/service/creators/"+creator1Reply.getCreatorId()+"/events/"+event1Reply.getEventId()+"/nominations"), nomination, Nomination.class);
		assertThat(nominationReply).isNotNull();

		assertThatThrownBy(()->this.restTemplate.getForObject(new URI("http://localhost:"+port+"/service/creators/"+creator1Reply.getCreatorId()+"/events/"+event2Reply.getEventId()+"/nominations"), Nomination.class)).isInstanceOf(HttpClientErrorException.BadRequest.class);
	}
}
