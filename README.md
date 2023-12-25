# Development
All developed projects of ROHIT779  

**1) Voting web service**  
   <div>
   <div> 
   Summary: A utility which will allow voting/poll event organizers to create a voting event, share it with the voters and voters will be able to vote for their favorite candidates. Finally the organizers can see the result of the event.

   Technologies used: Java, Spring Boot, REST APIs, Maven, PostgreSQL.  
   Modules: Voting web service has 3 modules:
   1) voting-creator: A service for the event organizers (creators) to create a voting event and add nominations.  
   2) voting-voter: A service for the voters to register themselves for the event and cast their vote to their favorite candidates.  
   3) voting-service: This service helps to get detailed information of a voting event and to see the final result of the event.  
   4) voting-js: The front-end component of this application  
   </div>
   <div>
   <i>Current Progress: voting-creator, voting-voter and voting-service have its implementations in place. Integration testing is done for the 3 modules considering success workflow and few edge case failure workflows.</i>
   </div>
   <div>
   <i>Work Pending: Adding documentation for all the modules, Adding javadoc, using Docker to deploy services, adding Custom Exceptions, Designing better, adding authentication (if possible), adding service-profiles. Finally developing voting-js, the front-end component.
   </div>
