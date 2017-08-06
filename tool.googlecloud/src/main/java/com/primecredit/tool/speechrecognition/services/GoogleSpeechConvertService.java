package com.primecredit.tool.speechrecognition.services;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.google.api.gax.grpc.OperationFuture;
import com.google.cloud.speech.v1.LongRunningRecognizeResponse;
import com.google.cloud.speech.v1.RecognitionAudio;
import com.google.cloud.speech.v1.RecognitionConfig;
import com.google.cloud.speech.v1.RecognitionConfig.AudioEncoding;
import com.google.cloud.speech.v1.SpeechClient;
import com.google.cloud.speech.v1.SpeechRecognitionAlternative;
import com.google.cloud.speech.v1.SpeechRecognitionResult;
import com.google.protobuf.ByteString;
import com.primecredit.tool.common.util.WavFileHandler;

@Service
public class GoogleSpeechConvertService {
	
	private static Logger logger = LoggerFactory.getLogger(GoogleSpeechConvertService.class);
	private static int MAX_ALTERNATIVE = 20;
	
	@Value("${temp.path}")
	private String tempPath;
	
	public List<String> convert(String sourceFile) {

		WavFileHandler wavFileHandler = WavFileHandler.getInstance();
		
		Map<Integer, String> resultMap = initResultMap();

		try {
			List<String> tmpFiles = wavFileHandler.splitWavFile(sourceFile,tempPath);
			// List<String> tmpFiles = null;
			logger.debug("Google Speech API {} - Start ....",sourceFile);
			Iterator<String> fileIter = tmpFiles.iterator();
			while (fileIter.hasNext()) {
				String tmpFileName = fileIter.next();
				Map<Integer, String> tmpMap = asyncRecognizeFile(tmpFileName);
				appendMapResult(resultMap, tmpMap);
				File tmpFile = new File(tmpFileName);
				tmpFile.delete();
				
			}

			logger.debug("\nGoogle Speech API End ....");

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		List<String> results = new ArrayList<String>();
		results.addAll(resultMap.values());
		return results;
	}

	private void appendMapResult(Map<Integer, String> targetMap, Map<Integer, String> updateMap) {
		Iterator<Integer> keyIter = updateMap.keySet().iterator();
		while (keyIter.hasNext()) {
			int key = keyIter.next();
			if (targetMap.containsKey(key)) {
				String value = targetMap.get(key);
				String append = updateMap.get(key);
				StringBuilder sb = new StringBuilder();
				sb.append(value);
				sb.append(append);
				targetMap.put(key, sb.toString());
			} else {
				String append = updateMap.get(key);
				targetMap.put(key, append);
			}
		}
	}

	private Map<Integer, String> initResultMap() {
		Map<Integer, String> resultMap = new LinkedHashMap<Integer, String>();
		for(int i=1; i<=MAX_ALTERNATIVE; i++){
			resultMap.put(i, "");
		}

		return resultMap;
	}

	private Map<Integer, String> asyncRecognizeFile(String fileName) throws Exception, IOException {
		Map<Integer, String> resultMap = new LinkedHashMap<Integer, String>();
		
		// Instantiates a client with GOOGLE_APPLICATION_CREDENTIALS
		SpeechClient speech = SpeechClient.create();

		Path path = Paths.get(fileName);
		byte[] data = Files.readAllBytes(path);
		ByteString audioBytes = ByteString.copyFrom(data);
		
		File srcFile = new File(fileName);
		AudioInputStream ais = null;
		AudioFormat format = null;

		ais = AudioSystem.getAudioInputStream(srcFile);
		format = ais.getFormat();
				

		// Configure request with local raw PCM audio
		RecognitionConfig config = RecognitionConfig.newBuilder()
				.setEncoding(AudioEncoding.LINEAR16)
				.setLanguageCode("yue-Hant-HK")
				.setSampleRateHertz((int) format.getSampleRate())
				.setProfanityFilter(false)
				.setMaxAlternatives(MAX_ALTERNATIVE)
				.build();

		RecognitionAudio audio = RecognitionAudio.newBuilder().setContent(audioBytes).build();

		// Use non-blocking call for getting file transcription

		OperationFuture<LongRunningRecognizeResponse> response = speech.longRunningRecognizeAsync(config, audio);
		while (!response.isDone()) {
			// System.out.println("Waiting for response...");
			Thread.sleep(5000);
		}
		List<SpeechRecognitionResult> results = response.get().getResultsList();
		// System.out.println(response.get);

		// RecognizeResponse response = speech.recognize(config, audio);
		// List<SpeechRecognitionResult> results = response.getResultsList();

		for (SpeechRecognitionResult result : results) {
			List<SpeechRecognitionAlternative> alternatives = result.getAlternativesList();

			int i = 1;
			for (SpeechRecognitionAlternative alternative : alternatives) {
				// System.out.println(alternative.getConfidence());
				// System.out.println("[" + i + "]\t" + alternative.getTranscript());
				if(resultMap.containsKey(i)){
					StringBuilder sb  = new StringBuilder();
					sb.append(resultMap.get(i));
					sb.append(alternative.getTranscript());
				}else{
					resultMap.put(i, alternative.getTranscript());
				}
				i++;
			
			}
		}
		ais.close();
		speech.close();
	
		return resultMap;
	}
	
}