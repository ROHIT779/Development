package com.example.votingvoter.jdbc;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class TestJDBCManager {

  @Autowired private JDBCProperties jdbcProperties;

  public String deleteAllData() throws SQLException {
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
      throw exception;
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
