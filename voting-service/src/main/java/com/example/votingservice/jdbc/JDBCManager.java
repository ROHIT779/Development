package com.example.votingservice.jdbc;

import com.example.votingservice.model.*;
import java.sql.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
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

  public EventWithNomination getEvent(String eventId) {
    int count = 0;
    Event event = new Event();
    List<Candidate> candidateList = new ArrayList<>();

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

      statement =
          connection.prepareStatement(
              "select * from event left join nomination using (event_id) left join candidate using (candidate_id) where event_id=?");
      statement.setString(1, eventId);
      resultSet = statement.executeQuery();
      boolean isFirstRow = true;
      while (resultSet.next()) {
        System.out.println(resultSet.getString("candidate_id"));
        System.out.println(resultSet.getString("candidate_name"));
        System.out.println(resultSet.getString("candidate_info"));

        Candidate candidate =
            new Candidate(
                resultSet.getString("candidate_id"),
                resultSet.getString("candidate_name"),
                resultSet.getString("candidate_info"));
        candidateList.add(candidate);
        if (isFirstRow) {
          event =
              new Event(
                  resultSet.getString("event_id"),
                  resultSet.getString("event_name"),
                  resultSet.getString("event_info"),
                  resultSet.getString("creator_id"));
          isFirstRow = false;
        }
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
    return new EventWithNomination(
        event.getEventId(),
        event.getEventName(),
        event.getEventInfo(),
        event.getCreatorId(),
        candidateList);
  }

  public VotingResult getResult(String eventId) {
    VotingResult votingResult = new VotingResult();
    List<CandidateResult> candidateResults = new ArrayList<>();
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

      statement =
          connection.prepareStatement(
              "select * from result left join candidate using (candidate_id) where event_id=?");
      statement.setString(1, eventId);
      resultSet = statement.executeQuery();
      while (resultSet.next()) {
        System.out.println(resultSet.getString("candidate_id"));
        System.out.println(resultSet.getString("candidate_name"));
        System.out.println(resultSet.getString("candidate_info"));

        Candidate candidate =
            new Candidate(
                resultSet.getString("candidate_id"),
                resultSet.getString("candidate_name"),
                resultSet.getString("candidate_info"));
        candidateResults.add(new CandidateResult(resultSet.getInt("count"), candidate));
      }
      votingResult.setEventId(eventId);
      votingResult.setFinalResult(candidateResults);
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
    return votingResult;
  }

  public boolean validateId(String idType, String value) {
    int count = 0;
    System.out.println("ID type: " + idType);
    System.out.println("ID value: " + value);
    String tableName = idType.substring(0, idType.length() - 3);
    System.out.println("Table name is: " + tableName);
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

      String validateQuery =
          "select count(*) from " + tableName + " where " + idType + "=" + "\'" + value + "\'";
      System.out.println(validateQuery);
      statement = connection.prepareStatement(validateQuery);
      resultSet = statement.executeQuery();
      while (resultSet.next()) {
        count = resultSet.getInt(1);
      }
      System.out.println("Count: " + count);
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

  public String deleteAllData() {
    String[] tables = {"result", "voter", "nomination", "candidate", "event", "creator"};
    String output = "";
    Connection connection = null;
    PreparedStatement statement = null;
    try {
      connection =
          DriverManager.getConnection(
              jdbcProperties.getDatabaseUrl() + "/" + jdbcProperties.getDatabaseName(),
              jdbcProperties.getUserName(),
              jdbcProperties.getPassword());
      for (String table : tables) {
        String deleteQuery = "delete from " + table;
        System.out.println(deleteQuery);
        statement = connection.prepareStatement(deleteQuery);
        int rowsAffected = 0;
        rowsAffected = statement.executeUpdate();
        output += rowsAffected + " rows affected in table: " + table + "\n";
      }
    } catch (SQLException exception) {
      throw new RuntimeException(exception);
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
    System.out.println(output);
    return output;
  }
}
