package com.primecredit.tool.speechrecognition;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class SpeechRecognitionWsApplication {

	private static Logger logger = LoggerFactory.getLogger(SpeechRecognitionWsApplication.class);
	
	public static void main(String[] args) {
		logger.debug("SpeechWsApplication - Start");
		SpringApplication.run(SpeechRecognitionWsApplication.class, args);
	}
}
