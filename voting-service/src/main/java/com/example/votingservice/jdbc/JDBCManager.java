package com.example.votingservice.jdbc;

import com.example.votingservice.model.*;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

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


    public EventWithNomination getEvent(String eventId){
        int count = 0;
        Event event = new Event();
        List<Candidate> candidateList = new ArrayList<>();

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
            statement = connection.prepareStatement("select * from event left join nomination using (event_id) left join candidate using (candidate_id) where event_id=?");
            statement.setString(1, eventId);
            ResultSet resultSet;
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
            resultSet.close();
            statement.close();
            connection.close();
        }
        catch (Exception exception) {
            System.out.println(exception);
        }
        return new EventWithNomination(event.getEventId(), event.getEventName(), event.getEventInfo(),event.getCreatorId(),
                candidateList);
    }

    public VotingResult getResult(String eventId){
        VotingResult votingResult = new VotingResult();
        List<CandidateResult> candidateResults = new ArrayList<>();
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
            statement = connection.prepareStatement("select * from result left join candidate using (candidate_id) where event_id=?");
            statement.setString(1, eventId);
            ResultSet resultSet;
            resultSet = statement.executeQuery();
            while (resultSet.next()) {
                System.out.println(resultSet.getString("candidate_id"));
                System.out.println(resultSet.getString("candidate_name"));
                System.out.println(resultSet.getString("candidate_info"));

                Candidate candidate = new Candidate(resultSet.getString("candidate_id"), resultSet.getString("candidate_name"), resultSet.getString("candidate_info"));
                candidateResults.add(new CandidateResult(resultSet.getInt("count"), candidate));
            }
            resultSet.close();
            statement.close();
            connection.close();
            votingResult.setEventId(eventId);
            votingResult.setFinalResult(candidateResults);
        }
        catch (Exception exception) {
            System.out.println(exception);
        }
        return votingResult;
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
