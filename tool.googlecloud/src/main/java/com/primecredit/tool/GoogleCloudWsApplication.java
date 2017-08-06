package com.primecredit.tool;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class GoogleCloudWsApplication {

	private static Logger logger = LoggerFactory.getLogger(GoogleCloudWsApplication.class);
	
	public static void main(String[] args) {
		logger.debug("SpeechWsApplication - Start");
		SpringApplication.run(GoogleCloudWsApplication.class, args);
	}
}
