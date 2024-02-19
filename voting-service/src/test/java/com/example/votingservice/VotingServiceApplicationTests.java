package com.example.votingservice;

import com.example.votingservice.jdbc.JDBCProperties;
import com.example.votingservice.jdbc.TestJDBCManager;
import com.example.votingservice.model.Candidate;
import com.example.votingservice.model.VotingResult;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;

import java.net.URI;
import java.net.URISyntaxException;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
class VotingServiceApplicationTests {

	private String port= "8083";

	private RestTemplate restTemplate = new RestTemplate();

	@Autowired
	TestJDBCManager jdbcManager;

	@Autowired
	JDBCProperties jdbcProperties;

	@BeforeEach
	void setUp() throws SQLException {
		int rowsAffected=0;
		Connection connection = DriverManager.getConnection(
				jdbcProperties.getDatabaseUrl()+"/"+jdbcProperties.getDatabaseName(),
				jdbcProperties.getUserName(), jdbcProperties.getPassword());
		rowsAffected=createCreator(connection, "c1","creator 1","first creator");
		assertTrue(rowsAffected==1);

		rowsAffected=createEvent(connection, "e1","event 1","first event","c1");
		assertTrue(rowsAffected==1);

		List<Candidate> candidateList1=new ArrayList<>();
		Candidate candidate1 = new Candidate("cnd1","candidate 1","experienced");
		Candidate candidate2 = new Candidate("cnd2","candidate 2","beginner");
		candidateList1.add(candidate1);
		candidateList1.add(candidate2);
		addNomination(connection,"e1",candidateList1);

		rowsAffected=createEvent(connection, "e2","event 2","second event","c1");
		assertTrue(rowsAffected==1);

		List<Candidate> candidateList2=new ArrayList<>();
		Candidate candidate3 = new Candidate("cnd3","candidate 3","experienced");
		Candidate candidate4 = new Candidate("cnd4","candidate 4","beginner");
		candidateList2.add(candidate3);
		candidateList2.add(candidate4);
		addNomination(connection,"e2",candidateList2);

		rowsAffected=createVoter(connection, "v1","voter 1","e1");
		assertTrue(rowsAffected==1);
		rowsAffected=createVoter(connection, "v2","voter 2","e1");
		assertTrue(rowsAffected==1);
		rowsAffected=createVoter(connection, "v3","voter 3","e1");
		assertTrue(rowsAffected==1);
		rowsAffected=createVoter(connection, "v4","voter 4","e2");
		assertTrue(rowsAffected==1);
		rowsAffected=createVoter(connection, "v5","voter 5","e2");
		assertTrue(rowsAffected==1);

		castVote(connection, "v1", "e1", "cnd1");
		castVote(connection, "v2", "e1", "cnd2");
		castVote(connection, "v3", "e1", "cnd1");

		castVote(connection, "v4", "e2", "cnd3");
		castVote(connection, "v5", "e2", "cnd4");
		connection.close();
	}

	@AfterEach
	void tearDown() {
		try{
			jdbcManager.deleteAllData();
		}catch(SQLException e){
			Logger.getAnonymousLogger().log(Level.WARNING, "Following exception is thrown when deleting data from tables: "+e);
		}
	}

	@Test
	void testGetResultSuccess() throws URISyntaxException {
		VotingResult resultReply=restTemplate.getForObject(new URI("http://localhost:"+port+"/service/voting/events/e1/result"), VotingResult.class);
		if(resultReply.getFinalResult().get(0).getCandidate().getCandidateId().equals("cnd1")){
			assertThat(resultReply.getFinalResult().get(0).getCount()).isEqualTo(2);
		}else if(resultReply.getFinalResult().get(0).getCandidate().getCandidateId().equals("cnd2")){
			assertThat(resultReply.getFinalResult().get(0).getCount()).isEqualTo(1);
		}
	}

	@Test
	void testGetResultInvalidNonExistingEventIdBadRequest() throws URISyntaxException {
		assertThatThrownBy(()->restTemplate.getForObject(new URI("http://localhost:"+port+"/service/voting/events/e7/result"), VotingResult.class)).isInstanceOf(HttpClientErrorException.BadRequest.class);
	}

