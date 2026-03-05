package com.example.voting_utility;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.server.EnableEurekaServer;

@SpringBootApplication
@EnableEurekaServer
public class VotingUtilityApplication {

	public static void main(String[] args) {
		SpringApplication.run(VotingUtilityApplication.class, args);
	}

}
