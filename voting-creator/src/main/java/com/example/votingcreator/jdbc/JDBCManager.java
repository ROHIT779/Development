package com.example.votingcreator.jdbc;

import com.example.votingcreator.model.Candidate;
import com.example.votingcreator.model.Creator;
import com.example.votingcreator.model.Event;
import com.example.votingcreator.model.Nomination;

import java.sql.*;
import java.util.List;

public class JDBCManager {


    public static void addCreator(Creator creator){
        String creatorId = creator.getCreatorId();
        String creatorName = creator.getCreatorName();
        String creatorInfo = creator.getCreatorInfo();
        int rowsAffected = 0;

        Connection connection = null;
        try {
            // below two lines are used for connectivity.
            Class.forName("com.mysql.cj.jdbc.Driver");
            connection = DriverManager.getConnection(
                    "jdbc:mysql://localhost:3306/voting_application",
                    "root", "root");

            // mydb is database
            // mydbuser is name of database
            // mydbuser is password of database

            String sql = "insert into creator values (?,?,?)";
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setString(1, creatorId);
            statement.setString(2, creatorName);
            statement.setString(3, creatorInfo);
            rowsAffected = statement.executeUpdate();

            statement.close();
            connection.close();
        }
        catch (Exception exception) {
            System.out.println(exception);
        }
        System.out.println(rowsAffected + " rows affected.");
    }

    public static Creator getCreator(String creatorId){
        String creatorName="";
        String creatorInfo="";
        Connection connection = null;
        try {
            // below two lines are used for connectivity.
            Class.forName("com.mysql.cj.jdbc.Driver");
            connection = DriverManager.getConnection(
                    "jdbc:mysql://localhost:3306/voting_application",
                    "root", "root");

            // mydb is database
            // mydbuser is name of database
            // mydbuser is password of database

            PreparedStatement statement;
            statement = connection.prepareStatement("select * from creator where creator_id=?");
            statement.setString(1, creatorId);
            ResultSet resultSet;
            resultSet = statement.executeQuery();
            while (resultSet.next()) {
                creatorId = resultSet.getString("creator_id").trim();
                creatorName = resultSet.getString("creator_name").trim();
                creatorInfo = resultSet.getString("creator_info").trim();

                System.out.println("creatorId : " + creatorId
                        + " creatorName : " + creatorName + "creatorInfo" + creatorInfo);
            }
            resultSet.close();
            statement.close();
            connection.close();
        }
        catch (Exception exception) {
            System.out.println(exception);
        }
        return new Creator(creatorId, creatorName, creatorInfo);
    }

    public static void addEvent(Event event){

        String eventId = event.getEventId();
        String eventName = event.getEventName();
        String eventInfo = event.getEventInfo();
        String creatorId = event.getCreatorId();
        int rowsAffected = 0;

        Connection connection = null;
        try {
            // below two lines are used for connectivity.
            Class.forName("com.mysql.cj.jdbc.Driver");
            connection = DriverManager.getConnection(
                    "jdbc:mysql://localhost:3306/voting_application",
                    "root", "root");

            // mydb is database
            // mydbuser is name of database
            // mydbuser is password of database

            String sql = "insert into event values (?,?,?,?)";
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setString(1, eventId);
            statement.setString(2, eventName);
            statement.setString(3, eventInfo);
            statement.setString(4, creatorId);
            rowsAffected = statement.executeUpdate();

            statement.close();
            connection.close();
        }
        catch (Exception exception) {
            System.out.println(exception);
        }
        System.out.println(rowsAffected + " rows affected.");
    }

    public static Event getEvent(String creatorId, String eventId){
        String eventName="";
        String eventInfo="";
        Connection connection = null;
        try {
            // below two lines are used for connectivity.
            Class.forName("com.mysql.cj.jdbc.Driver");
            connection = DriverManager.getConnection(
                    "jdbc:mysql://localhost:3306/voting_application",
                    "root", "root");

            // mydb is database
            // mydbuser is name of database
            // mydbuser is password of database

            PreparedStatement statement;
            statement = connection.prepareStatement("select * from event where event_id=? and creator_id=?");
            statement.setString(1, eventId);
            statement.setString(2, creatorId);
            ResultSet resultSet;
            resultSet = statement.executeQuery();
            while (resultSet.next()) {
                eventId = resultSet.getString("event_id").trim();
                eventName = resultSet.getString("event_name").trim();
                eventInfo = resultSet.getString("event_info").trim();
                creatorId = resultSet.getString("creator_id").trim();


                System.out.println("eventId : " + eventId
                        + " eventName : " + eventName + " eventInfo" + eventInfo
                + " creatorId: " + creatorId);
            }
            resultSet.close();
            statement.close();
            connection.close();
        }
        catch (Exception exception) {
            System.out.println(exception);
        }
        return new Event(eventId, eventName, eventInfo, creatorId);
    }

    public static void addNomination(String creatorId, Nomination nomination){
        int rowsAffected = 0;
        Connection connection = null;
        if(addCandidates(nomination.getCandidateList())){
            try {
                // below two lines are used for connectivity.
                Class.forName("com.mysql.cj.jdbc.Driver");
                connection = DriverManager.getConnection(
                        "jdbc:mysql://localhost:3306/voting_application",
                        "root", "root");
                for(Candidate candidate : nomination.getCandidateList()){
                    String sql = "insert into nomination values (?,?)";
                    PreparedStatement statement = connection.prepareStatement(sql);
                    statement.setString(1, nomination.getEventId());
                    statement.setString(2, candidate.getCandidateId());
                    statement.executeUpdate();
                    statement.close();
                    rowsAffected++;
                }
                connection.close();
            }
            catch (Exception exception) {
                System.out.println(exception);
            }
            System.out.println(rowsAffected + " rows affected for nomination.");
        }
    }

    private static boolean addCandidates(List<Candidate> candidateList){
        int rowsAffected = 0;

        Connection connection = null;
        try {
            // below two lines are used for connectivity.
            Class.forName("com.mysql.cj.jdbc.Driver");
            connection = DriverManager.getConnection(
                    "jdbc:mysql://localhost:3306/voting_application",
                    "root", "root");

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
            connection.close();
        }
        catch (Exception exception) {
            System.out.println(exception);
        }
        System.out.println(rowsAffected + " rows affected for candidate");
        return candidateList.size() == rowsAffected ? true : false;
    }
}
