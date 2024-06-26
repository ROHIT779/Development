package com.example.votingvoter;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.example.votingvoter.jdbc.JDBCProperties;
import com.example.votingvoter.jdbc.TestJDBCManager;
import com.example.votingvoter.model.Candidate;
import com.example.votingvoter.model.Vote;
import com.example.votingvoter.model.Voter;
import java.net.URI;
import java.net.URISyntaxException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

@SpringBootTest
class VotingVoterApplicationTests {

  private String port = "8082";

  private RestTemplate restTemplate = new RestTemplate();
  @Autowired private TestJDBCManager jdbcManager;

  @Autowired JDBCProperties jdbcProperties;

  @BeforeEach
  void setUp() throws SQLException {
    int rowsAffected = 0;
    Connection connection =
        DriverManager.getConnection(
            jdbcProperties.getDatabaseUrl() + "/" + jdbcProperties.getDatabaseName(),
            jdbcProperties.getUserName(),
            jdbcProperties.getPassword());
    rowsAffected = createCreator(connection, "c1", "creator 1", "first creator");
    assertTrue(rowsAffected == 1);

    rowsAffected = createEvent(connection, "e1", "event 1", "first event", "c1");
    assertTrue(rowsAffected == 1);

    List<Candidate> candidateList1 = new ArrayList<>();
    Candidate candidate1 = new Candidate("cnd1", "candidate 1", "experienced");
    Candidate candidate2 = new Candidate("cnd2", "candidate 2", "beginner");
    candidateList1.add(candidate1);
    candidateList1.add(candidate2);
    addNomination(connection, "e1", candidateList1);

    rowsAffected = createEvent(connection, "e2", "event 2", "second event", "c1");
    assertTrue(rowsAffected == 1);

    List<Candidate> candidateList2 = new ArrayList<>();
    Candidate candidate3 = new Candidate("cnd3", "candidate 3", "experienced");
    Candidate candidate4 = new Candidate("cnd4", "candidate 4", "beginner");
    candidateList2.add(candidate3);
    candidateList2.add(candidate4);
    addNomination(connection, "e2", candidateList2);
    connection.close();
  }

  @AfterEach
  void tearDown() throws URISyntaxException {
    try {
      jdbcManager.deleteAllData();
    } catch (SQLException e) {
      Logger.getAnonymousLogger()
          .log(Level.WARNING, "Following exception is thrown when deleting data from tables: " + e);
    }
  }

  @Test
  void testVoterSuccessfulWorkflow() throws URISyntaxException {
    Voter voter = new Voter();
    voter.setVoterName("voter 1");
    Voter voterReply =
        this.restTemplate.postForObject(
            new URI("http://localhost:" + port + "/service/events/e1/voters/"), voter, Voter.class);
    assertThat(voterReply.getVoterName()).isEqualTo("voter 1");
    assertThat(voterReply.getEventId()).isEqualTo("e1");

    Voter voterGetReply =
        this.restTemplate.getForObject(
            new URI(
                "http://localhost:"
                    + port
                    + "/service/events/e1/voters/"
                    + voterReply.getVoterId()),
            Voter.class);
    assertThat(voterGetReply.getVoterId()).isEqualTo(voterReply.getVoterId());
    assertThat(voterGetReply.getVoterName()).isEqualTo(voterReply.getVoterName());
    assertThat(voterGetReply.getEventId()).isEqualTo(voterReply.getEventId());
    assertThat(voterGetReply.getVote()).isEqualTo(null);

    Vote vote = new Vote();
    vote.setCandidateId("cnd2");
    Voter voterVoteReply =
        this.restTemplate.postForObject(
            new URI(
                "http://localhost:"
                    + port
                    + "/service/events/e1/voters/"
                    + voterReply.getVoterId()
                    + "/vote"),
            vote,
            Voter.class);
    assertThat(voterVoteReply).isNotNull();
    assertThat(voterVoteReply.getVote().getCandidateId()).isEqualTo("cnd2");
  }

  @Test
  void testVoterValidEventIdInvalidExistingCandidateIdBadRequest() throws URISyntaxException {
    Voter voter = new Voter();
    voter.setVoterName("voter 1");
    Voter voterReply =
        this.restTemplate.postForObject(
            new URI("http://localhost:" + port + "/service/events/e1/voters/"), voter, Voter.class);

    Vote vote = new Vote();
    vote.setCandidateId("cnd3");
    assertThatThrownBy(
            () ->
                this.restTemplate.postForObject(
                    new URI(
                        "http://localhost:"
                            + port
                            + "/service/events/e1/voters/"
                            + voterReply.getVoterId()
                            + "/vote"),
                    vote,
                    Voter.class))
        .isInstanceOf(HttpClientErrorException.BadRequest.class);
  }

  @Test
  void testVoterCastingVoteMoreThanOnceBadRequest() throws URISyntaxException {
    Voter voter = new Voter();
    voter.setVoterName("voter 1");
    Voter voterReply =
        this.restTemplate.postForObject(
            new URI("http://localhost:" + port + "/service/events/e1/voters/"), voter, Voter.class);

    Vote vote = new Vote();
    vote.setCandidateId("cnd1");
    this.restTemplate.postForObject(
        new URI(
            "http://localhost:"
                + port
                + "/service/events/e1/voters/"
                + voterReply.getVoterId()
                + "/vote"),
        vote,
        Voter.class);
    assertThatThrownBy(
            () ->
                this.restTemplate.postForObject(
                    new URI(
                        "http://localhost:"
                            + port
                            + "/service/events/e1/voters/"
                            + voterReply.getVoterId()
                            + "/vote"),
                    vote,
                    Voter.class))
        .isInstanceOf(HttpClientErrorException.BadRequest.class);
  }

