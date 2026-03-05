package com.example.votingcreator;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(basePackages = "com.example.votingcreator")
@EnableEurekaClient
public class VotingCreatorApplication {

  public static void main(String[] args) {
    SpringApplication.run(VotingCreatorApplication.class, args);
  }
}
