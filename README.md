# Development

All developed projects of ROHIT779

**1) Voting web service**

   <div>
   <div>
   <p><b>Goal:</b> Building a useful application parallelly when learning Spring Boot and PostgreSQL</p> 
   <p><b>Summary:</b> A utility which will allow voting/poll event organizers to create a voting event, share it with the voters and voters will be able to vote for their favorite candidates. Finally the organizers can see the result of the event.</p>

   <p><b>Technologies used:</b> Java, Spring Boot, REST APIs, Maven, PostgreSQL. </p> 
   <p><b>Modules:</b> Voting web service has 4 modules in total: 3 backend modules and 1 frontend module:
   <ol>
   <li><b>voting-creator:</b> A service for the event organizers (creators) to create a voting event and add nominations.</li> 
   <li><b>voting-voter:</b> A service for the voters to register themselves for the event and cast their vote to their favorite candidates.</li>
   <li><b>voting-service:</b> This service helps to get detailed information of a voting event and to see the final result of the event.</li>
   <li><b>voting-js:</b> The front-end component of this application.</li>
   </ol>
   </p> 
   </div>
   <div>
      <b>Sample Workflow</b>
   <ol>
   <li><b>Add creator</b><br>
   <p>Description: A person/organization who will create a voting event is called a Creator. With this service a creator will be added by providing creator's name and info.<p>
   <div>
   Sample Request:<br>
   URI: POST /creator/<br>
   Headers:<br>
   Content-Type: application/json<br>
   Request Body:<br>
   <pre>
   {
    "creatorName":"creator 1",
    "creatorInfo": "first creator"
   }
   </pre>
   </div>

   <div>
   Sample Response:<br>
   Headers:<br>
   Status: 201 <i>CREATED</i><br>
   Content-Type: application/json<br>
   Response Body:<br>
   <pre>
   {
   "creatorId":"fnvJJ",
   "creatorName":"creator 1",
   "creatorInfo":"first creator",
   "eventWithNominationList":null
   }
   </pre>
   </div>
   </li>

   <li><b>Get creator details</b><br>
   <p>Description: Creator's details will be fetched with given creator ID.</p>
   <div>
   Sample Request:<br>
   URI: GET /creator/fnvJJ<br>
   </div>

   <div>
   Sample Response:<br>
   Headers:<br>
   Status: 200 <i>OK</i><br>
   Content-Type: application/json<br>
   Response Body:<br>
   <pre>
   {
    "creatorId": "fnvJJ",
    "creatorName": "creator 1",
    "creatorInfo": "first creator",
    "eventWithNominationList": []
   }
   </pre>
   </div>
   </li>

   <li><b>Create voting event</b><br>
   <p>Description: A voting event will be created by the creator by providing event-name and event-info.</p>
   <div>
   Sample Request:<br>
   URI: POST /creator/fnvJJ/event<br>
   Headers:<br>
   Content-Type: application/json<br>
   Request Body:<br>
   <pre>
   {
    "eventName":"voting-event 1",
    "eventInfo":"first voting event"
   }
   </pre>
   </div>

   <div>
   Sample Response:<br>
   Headers:<br>
   Status: 201 <i>CREATED</i><br>
   Content-Type: application/json<br>
   Response Body:<br>
   <pre>
   {
    "eventId": "dEok6",
    "eventName": "voting-event 1",
    "eventInfo": "first voting event",
    "creatorId": "fnvJJ"
   }
   </pre>
   </div>
   </li>

   <li><b>Get Event Details</b><br>
   <p>Description: Event details can be fetched by the creator by providing event ID.</p>
   <div>
   Sample Request:<br>
   URI: GET /creator/fnvJJ/event/dEok6<br>
   </div>

   <div>
   Sample Response:<br>
   Headers:<br>
   Status: 200 <i>OK</i><br>
   Content-Type: application/json<br>
   Response Body:<br>
   <pre>
   {
    "eventId": "dEok6",
    "eventName": "voting-event 1",
    "eventInfo": "first voting event",
    "creatorId": "fnvJJ"
   }
   </pre>
   </div>
   </li>

   <li><b>Add nominations for the Event</b><br>
   <p>Description: Creator can add candidate nominations by providing creator ID and event ID.</p>
   <div>
   Sample Request:<br>
   URI: POST /creator/fnvJJ/event/dEok6/nomination<br>
   Headers:<br>
   Content-Type: application/json<br>
   Request Body:<br>
   <pre>
   {
    "candidateList":[
        {
            "candidateName":"candidate 1",
            "candidateInfo":"candidate info 1"
        },
        {
            "candidateName":"candidate 2",
            "candidateInfo":"candidate info 2"
        },
        {
            "candidateName":"candidate 3",
            "candidateInfo":"candidate info 3"
        }
    ]
   }
   </pre>
   </div>

   <div>
   Sample Response:<br>
   Headers:<br>
   Status: 201 <i>CREATED</i><br>
   Content-Type: application/json<br>
   Response Body:<br>
   <pre>
{
    "eventId": "dEok6",
    "candidateList": [
        {
            "candidateId": "MkXL9",
            "candidateName": "candidate 1",
            "candidateInfo": "candidate info 1"
        },
        {
            "candidateId": "FmnKv",
            "candidateName": "candidate 2",
            "candidateInfo": "candidate info 2"
        },
        {
            "candidateId": "ehFN2",
            "candidateName": "candidate 3",
            "candidateInfo": "candidate info 3"
        }
    ]
}
   </pre>
   </div>
   </li>
   <li><b>Get Nomination Details</b><br>
   <p>Description: Creator can fetch candidate nomination details by providing creator ID and event ID.</p>
   <div>
   Sample Request:<br>
   URI: GET /creator/fnvJJ/event/dEok6/nomination<br>
   </div>

   <div>
   Sample Response:<br>
   Headers:<br>
   Status: 200 <i>OK</i><br>
   Content-Type: application/json<br>
   Response Body:<br>
   <pre>
 {
    "eventId": "dEok6",
    "eventName": "voting-event 1",
    "eventInfo": "first voting event",
    "creatorId": "fnvJJ",
    "candidateList": [
        {
            "candidateId": "MkXL9",
            "candidateName": "candidate 1",
            "candidateInfo": "candidate info 1"
        },
        {
            "candidateId": "FmnKv",
            "candidateName": "candidate 2",
            "candidateInfo": "candidate info 2"
        },
        {
            "candidateId": "ehFN2",
            "candidateName": "candidate 3",
            "candidateInfo": "candidate info 3"
        }
    ]
}
   </pre>
   </div>
   </li>

   <li><b>Add voter</b><br>
   <p>Description: A voter will be registered to the voting-event by providing event ID and his name.</p>
   <div>
   Sample Request:<br>
   URI: POST /event/dEok6/voter/<br>
   Headers:<br>
   Content-Type: application/json<br>
   Request Body:<br>
   <pre>
{
    "voterName": "voter 1"
}
   </pre>
   </div>

   <div>
   Sample Response:<br>
   Headers:<br>
   Status: 200 <i>OK</i><br>
   Content-Type: application/json<br>
   Response Body:<br>
   <pre>
{
    "voterId": "0bn3X",
    "voterName": "voter 1",
    "vote": null,
    "eventId": "dEok6"
}
   </pre>
   </div>
   </li>

   <li><b>Get Voter Details</b><br>
   <p>Description: A voter can view his details.</p>
   <div>
   Sample Request:<br>
   URI: GET /event/dEok6/voter/0bn3X<br>
   </div>

   <div>
   Sample Response:<br>
   Headers:<br>
   Status: 200 <i>OK</i><br>
   Content-Type: application/json<br>
   Response Body:<br>
   <pre>
{
    "voterId": "0bn3X",
    "voterName": "voter 1",
    "vote": null,
    "eventId": "dEok6"
}
   </pre>
   </div>
   </li>

   <li><b>Get Event Details</b><br>
   <p>Description: A voter can view nominated candidates before casting vote.</p>
   <div>
   Sample Request:<br>
   URI: GET /event/dEok6/<br>
   </div>

   <div>
   Sample Response:<br>
   Headers:<br>
   Status: 200 <i>OK</i><br>
   Content-Type: application/json<br>
   Response Body:<br>
   <pre>
{
    "eventId": "dEok6",
    "eventName": "voting-event 1",
    "eventInfo": "first voting event",
    "creatorId": "fnvJJ",
    "candidateList": [
        {
            "candidateId": "MkXL9",
            "candidateName": "candidate 1",
            "candidateInfo": "candidate info 1"
        },
        {
            "candidateId": "FmnKv",
            "candidateName": "candidate 2",
            "candidateInfo": "candidate info 2"
        },
        {
            "candidateId": "ehFN2",
            "candidateName": "candidate 3",
            "candidateInfo": "candidate info 3"
        }
    ]
}
   </pre>
   </div>
   </li>

   <li><b>Cast Vote</b><br>
   <p>Description: A voter will be able to cast vote to his favorite candidate</p>
   <div>
   Sample Request:<br>
   URI: POST /event/dEok6/voter/0bn3X/vote<br>
   Headers:<br>
   Content-Type: application/json<br>
   Request Body:<br>
   <pre>
{
    "candidateId": "FmnKv"
}
   </pre>
   </div>

   <div>
   Sample Response:<br>
   Headers:<br>
   Status: 200 <i>OK</i><br>
   Content-Type: application/json<br>
   Response Body:<br>
   <pre>
{
    "voterId": "0bn3X",
    "voterName": "voter 1",
    "vote": {
        "candidateId": "FmnKv"
    },
    "eventId": "dEok6"
}
   </pre>
   </div>
   </li>

   <li><b>Get Event Details</b><br>
   <p>Description: This service is to get additional details of the Event</p>
   <div>
   Sample Request:<br>
   URI: GET /voting/event/dEok6<br>
   </div>

   <div>
   Sample Response:<br>
   Headers:<br>
   Status: 200 <i>OK</i><br>
   Content-Type: application/json<br>
   Response Body:<br>
   <pre>
{
    "eventId": "dEok6",
    "eventName": "voting-event 1",
    "eventInfo": "first voting event",
    "creatorId": "fnvJJ",
    "candidateList": [
        {
            "candidateId": "MkXL9",
            "candidateName": "candidate 1",
            "candidateInfo": "candidate info 1"
        },
        {
            "candidateId": "FmnKv",
            "candidateName": "candidate 2",
            "candidateInfo": "candidate info 2"
        },
        {
            "candidateId": "ehFN2",
            "candidateName": "candidate 3",
            "candidateInfo": "candidate info 3"
        }
    ]
}
   </pre>
   </div>
   </li>

   <li><b>Get Event Result</b><br>
   <p>Description: Final result of the event can be viewed.</p>
   <div>
   Sample Request:<br>
   URI: GET /voting/event/dEok6/result<br>
   </div>

   <div>
   Sample Response:<br>
   Headers:<br>
   Status: 200 <i>OK</i><br>
   Content-Type: application/json<br>
   Response Body:<br>
   <pre>
{
    "eventId": "dEok6",
    "finalResult": [
        {
            "candidate": {
                "candidateId": "FmnKv",
                "candidateName": "candidate 2",
                "candidateInfo": "candidate info 2"
            },
            "count": 2
        },
        {
            "candidate": {
                "candidateId": "ehFN2",
                "candidateName": "candidate 3",
                "candidateInfo": "candidate info 3"
            },
            "count": 1
        }
    ]
}
   </pre>
   </div>
   </li>

   <li><b>Delete All Data</b><br>
   <p>Description: An Admin level utility service to delete all data related to an event.</p>
   <div>
   Sample Request:<br>
   URI: DELETE /voting/delete-all<br>
   </div>

   <div>
   Sample Response:<br>
   Headers:<br>
   Status: 200 <i>OK</i><br>
   Content-Type: application/json<br>
   Response Body:<br>
   <pre>