  @Test
  void testVoterUnableToCreateEventLockedBadRequest() throws URISyntaxException {
    lockEvent("e1");
    Voter voter = new Voter();
    voter.setVoterName("voter 1");
    assertThatThrownBy(
            () ->
                this.restTemplate.postForObject(
                    new URI("http://localhost:" + port + "/service/events/e1/voters/"),
                    voter,
                    Voter.class))
        .isInstanceOf(HttpClientErrorException.BadRequest.class);
  }

  @Test
  void testVoterUnableToVoteEventLockedBadRequest() throws URISyntaxException {
    Voter voter = new Voter();
    voter.setVoterName("voter 1");
    Voter voterReply =
        this.restTemplate.postForObject(
            new URI("http://localhost:" + port + "/service/events/e1/voters/"), voter, Voter.class);
    assertThat(voterReply.getVoterName()).isEqualTo("voter 1");
    assertThat(voterReply.getEventId()).isEqualTo("e1");

    Voter voterGetReply =
        this.restTemplate.getForObject(
            new URI(
                "http://localhost:"
                    + port
                    + "/service/events/e1/voters/"
                    + voterReply.getVoterId()),
            Voter.class);
    assertThat(voterGetReply.getVoterId()).isEqualTo(voterReply.getVoterId());
    assertThat(voterGetReply.getVoterName()).isEqualTo(voterReply.getVoterName());
    assertThat(voterGetReply.getEventId()).isEqualTo(voterReply.getEventId());
    assertThat(voterGetReply.getVote()).isEqualTo(null);

    lockEvent("e1");
    Vote vote = new Vote();
    vote.setCandidateId("cnd2");
    assertThatThrownBy(
            () ->
                this.restTemplate.postForObject(
                    new URI(
                        "http://localhost:"
                            + port
                            + "/service/events/e1/voters/"
                            + voterGetReply.getVoterId()
                            + "/vote"),
                    vote,
                    Voter.class))
        .isInstanceOf(HttpClientErrorException.BadRequest.class);
  }

  private boolean lockEvent(String eventId) {
    int rowCount = 0;
    Connection connection = null;
    PreparedStatement statement = null;
    try {
      // below two lines are used for connectivity.
      // Class.forName("com.mysql.cj.jdbc.Driver");
      connection =
          DriverManager.getConnection(
              jdbcProperties.getDatabaseUrl() + "/" + jdbcProperties.getDatabaseName(),
              jdbcProperties.getUserName(),
              jdbcProperties.getPassword());

      // mydb is database
      // mydbuser is name of database
      // mydbuser is password of database

      statement = connection.prepareStatement("update event set locked=true where event_id=?");
      statement.setString(1, eventId);
      System.out.println(statement);
      rowCount = statement.executeUpdate();
      System.out.println("Row count after locking the event: " + rowCount);
    } catch (Exception exception) {
      System.out.println(exception);
    } finally {
      try {
        if (statement != null) {
          statement.close();
        }
        if (connection != null) {
          connection.close();
        }
      } catch (SQLException exception) {
        System.out.println(exception);
      }
    }
    return rowCount > 0 ? true : false;
  }

  private int createCreator(
      Connection connection, String creatorId, String creatorName, String creatorInfo)
      throws SQLException {
    // mydb is database
    // mydbuser is name of database
    // mydbuser is password of database

    String sql = "insert into creator values (?,?,?)";
    PreparedStatement statement = connection.prepareStatement(sql);
    statement.setString(1, creatorId);
    statement.setString(2, creatorName);
    statement.setString(3, creatorInfo);
    int rowsAffected = statement.executeUpdate();

    statement.close();
    return rowsAffected;
  }

  private int createEvent(
      Connection connection, String eventId, String eventName, String eventInfo, String creatorId)
      throws SQLException {
    String sql = "insert into event values (?,?,?,?)";
    PreparedStatement statement = connection.prepareStatement(sql);
    statement.setString(1, eventId);
    statement.setString(2, eventName);
    statement.setString(3, eventInfo);
    statement.setString(4, creatorId);
    int rowsAffected = statement.executeUpdate();
    statement.close();
    return rowsAffected;
  }

  private void addNomination(Connection connection, String eventId, List<Candidate> candidateList)
      throws SQLException {
    int rowsAffected = 0;
    for (Candidate candidate : candidateList) {
      String sql = "insert into candidate values (?,?,?)";
      PreparedStatement statement = connection.prepareStatement(sql);
      statement.setString(1, candidate.getCandidateId());
      statement.setString(2, candidate.getCandidateName());
      statement.setString(3, candidate.getCandidateInfo());
      statement.executeUpdate();
      statement.close();
      rowsAffected++;
    }
    assertTrue(rowsAffected == 2);

    rowsAffected = 0;
    for (Candidate candidate : candidateList) {
      String sql = "insert into nomination values (?,?)";
      PreparedStatement statement = connection.prepareStatement(sql);
      statement.setString(1, candidate.getCandidateId());
      statement.setString(2, eventId);
      statement.executeUpdate();
      statement.close();
      rowsAffected++;
    }
    assertTrue(rowsAffected == 2);
  }
}
