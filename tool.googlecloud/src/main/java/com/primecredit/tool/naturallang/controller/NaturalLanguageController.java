package com.primecredit.tool.naturallang.controller;


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

import com.primecredit.tool.common.domain.NaturaLangEntry;
import com.primecredit.tool.common.wsobject.request.NaturalLangRequest;
import com.primecredit.tool.common.wsobject.response.NaturalLangResponse;
import com.primecredit.tool.naturallang.services.GoogleNaturalLanguageService;

@RestController
@RequestMapping("/NaturalLanguage")
public class NaturalLanguageController {

	private static Logger logger = LoggerFactory.getLogger(NaturalLanguageController.class);
	
	@Autowired
	private GoogleNaturalLanguageService googleNaturalLanguageService;
	
	@Value("${temp.path}")
	private String tempPath;

	@RequestMapping(value = "/analyzeEntities", method = RequestMethod.POST)
	public NaturalLangResponse analyzeEntities(@RequestBody NaturalLangRequest request) {
		
		logger.info("From {} {} {}",request.getClientMachineId(), String.valueOf(request.getMillisecond()) , request.getInput());
		
		NaturalLangResponse response = new NaturalLangResponse();
		response.setClientMachineId(request.getClientMachineId());
		response.setMillisecond(new Date().getTime());
		
		List<NaturaLangEntry> entites = googleNaturalLanguageService.analyzeEntities(request.getInput());
		response.setEntites(entites);
		response.setSuccess(true);
		
		
		return response;
	}
		

	
}
