package com.example.votingvoter.jdbc;

import com.example.votingvoter.model.Vote;
import com.example.votingvoter.model.Voter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.sql.*;
import java.util.Arrays;

@Service
public class JDBCManager {

    @Autowired
    private JDBCProperties jdbcProperties;

    @Autowired
    public JDBCManager(JDBCProperties jdbcProperties){
        this.jdbcProperties=jdbcProperties;
        System.out.println("JDBC URL: "+this.jdbcProperties.getDatabaseUrl());
        System.out.println("JDBC URL: "+this.jdbcProperties.getDatabaseName());
        System.out.println("JDBC URL: "+this.jdbcProperties.getUserName());
        System.out.println("JDBC URL: "+this.jdbcProperties.getPassword());

    }


    public void addVoter(Voter voter) {
        String voterId = voter.getVoterId();
        String voterName = voter.getVoterName();
        int rowsAffected = 0;

        Connection connection = null;
        try {
            // below two lines are used for connectivity.
            //Class.forName("com.mysql.cj.jdbc.Driver");
            connection = DriverManager.getConnection(
                    jdbcProperties.getDatabaseUrl()+"/"+jdbcProperties.getDatabaseName(),
                    jdbcProperties.getUserName(), jdbcProperties.getPassword());

            // mydb is database
            // mydbuser is name of database
            // mydbuser is password of database

            String sql = "insert into voter values (?,?,?,?)";
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setString(1, voterId);
            statement.setString(2, voterName);
            statement.setString(3, voter.getEventId());
            statement.setBoolean(4, false);

            rowsAffected = statement.executeUpdate();

            statement.close();
            connection.close();
        } catch (Exception exception) {
            System.out.println(Arrays.toString(exception.getStackTrace()));
        }
        System.out.println(rowsAffected + " rows affected addVoter.");
    }

    public Voter getVoter(String voterId){
        String voterName="";
        String eventId = "";
        Connection connection = null;
        try {
            // below two lines are used for connectivity.
            //Class.forName("com.mysql.cj.jdbc.Driver");
            connection = DriverManager.getConnection(
                    jdbcProperties.getDatabaseUrl()+"/"+jdbcProperties.getDatabaseName(),
                    jdbcProperties.getUserName(), jdbcProperties.getPassword());

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
            System.out.println(Arrays.toString(exception.getStackTrace()));
        }
        return new Voter(voterId, voterName, eventId, null);
    }

    public Voter postVote(String voterId, String eventId, Vote vote) throws SQLException {
        int rowsAffected = 0;

        Connection connection = null;
        try {
            // below two lines are used for connectivity.
            //Class.forName("com.mysql.cj.jdbc.Driver");
            connection = DriverManager.getConnection(
                    jdbcProperties.getDatabaseUrl()+"/"+jdbcProperties.getDatabaseName(),
                    jdbcProperties.getUserName(), jdbcProperties.getPassword());


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
            System.out.println(Arrays.toString(exception.getStackTrace()));
        }
        return acknowledgeVoter(voterId, eventId, vote, connection);
    }

    public String getCreatorFromEvent(String eventId){


        Connection connection = null;
        String creatorId="";
        try {
            // below two lines are used for connectivity.
            //Class.forName("com.mysql.cj.jdbc.Driver");
            connection = DriverManager.getConnection(
                    jdbcProperties.getDatabaseUrl()+"/"+jdbcProperties.getDatabaseName(),
                    jdbcProperties.getUserName(), jdbcProperties.getPassword());

            // mydb is database
            // mydbuser is name of database
            // mydbuser is password of database

            PreparedStatement statement;
            statement = connection.prepareStatement("select creator_id from event where event_id=?");
            statement.setString(1, eventId);
            ResultSet resultSet;
            resultSet = statement.executeQuery();
            while (resultSet.next()) {
                creatorId = resultSet.getString("creator_id").trim();
                System.out.println("creatorId : " + creatorId+" from eventId: "+eventId);
            }
            resultSet.close();
            statement.close();
            connection.close();
        }
        catch (Exception exception) {
            System.out.println(exception);
        }
        return creatorId;
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
    public boolean validateId(String eventId, String voterId, String candidateId){
        int count=0;
        System.out.println("eventId: "+ eventId);
        System.out.println("voterId: "+ voterId);
        Connection connection = null;
        try {
            // below two lines are used for connectivity.
            //Class.forName("com.mysql.cj.jdbc.Driver");
            connection = DriverManager.getConnection(
                    jdbcProperties.getDatabaseUrl()+"/"+jdbcProperties.getDatabaseName(),
                    jdbcProperties.getUserName(), jdbcProperties.getPassword());

            if(eventId!=null && voterId==null && candidateId==null){
                PreparedStatement statement;
                String validateEventQuery = "select count(*) from event where event_id=\'"+eventId+"\'";
                System.out.println(validateEventQuery);
                statement = connection.prepareStatement(validateEventQuery);
                ResultSet resultSet;
                resultSet = statement.executeQuery();
                while (resultSet.next()) {
                    count = resultSet.getInt(1);
                }
                System.out.println("Count: "+count);
                resultSet.close();
                statement.close();
            }else if(eventId!=null && voterId != null && candidateId==null){
                PreparedStatement statement;
                String validateVoterWithEvent = "select count(*) from voter where voter_id=\'"+voterId+"\' and event_id=\'"+eventId+"\'";
                System.out.println(validateVoterWithEvent);
                statement = connection.prepareStatement(validateVoterWithEvent);
                ResultSet resultSet;
                resultSet = statement.executeQuery();
                while (resultSet.next()) {
                    count = resultSet.getInt(1);
                }
                System.out.println("Count: "+count);
                resultSet.close();
                statement.close();
            }else if(eventId!=null && voterId==null && candidateId!=null){
                PreparedStatement statement;
                String validateCandidateWithEvent = "select count(*) from nomination where candidate_id=\'"+candidateId+"\' and event_id=\'"+eventId+"\'";
                System.out.println(validateCandidateWithEvent);
                statement = connection.prepareStatement(validateCandidateWithEvent);
                ResultSet resultSet;
                resultSet = statement.executeQuery();
                while (resultSet.next()) {
                    count = resultSet.getInt(1);
                }
                System.out.println("Count: "+count);
                resultSet.close();
                statement.close();
            }
            connection.close();
        }
        catch (SQLException exception) {
            System.out.println(Arrays.toString(exception.getStackTrace()));
        }
        if(count>0){
            return true;
        }else{
            return false;
        }
    }
}
