package com.example.votingvoter.jdbc;

import com.example.votingvoter.model.Vote;
import com.example.votingvoter.model.Voter;
import java.sql.*;
import java.util.Arrays;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class JDBCManager {

  @Autowired private JDBCProperties jdbcProperties;

  @Autowired
  public JDBCManager(JDBCProperties jdbcProperties) {
    this.jdbcProperties = jdbcProperties;
    System.out.println("JDBC URL: " + this.jdbcProperties.getDatabaseUrl());
    System.out.println("JDBC URL: " + this.jdbcProperties.getDatabaseName());
    System.out.println("JDBC URL: " + this.jdbcProperties.getUserName());
    System.out.println("JDBC URL: " + this.jdbcProperties.getPassword());
  }

  public String addVoter(Voter voter) {
    String voterId = "";
    String voterName = voter.getVoterName();
    int rowsAffected = 0;

    Connection connection = null;
    PreparedStatement statement = null;
    ResultSet resultSet = null;
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

      String sql = "insert into voter (voter_name, event_id, voted) values (?,?,?)";
      statement = connection.prepareStatement(sql, new String[] {"voter_id"});
      statement.setString(1, voterName);
      statement.setString(2, voter.getEventId());
      statement.setBoolean(3, false);
      rowsAffected = statement.executeUpdate();
      resultSet = statement.getGeneratedKeys();
      if (resultSet.next()) {
        voterId = String.valueOf(resultSet.getLong(1));
      }
    } catch (Exception exception) {
      System.out.println(Arrays.toString(exception.getStackTrace()));
    } finally {
      try {
        if (resultSet != null) {
          resultSet.close();
        }
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
    System.out.println(rowsAffected + " rows affected addVoter.");
    return voterId;
  }

  public Voter getVoter(String voterId) {
    String voterName = "";
    String eventId = "";
    Connection connection = null;
    PreparedStatement statement = null;
    ResultSet resultSet = null;
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

      statement = connection.prepareStatement("select * from voter where voter_id=?");
      statement.setString(1, voterId);
      resultSet = statement.executeQuery();
      while (resultSet.next()) {
        voterId = resultSet.getString("voter_id").trim();
        voterName = resultSet.getString("voter_name").trim();
        eventId = resultSet.getString("event_id").trim();

        System.out.println(
            "voterId : " + voterId + " voterName : " + voterName + "eventId: " + eventId);
      }
    } catch (Exception exception) {
      System.out.println(Arrays.toString(exception.getStackTrace()));
    } finally {
      try {
        if (resultSet != null) {
          resultSet.close();
        }
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
    return new Voter(voterId, voterName, eventId, null);
  }

  public Voter postVote(String voterId, String eventId, Vote vote) throws SQLException {
    int rowsAffected = 0;
    System.out.println("VoterID in postVote(): " + voterId);
    Voter voter = null;
    Connection connection = null;
    PreparedStatement statement = null;
    ResultSet resultSet = null;
    try {
      // below two lines are used for connectivity.
      // Class.forName("com.mysql.cj.jdbc.Driver");
      connection =
          DriverManager.getConnection(
              jdbcProperties.getDatabaseUrl() + "/" + jdbcProperties.getDatabaseName(),
              jdbcProperties.getUserName(),
              jdbcProperties.getPassword());

      Boolean hasVoted = hasVoted(connection, statement, resultSet, voterId);
      if (!hasVoted) {
        resultSet = getCountForCandidate(connection, statement, resultSet, vote, eventId);
        if (!resultSet.next()) {
          System.out.println("Inside If block");
          addFirstVoteForCandidate(connection, statement, vote, eventId);
        } else {
          System.out.println("Inside else");
          addVoteForCandidate(connection, statement, resultSet, vote, eventId);
        }
        voter = acknowledgeVoter(connection, statement, voterId, eventId, vote);
        return voter;
      }
    } catch (Exception exception) {
      System.out.println(Arrays.toString(exception.getStackTrace()));
    } finally {
      try {
        if (resultSet != null) {
          resultSet.close();
        }
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
    Logger.getAnonymousLogger().log(Level.WARNING, "Voter " + voterId + " already voted!");
    return null;
  }

  public String getCreatorFromEvent(String eventId) {
    Connection connection = null;
    PreparedStatement statement = null;
    ResultSet resultSet = null;
    String creatorId = "";
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

      statement = connection.prepareStatement("select creator_id from event where event_id=?");
      statement.setString(1, eventId);
      resultSet = statement.executeQuery();
      while (resultSet.next()) {
        creatorId = resultSet.getString("creator_id").trim();
        System.out.println("creatorId : " + creatorId + " from eventId: " + eventId);
      }
    } catch (Exception exception) {
      System.out.println(exception);
    } finally {
      try {
        if (resultSet != null) {
          resultSet.close();
        }
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
    return creatorId;
  }

  private Voter acknowledgeVoter(
      Connection connection,
      PreparedStatement statement,
      String voterId,
      String eventId,
      Vote vote) {
    String sqlAcknowledgement = "update voter set voted=? where voter_id=?";
    try {
      statement = connection.prepareStatement(sqlAcknowledgement);
      statement.setBoolean(1, true);
      statement.setString(2, voterId);
      statement.executeUpdate();
    } catch (SQLException exception) {
      System.out.println(exception);
    }
    System.out.println("Voter Acknowledged");
    return new Voter(voterId, getVoter(voterId).getVoterName(), eventId, vote);
  }

  public boolean validateId(String eventId, String voterId, String candidateId) {
    int count = 0;
    System.out.println("eventId: " + eventId);
    System.out.println("voterId: " + voterId);
    Connection connection = null;
    PreparedStatement statement = null;
    ResultSet resultSet = null;
    try {
      // below two lines are used for connectivity.
      // Class.forName("com.mysql.cj.jdbc.Driver");
      connection =
          DriverManager.getConnection(
              jdbcProperties.getDatabaseUrl() + "/" + jdbcProperties.getDatabaseName(),
              jdbcProperties.getUserName(),
              jdbcProperties.getPassword());

      if (eventId != null && voterId == null && candidateId == null) {
        resultSet = validateEventId(connection, statement, resultSet, eventId);
        while (resultSet.next()) {
          count = resultSet.getInt(1);
        }
        System.out.println("Count: " + count);
      } else if (eventId != null && voterId != null && candidateId == null) {
        resultSet = validateEventIdWithVoterId(connection, statement, resultSet, eventId, voterId);
        while (resultSet.next()) {
          count = resultSet.getInt(1);
        }
        System.out.println("Count: " + count);
      } else if (eventId != null && voterId == null && candidateId != null) {
        resultSet =
            validateEventIdWithCandidateId(connection, statement, resultSet, eventId, candidateId);
        while (resultSet.next()) {
          count = resultSet.getInt(1);
        }
        System.out.println("Count: " + count);
      }
    } catch (SQLException exception) {
      System.out.println(Arrays.toString(exception.getStackTrace()));
    } finally {
      try {
        if (resultSet != null) {
          resultSet.close();
        }
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
    if (count > 0) {
      return true;
    } else {
      return false;
    }
  }

  private ResultSet validateEventId(
      Connection connection, PreparedStatement statement, ResultSet resultSet, String eventId) {
    String validateEventQuery = "select count(*) from event where event_id=\'" + eventId + "\'";
    System.out.println(validateEventQuery);
    try {
      statement = connection.prepareStatement(validateEventQuery);
      resultSet = statement.executeQuery();
    } catch (SQLException exception) {
      System.out.println(exception);
    }
    return resultSet;
  }

  private ResultSet validateEventIdWithVoterId(
      Connection connection,
      PreparedStatement statement,
      ResultSet resultSet,
      String eventId,
      String voterId) {
    String validateVoterWithEvent =
        "select count(*) from voter where voter_id=\'"
            + voterId
            + "\' and event_id=\'"
            + eventId
            + "\'";
    System.out.println(validateVoterWithEvent);
    try {
      statement = connection.prepareStatement(validateVoterWithEvent);
      resultSet = statement.executeQuery();
    } catch (SQLException exception) {
      System.out.println(exception);
    }
    return resultSet;
  }

  private ResultSet validateEventIdWithCandidateId(
      Connection connection,
      PreparedStatement statement,
      ResultSet resultSet,
      String eventId,
      String candidateId) {
    String validateCandidateWithEvent =
        "select count(*) from nomination where candidate_id=\'"
            + candidateId
            + "\' and event_id=\'"
            + eventId
            + "\'";
    System.out.println(validateCandidateWithEvent);
    try {
      statement = connection.prepareStatement(validateCandidateWithEvent);
      resultSet = statement.executeQuery();
    } catch (SQLException exception) {
      System.out.println(exception);
    }
    return resultSet;
  }

  private boolean hasVoted(
      Connection connection, PreparedStatement statement, ResultSet resultSet, String voterId) {
    boolean hasVoted = false;
    try {
      String sqlVoterAlreadyVoted = "select voted from voter where voter_id=?";
      statement = connection.prepareStatement(sqlVoterAlreadyVoted);
      statement.setString(1, voterId);
      resultSet = statement.executeQuery();
      if (resultSet.next()) {
        hasVoted = resultSet.getBoolean(1);
        System.out.println("Inside hasVoted ResultSet If clause, hasVoted=" + hasVoted);
      }
    } catch (SQLException exception) {
      System.out.println(exception);
    }
    return hasVoted;
  }

  private ResultSet getCountForCandidate(
      Connection connection,
      PreparedStatement statement,
      ResultSet resultSet,
      Vote vote,
      String eventId) {
    try {
      String sqlGetResult = "select count from result where event_id=? and candidate_id=?";
      statement = connection.prepareStatement(sqlGetResult);
      statement.setString(1, eventId);
      statement.setString(2, vote.getCandidateId());
      resultSet = statement.executeQuery();
    } catch (SQLException exception) {
      System.out.println(exception);
    }
    return resultSet;
  }

  private void addFirstVoteForCandidate(
      Connection connection, PreparedStatement statement, Vote vote, String eventId) {
    String sqlAddResult = "insert into result values (?,?,?)";
    try {
      statement = connection.prepareStatement(sqlAddResult);
      statement.setString(1, eventId);
      statement.setString(2, vote.getCandidateId());
      statement.setInt(3, 1);
      statement.executeUpdate();
    } catch (SQLException exception) {
      System.out.println(exception);
    }
  }

  private void addVoteForCandidate(
      Connection connection,
      PreparedStatement statement,
      ResultSet resultSet,
      Vote vote,
      String eventId) {
    try {
      System.out.println("Rows fetched: " + resultSet.getInt(1));
      String sqlPostVote = "update result set count=? where event_id=? and candidate_id=?";
      statement = connection.prepareStatement(sqlPostVote);
      statement.setInt(1, resultSet.getInt(1) + 1);
      statement.setString(2, eventId);
      statement.setString(3, vote.getCandidateId());
      statement.executeUpdate();
    } catch (SQLException exception) {
      System.out.println(exception);
    }
  }
}
