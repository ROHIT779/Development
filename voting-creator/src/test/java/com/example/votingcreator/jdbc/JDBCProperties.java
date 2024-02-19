package com.example.votingcreator.jdbc;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
public class JDBCProperties {
    @Value("${database.url}")
    private String databaseUrl;

    @Value("${database.name}")
    private String databaseName;

    @Value("${database.username}")
    private String userName;

    @Value("${database.password}")
    private String password;

    public String getDatabaseUrl() {
        return databaseUrl;
    }

    public String getDatabaseName() {
        return databaseName;
    }

    public String getUserName() {
        return userName;
    }

    public String getPassword() {
        return password;
    }
}
