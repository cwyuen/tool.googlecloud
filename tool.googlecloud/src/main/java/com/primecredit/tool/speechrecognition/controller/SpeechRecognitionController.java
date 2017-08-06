package com.primecredit.tool.speechrecognition.controller;

import java.io.File;
import java.util.Date;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.primecredit.tool.common.util.FileUtil;
import com.primecredit.tool.common.util.WavFileHandler;
import com.primecredit.tool.common.wsobject.request.RecongnitionRequest;
import com.primecredit.tool.common.wsobject.response.RecognitionResponse;
import com.primecredit.tool.speechrecognition.services.GoogleSpeechConvertService;


@RestController
@RequestMapping("/SpeechRecognition")
public class SpeechRecognitionController {

	private static Logger logger = LoggerFactory.getLogger(SpeechRecognitionController.class);
	
	@Autowired
	private GoogleSpeechConvertService googleSpeechConvertService;
	
	@Value("${temp.path}")
	private String tempPath;

	@RequestMapping(value = "/recognition", method = RequestMethod.POST)
	public RecognitionResponse recognition(@RequestBody RecongnitionRequest request) {
		RecognitionResponse response = new RecognitionResponse();
		response.setClientMachineId(request.getClientMachineId());
		response.setMillisecond(new Date().getTime());
		
		
		StringBuilder sbTempFileName = new StringBuilder();
		sbTempFileName.append(request.getClientMachineId() .replaceAll("[^\\p{Alpha}\\p{Digit}]+",""));
		sbTempFileName.append("_");
		sbTempFileName.append(request.getMillisecond());
		sbTempFileName.append(".wav");
		
		try {
			File sourceFile = FileUtil.generateFile(tempPath, sbTempFileName.toString(), request.getFileData());

			List<String> speechTextList = googleSpeechConvertService.convert(sourceFile.getAbsolutePath());
			response.setSpeechTextList(speechTextList);
			
			sourceFile.delete();
		} catch (Exception e) {
			logger.error(e.getMessage());
		}
		
		return response;
		
	}
		

	
}
