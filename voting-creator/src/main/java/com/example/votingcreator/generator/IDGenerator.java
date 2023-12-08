package com.example.votingcreator.generator;

import org.apache.commons.lang3.RandomStringUtils;

public class IDGenerator {

    public static String getRandomId(){
        int length = 5;
        boolean useLetters = true;
        boolean useNumbers = true;
        String generatedString = RandomStringUtils.random(length, useLetters, useNumbers);

        System.out.println(generatedString);
        return generatedString;
    }
}
