package com.example.votingcreator.jdbc;

import com.example.votingcreator.model.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.sql.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

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

    public void addCreator(Creator creator){
        String creatorId = creator.getCreatorId();
        String creatorName = creator.getCreatorName();
        String creatorInfo = creator.getCreatorInfo();
        int rowsAffected = 0;

        Connection connection = null;
        PreparedStatement statement = null;
        try {
            // below two lines are used for connectivity.
            //Class.forName("com.mysql.cj.jdbc.Driver");
            connection = DriverManager.getConnection(
                    jdbcProperties.getDatabaseUrl()+"/"+jdbcProperties.getDatabaseName(),
                    jdbcProperties.getUserName(), jdbcProperties.getPassword());

            // mydb is database
            // mydbuser is name of database
            // mydbuser is password of database

            String sql = "insert into creator values (?,?,?)";
            statement = connection.prepareStatement(sql);
            statement.setString(1, creatorId);
            statement.setString(2, creatorName);
            statement.setString(3, creatorInfo);
            rowsAffected = statement.executeUpdate();

        } catch (Exception exception) {
            System.out.println(exception);
        }finally{
            try{
                if(statement != null){
                    statement.close();
                }
                if(connection != null){
                    connection.close();
                }
            }catch(SQLException exception){
                System.out.println(exception);
            }
        }
        System.out.println(rowsAffected + " rows affected.");
    }

    public Creator getCreator(String creatorId){


        String creatorName="";
        String creatorInfo="";
        Creator creator=null;
        Connection connection = null;
        PreparedStatement statement = null;
        ResultSet resultSet = null;
        try {
            // below two lines are used for connectivity.
            //Class.forName("com.mysql.cj.jdbc.Driver");
            connection = DriverManager.getConnection(
                    jdbcProperties.getDatabaseUrl()+"/"+jdbcProperties.getDatabaseName(),
                    jdbcProperties.getUserName(), jdbcProperties.getPassword());

            // mydb is database
            // mydbuser is name of database
            // mydbuser is password of database

            statement = connection.prepareStatement("select * from creator where creator_id=?");
            statement.setString(1, creatorId);
            resultSet = statement.executeQuery();
            while (resultSet.next()) {
                creatorId = resultSet.getString("creator_id").trim();
                creatorName = resultSet.getString("creator_name").trim();
                creatorInfo = resultSet.getString("creator_info").trim();

                System.out.println("creatorId : " + creatorId
                        + " creatorName : " + creatorName + "creatorInfo" + creatorInfo);
            }
            creator=new Creator(creatorId, creatorName, creatorInfo);
            statement = connection.prepareStatement("select event_id from event where creator_id=?");
            statement.setString(1, creatorId);
            resultSet = statement.executeQuery();
            List<String> eventIds=new ArrayList<>();
            while (resultSet.next()) {
                String eventId = resultSet.getString("event_id").trim();
                System.out.println("eventId : " + eventId);
                eventIds.add(eventId);
            }
            for(String eventId: eventIds){
                EventWithNomination eventWithNomination=getNominations(eventId);
                creator.addEventWithNomination(eventWithNomination);
            }
        }
        catch (Exception exception) {
            System.out.println(exception);
        }finally{
            try{
                if(resultSet != null){
                    resultSet.close();
                }
                if(statement != null){
                    statement.close();
                }
                if(connection != null){
                    connection.close();
                }
            }catch(SQLException exception){
                System.out.println(exception);
            }
        }
        return creator;
    }

    public void addEvent(Event event){

        String eventId = event.getEventId();
        String eventName = event.getEventName();
        String eventInfo = event.getEventInfo();
        String creatorId = event.getCreatorId();
        int rowsAffected = 0;

        Connection connection = null;
        PreparedStatement statement = null;
        try {
            // below two lines are used for connectivity.
            //Class.forName("com.mysql.cj.jdbc.Driver");
            connection = DriverManager.getConnection(
                    jdbcProperties.getDatabaseUrl()+"/"+jdbcProperties.getDatabaseName(),
                    jdbcProperties.getUserName(), jdbcProperties.getPassword());

            // mydb is database
            // mydbuser is name of database
            // mydbuser is password of database

            String sql = "insert into event values (?,?,?,?)";
            statement = connection.prepareStatement(sql);
            statement.setString(1, eventId);
            statement.setString(2, eventName);
            statement.setString(3, eventInfo);
            statement.setString(4, creatorId);
            rowsAffected = statement.executeUpdate();
        }
        catch (Exception exception) {
            System.out.println(exception);
        }finally{
            try{
                if(statement != null){
                    statement.close();
                }
                if(connection != null){
                    connection.close();
                }
            }catch(SQLException exception){
                System.out.println(exception);
            }
        }
        System.out.println(rowsAffected + " rows affected.");
    }

    public Event getEvent(String creatorId, String eventId){
        String eventName="";
        String eventInfo="";
        Connection connection = null;
        PreparedStatement statement = null;
        ResultSet resultSet = null;
        try {
            // below two lines are used for connectivity.
            //Class.forName("com.mysql.cj.jdbc.Driver");
            connection = DriverManager.getConnection(
                    jdbcProperties.getDatabaseUrl()+"/"+jdbcProperties.getDatabaseName(),
                    jdbcProperties.getUserName(), jdbcProperties.getPassword());

            // mydb is database
            // mydbuser is name of database
            // mydbuser is password of database

            statement = connection.prepareStatement("select * from event where event_id=? and creator_id=?");
            statement.setString(1, eventId);
            statement.setString(2, creatorId);
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
        }
        catch (Exception exception) {
            System.out.println(exception);
        }finally{
            try{
                if(resultSet != null){
                    resultSet.close();
                }
                if(statement != null){
                    statement.close();
                }
                if(connection != null){
                    connection.close();
                }
            }catch(SQLException exception){
                System.out.println(exception);
            }
        }
        return new Event(eventId, eventName, eventInfo, creatorId);
    }

    public void addNomination(String creatorId, Nomination nomination){
        int rowsAffected = 0;
        Connection connection = null;
        PreparedStatement statement = null;
        if(addCandidates(nomination.getCandidateList())){
            try {
                // below two lines are used for connectivity.
                //Class.forName("com.mysql.cj.jdbc.Driver");
                connection = DriverManager.getConnection(
                        jdbcProperties.getDatabaseUrl()+"/"+jdbcProperties.getDatabaseName(),
                        jdbcProperties.getUserName(), jdbcProperties.getPassword());
                for(Candidate candidate : nomination.getCandidateList()){
                    String sql = "insert into nomination values (?,?)";
                    statement = connection.prepareStatement(sql);
                    statement.setString(1, candidate.getCandidateId());
                    statement.setString(2, nomination.getEventId());
                    statement.executeUpdate();
                    rowsAffected++;
                }
            }
            catch (Exception exception) {
                System.out.println(exception);
            }finally{
                try{
                    if(statement != null){
                        statement.close();
                    }
                    if(connection != null){
                        connection.close();
                    }
                }catch(SQLException exception){
                    System.out.println(exception);
                }
            }
            System.out.println(rowsAffected + " rows affected for nomination.");
        }
    }

    private boolean addCandidates(List<Candidate> candidateList){
        int rowsAffected = 0;

        Connection connection = null;
        PreparedStatement statement = null;
        try {
            // below two lines are used for connectivity.
            //Class.forName("com.mysql.cj.jdbc.Driver");
            connection = DriverManager.getConnection(
                    jdbcProperties.getDatabaseUrl()+"/"+jdbcProperties.getDatabaseName(),
                    jdbcProperties.getUserName(), jdbcProperties.getPassword());

            for(Candidate candidate : candidateList){
                String sql = "insert into candidate values (?,?,?)";
                statement = connection.prepareStatement(sql);
                statement.setString(1, candidate.getCandidateId());
                statement.setString(2, candidate.getCandidateName());
                statement.setString(3, candidate.getCandidateInfo());
                statement.executeUpdate();
                rowsAffected++;
            }
        }
        catch (Exception exception) {
            System.out.println(exception);
        }finally{
            try{
                if(statement != null){
                    statement.close();
                }
                if(connection != null){
                    connection.close();
                }
            }catch(SQLException exception){
                System.out.println(exception);
            }
        }
        System.out.println(rowsAffected + " rows affected for candidate");
        return candidateList.size() == rowsAffected ? true : false;
    }

    public EventWithNomination getNominations(String eventId){
        int count = 0;
        Event event = new Event();
        List<Candidate> candidateList = new ArrayList<>();

        Connection connection = null;
        PreparedStatement statement = null;
        ResultSet resultSet = null;
        try {
            // below two lines are used for connectivity.
            //Class.forName("com.mysql.cj.jdbc.Driver");
            connection = DriverManager.getConnection(
                    jdbcProperties.getDatabaseUrl()+"/"+jdbcProperties.getDatabaseName(),
                    jdbcProperties.getUserName(), jdbcProperties.getPassword());

            // mydb is database
            // mydbuser is name of database
            // mydbuser is password of database

            statement = connection.prepareStatement("select * from event left join nomination using (event_id) left join candidate using (candidate_id) where event_id=?");
            statement.setString(1, eventId);
            resultSet = statement.executeQuery();
            boolean isFirstRow=true;
            while (resultSet.next()) {
                System.out.println(resultSet.getString("candidate_id"));
                System.out.println(resultSet.getString("candidate_name"));
                System.out.println(resultSet.getString("candidate_info"));

                Candidate candidate = new Candidate(resultSet.getString("candidate_id"), resultSet.getString("candidate_name"), resultSet.getString("candidate_info"));
                candidateList.add(candidate);
                if(isFirstRow){
                    event = new Event(resultSet.getString("event_id"), resultSet.getString("event_name"), resultSet.getString("event_info"), resultSet.getString("creator_id"));
                    isFirstRow = false;
                }
            }
        }
        catch (Exception exception) {
            System.out.println(Arrays.toString(exception.getStackTrace()));
        }finally{
            try{
                if(resultSet != null){
                    resultSet.close();
                }
                if(statement != null){
                    statement.close();
                }
                if(connection != null){
                    connection.close();
                }
            }catch(SQLException exception){
                System.out.println(exception);
            }
        }
        return new EventWithNomination(event.getEventId(), event.getEventName(), event.getEventInfo(),event.getCreatorId(),
                candidateList);
    }

    public boolean validateId(String creatorId, String eventId){
        int count=0;
        System.out.println("validating creatorId: "+ creatorId);
        System.out.println("validating eventId: "+ eventId);
        Connection connection = null;
        PreparedStatement statement = null;
        ResultSet resultSet = null;
        try {
            // below two lines are used for connectivity.
            //Class.forName("com.mysql.cj.jdbc.Driver");
            connection = DriverManager.getConnection(
                    jdbcProperties.getDatabaseUrl()+"/"+jdbcProperties.getDatabaseName(),
                    jdbcProperties.getUserName(), jdbcProperties.getPassword());

            // mydb is database
            // mydbuser is name of database
            // mydbuser is password of database

            if(creatorId != null && eventId == null){
                String validateCreatorQuery = "select count(*) from creator where creator_id=\'"+creatorId+"\'";
                System.out.println(validateCreatorQuery);
                statement = connection.prepareStatement(validateCreatorQuery);
                resultSet= statement.executeQuery();
                while(resultSet.next()){
                    count = resultSet.getInt(1);
                }
                System.out.println("Count: "+count);
            }else if(creatorId != null && eventId != null){
                String validateCreatorWithEventQuery = "select count(*) from event where creator_id=\'"+creatorId+"\' and event_id=\'"+eventId+"\'";
                System.out.println(validateCreatorWithEventQuery);
                statement = connection.prepareStatement(validateCreatorWithEventQuery);
                resultSet= statement.executeQuery();
                while(resultSet.next()){
                    count = resultSet.getInt(1);
                }
                System.out.println("Count: "+count);
            }
        }
        catch (SQLException exception) {
            System.out.println(exception.getStackTrace());
        }finally{
            try{
                if(resultSet != null){
                    resultSet.close();
                }
                if(statement != null){
                    statement.close();
                }
                if(connection != null){
                    connection.close();
                }
            }catch(SQLException exception){
                System.out.println(exception);
            }
        }
        if(count>0){
            return true;
        }else{
            return false;
        }
    }
}
