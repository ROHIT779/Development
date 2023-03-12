package com.example.votingvoter.jdbc;

import com.example.votingvoter.model.Vote;
import com.example.votingvoter.model.Voter;
import org.springframework.beans.factory.annotation.Value;

import java.sql.*;

public class JDBCManager {

    private String databaseUrl;

    private String databaseName;

    private String userName;

    private String password;

    public JDBCManager(){

    }

    public JDBCManager(String databaseUrl, String databaseName, String userName, String password) {
        this.databaseUrl = databaseUrl;
        this.databaseName = databaseName;
        this.userName = userName;
        this.password = password;
    }

    public void addVoter(Voter voter) {
        String voterId = voter.getVoterId();
        String voterName = voter.getVoterName();
        int rowsAffected = 0;

        Connection connection = null;
        try {
            // below two lines are used for connectivity.
            Class.forName("com.mysql.cj.jdbc.Driver");
            connection = DriverManager.getConnection(
                    databaseUrl+databaseName,
                    userName, password);

            // mydb is database
            // mydbuser is name of database
            // mydbuser is password of database

            String sql = "insert into voter values (?,?,?,?)";
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setString(1, voterId);
            statement.setString(2, voterName);
            statement.setBoolean(3, false);
            statement.setString(4, voter.getEventId());

            rowsAffected = statement.executeUpdate();

            statement.close();
            connection.close();
        } catch (Exception exception) {
            System.out.println(exception);
        }
        System.out.println(rowsAffected + " rows affected addVoter.");
    }

    public Voter getVoter(String voterId){
        String voterName="";
        String eventId = "";
        Connection connection = null;
        try {
            // below two lines are used for connectivity.
            Class.forName("com.mysql.cj.jdbc.Driver");
            connection = DriverManager.getConnection(
                    databaseUrl+databaseName,
                    userName, password);

            // mydb is database
            // mydbuser is name of database
            // mydbuser is password of database

            PreparedStatement statement;
            statement = connection.prepareStatement("select * from voter where voter_id=?");
            statement.setString(1, voterId);
            ResultSet resultSet;
            resultSet = statement.executeQuery();
            while (resultSet.next()) {
                voterId = resultSet.getString("voter_id").trim();
                voterName = resultSet.getString("voter_name").trim();
                eventId = resultSet.getString("event_id").trim();

                System.out.println("voterId : " + voterId
                        + " voterName : " + voterName + "eventId: " + eventId);
            }
            resultSet.close();
            statement.close();
            connection.close();
        }
        catch (Exception exception) {
            System.out.println(exception);
        }
        return new Voter(voterId, voterName, eventId, null);
    }

    public Voter postVote(String voterId, String eventId, Vote vote) throws SQLException {
        int rowsAffected = 0;

        Connection connection = null;
        try {
            // below two lines are used for connectivity.
            Class.forName("com.mysql.cj.jdbc.Driver");
            connection = DriverManager.getConnection(
                    databaseUrl+databaseName,
                    userName, password);


            String sqlGetResult = "select count from result where event_id=? and candidate_id=?";
            PreparedStatement statement = connection.prepareStatement(sqlGetResult);
            statement.setString(1, eventId);
            statement.setString(2, vote.getCandidateId());

            ResultSet resultSet;
            resultSet = statement.executeQuery();
            if(!resultSet.next()){
                System.out.println("Inside If block");
                String sqlAddResult = "insert into result values (?,?,?)";
                PreparedStatement statement1 = connection.prepareStatement(sqlAddResult);
                statement1.setString(1, eventId);
                statement1.setString(2, vote.getCandidateId());
                statement1.setInt(3, 1);
                statement1.executeUpdate();
                statement1.close();
            }else{
                System.out.println("Inside else");
                    System.out.println("Rows fetched: "+resultSet.getInt(1));

                    String sqlPostVote = "update result set count=? where event_id=? and candidate_id=?";
                PreparedStatement statement2 = connection.prepareStatement(sqlPostVote);
                statement2.setInt(1,resultSet.getInt(1)+1);
                statement2.setString(2, eventId);
                statement2.setString(3, vote.getCandidateId());
                statement2.executeUpdate();
                statement2.close();
            }

            resultSet.close();
            statement.close();
        } catch (Exception exception) {
            System.out.println(exception);
        }
        return acknowledgeVoter(voterId, eventId, vote, connection);
    }

    private Voter acknowledgeVoter(String voterId, String eventId, Vote vote, Connection connection) throws SQLException {
        String sqlAcknowledgement = "update voter set voted=? where voter_id=?";
        PreparedStatement statement = connection.prepareStatement(sqlAcknowledgement);
        statement.setBoolean(1, true);
        statement.setString(2, voterId);
        statement.executeUpdate();
        statement.close();
        connection.close();
        System.out.println("Voter Acknowledged");
        return new Voter(voterId,getVoter(voterId).getVoterName(), eventId, vote);
    }
    public boolean validateId(String idType, String value){
        int count=0;
        System.out.println("ID type: "+ idType);
        System.out.println("ID value: "+ value);
        String tableName = idType.substring(0, idType.length()-3);
        System.out.println("Table name is: "+tableName);
        Connection connection = null;
        try {
            // below two lines are used for connectivity.
            Class.forName("com.mysql.cj.jdbc.Driver");
            connection = DriverManager.getConnection(
                    databaseUrl+databaseName,
                    userName, password);

            // mydb is database
            // mydbuser is name of database
            // mydbuser is password of database

            PreparedStatement statement;
            String validateQuery = "select count(*) from "+tableName+" where "+idType+"="+"\""+value+"\"";
            System.out.println(validateQuery);
            statement = connection.prepareStatement(validateQuery);
            ResultSet resultSet;
            resultSet = statement.executeQuery();
            while (resultSet.next()) {
                count = resultSet.getInt(1);
            }
            System.out.println("Count: "+count);
            resultSet.close();
            statement.close();
            connection.close();
        }
        catch (SQLException | ClassNotFoundException exception) {
            System.out.println(exception);
        }
        if(count>0){
            return true;
        }else{
            return false;
        }
    }
}
