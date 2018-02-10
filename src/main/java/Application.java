package main.java;

import java.util.Arrays;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;

import main.java.model.Album;
import main.java.model.Image;
import main.java.model.JSONResponse;

public class Application {
	
	private static final String CLIENT_ID = "aab9761289e2b38";
	private static final Logger log = LoggerFactory.getLogger(Application.class);
	
	public static void main(String[] args) {
		
		//Setup our headers.
		HttpHeaders headers = new HttpHeaders();
		headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));
		headers.setContentType(MediaType.APPLICATION_JSON);
		headers.set("Authorization", "Client-ID " + CLIENT_ID);
		
		RestTemplate restTemplate = new RestTemplate();
		ResponseEntity<JSONResponse> respEntity = restTemplate.exchange("https://api.imgur.com/3/album/up1Gl", HttpMethod.GET, new HttpEntity<String>("parameters", headers), JSONResponse.class);
		
		JSONResponse a = respEntity.getBody();
		log.info(a.toString());
		
	}

}