2 rows affected in table: result
3 rows affected in table: voter
3 rows affected in table: nomination
3 rows affected in table: candidate
1 rows affected in table: event
1 rows affected in table: creator
   </pre>
   </div>
   </li>

   </ol>
   </div>

   <div>
   <ul>
   <li>
   <p><b>Validations performed: </b>
   <p>In case of any validation failure, Response will be <b>400 BAD_REQUEST</b> with no Response body.</p>
   <ol>
   <li><b>GET /creator/{creatorId} :</b> If non-existing creatorId is passed, response status will be 400.</li>
   <li><b>POST /creator/{creatorId}/event :</b> If non-existing creatorId is passed, response status will be 400.</li>
   <li><b>GET /creator/{creatorId}/event/{eventId} :</b> If non-existing creatorId or eventId is passed OR an eventId is passed which is not created by creatorId, response status will be 400.</li>
   <li><b>POST /creator/{creatorId}/event/{eventId}/nomination :</b> If non-existing creatorId or eventId is passed OR an eventId is passed which is not created by creatorId, response status will be 400.</li>
   <li><b>GET /creator/{creatorId}/event/{eventId}/nomination :</b> If non-existing creatorId or eventId is passed OR an eventId is passed which is not created by creatorId, response status will be 400.</li>
   <li><b>POST /event/{eventId}/voter/ :</b> If non-existing eventId is passed, response status will be 400.</li>
   <li><b>GET /event/{eventId}/voter/{voterId} :</b> If non-existing eventId or voterId is passed OR a voterId is passed who is not registered with the eventId, response status will be 400.</li>
   <li><b>GET /event/{eventId} :</b> If non-existing eventId is passed, response status will be 400.</li>
   <li><b>POST /event/{eventId}/voter/{voterId}/vote :</b> If non-existing eventId or voterId or candidateId is passed OR a voterId or candidateId is passed who is not registered with the eventId OR voterId tries to vote more than once, response status will be 400.</li>
   <li><b>GET voting/event/{eventId} :</b> If non-existing eventId is passed, response status will be 400.</li>
   <li><b>GET voting/event/{eventId}/result :</b> If non-existing eventId is passed, response status will be 400.</li>
   </ol>
   </p>
   </li>
    <li><i><b>Note:</b> The candidate a person is voting for is <b>NEVER</b> stored in database due to confidentiality.</i></li>
   <li><i>Status: In-progress</i></li>
   </ul>
   </div>