	private int createCreator(Connection connection, String creatorId, String creatorName, String creatorInfo) throws SQLException {
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

	private int createEvent(Connection connection, String eventId, String eventName, String eventInfo, String creatorId) throws SQLException {
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

	private void addNomination(Connection connection, String eventId, List<Candidate> candidateList) throws SQLException {
		int rowsAffected=0;
		for(Candidate candidate : candidateList){
			String sql = "insert into candidate values (?,?,?)";
			PreparedStatement statement = connection.prepareStatement(sql);
			statement.setString(1, candidate.getCandidateId());
			statement.setString(2, candidate.getCandidateName());
			statement.setString(3, candidate.getCandidateInfo());
			statement.executeUpdate();
			statement.close();
			rowsAffected++;
		}
		assertTrue(rowsAffected==2);

		rowsAffected=0;
		for(Candidate candidate : candidateList){
			String sql = "insert into nomination values (?,?)";
			PreparedStatement statement = connection.prepareStatement(sql);
			statement.setString(1, candidate.getCandidateId());
			statement.setString(2, eventId);
			statement.executeUpdate();
			statement.close();
			rowsAffected++;
		}
		assertTrue(rowsAffected==2);
	}

	private int createVoter(Connection connection, String voterId, String voterName, String eventId) throws SQLException {
		int rowsAffected=0;
		String sql = "insert into voter values (?,?,?,?)";
		PreparedStatement statement = connection.prepareStatement(sql);
		statement.setString(1, voterId);
		statement.setString(2, voterName);
		statement.setString(3, eventId);
		statement.setBoolean(4, false);

		rowsAffected = statement.executeUpdate();

		statement.close();
		return rowsAffected;
	}

	private void castVote(Connection connection, String voterId, String eventId, String candidateId) throws SQLException {
		Boolean hasVoted=false;
		String sqlVoterAlreadyVoted = "select voted from voter where voter_id=?";
		PreparedStatement statement = connection.prepareStatement(sqlVoterAlreadyVoted);
		statement.setString(1, voterId);
		ResultSet resultSet=statement.executeQuery();
		if(resultSet.next()){
			hasVoted=resultSet.getBoolean(1);
			System.out.println("Inside hasVoted ResultSet If clause, hasVoted="+hasVoted);
		}
		resultSet.close();
		statement.close();

		if(!hasVoted){
			String sqlGetResult = "select count from result where event_id=? and candidate_id=?";
			PreparedStatement statement0 = connection.prepareStatement(sqlGetResult);
			statement0.setString(1, eventId);
			statement0.setString(2, candidateId);

			ResultSet resultSet0 = statement0.executeQuery();
			if(!resultSet0.next()){
				System.out.println("Inside If block");
				String sqlAddResult = "insert into result values (?,?,?)";
				PreparedStatement statement1 = connection.prepareStatement(sqlAddResult);
				statement1.setString(1, eventId);
				statement1.setString(2, candidateId);
				statement1.setInt(3, 1);
				statement1.executeUpdate();
				statement1.close();
			}else{
				System.out.println("Inside else");
				System.out.println("Rows fetched: "+resultSet0.getInt(1));

				String sqlPostVote = "update result set count=? where event_id=? and candidate_id=?";
				PreparedStatement statement2 = connection.prepareStatement(sqlPostVote);
				statement2.setInt(1,resultSet0.getInt(1)+1);
				statement2.setString(2, eventId);
				statement2.setString(3, candidateId);
				statement2.executeUpdate();
				statement2.close();
			}
			resultSet0.close();
			statement0.close();
			acknowledgeVoter(connection, voterId);
		}
	}

	private void acknowledgeVoter(Connection connection, String voterId) throws SQLException {
		String sqlAcknowledgement = "update voter set voted=? where voter_id=?";
		PreparedStatement statement = connection.prepareStatement(sqlAcknowledgement);
		statement.setBoolean(1, true);
		statement.setString(2, voterId);
		statement.executeUpdate();
		statement.close();
		System.out.println("Voter Acknowledged");
	}
}
