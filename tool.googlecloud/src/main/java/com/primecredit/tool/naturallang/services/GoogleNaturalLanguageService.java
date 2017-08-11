package com.primecredit.tool.naturallang.services;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.google.cloud.language.v1.AnalyzeEntitiesResponse;
import com.google.cloud.language.v1.Document;
import com.google.cloud.language.v1.Document.Type;
import com.google.cloud.language.v1.EncodingType;
import com.google.cloud.language.v1.Entity;
import com.google.cloud.language.v1.LanguageServiceClient;
import com.primecredit.tool.common.domain.NaturaLangEntry;

@Service
public class GoogleNaturalLanguageService {

	private static Logger logger = LoggerFactory.getLogger(GoogleNaturalLanguageService.class);

	public List<NaturaLangEntry> analyzeEntities(String input) {

		List<NaturaLangEntry> results = new ArrayList<>();

		// Instantiates a client
		LanguageServiceClient language;
		try {
			language = LanguageServiceClient.create();
			// The text to analyze
			Document doc = Document.newBuilder().setContent(input).setType(Type.PLAIN_TEXT).build();

			// analyzeEntitie
			AnalyzeEntitiesResponse response = language.analyzeEntities(doc, EncodingType.UTF8);
			List<Entity> entities = response.getEntitiesList();
			for (Entity entity : entities) {
				NaturaLangEntry dist = new NaturaLangEntry();
				dist.setName(entity.getName());
				dist.setSalience(entity.getSalience());
				dist.setType(entity.getType().toString());
				results.add(dist);
			}

			language.close();
		} catch (Exception e) {
			logger.error("Exception - GoogleNaturalLanguageService.analyze" + e.getMessage());
		}

		return results;
	}

}