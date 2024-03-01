package com.example.votingcreator;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(basePackages = "com.example.votingcreator")
public class VotingCreatorApplication {

  public static void main(String[] args) {
    SpringApplication.run(VotingCreatorApplication.class, args);
  }
}
