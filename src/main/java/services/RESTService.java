package main.java.services;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import main.java.model.KeyProperties;
import main.java.model.ResponseWrapper;
import main.java.model.Tokens;

@Service
public class RESTService {
	
	private final KeyProperties keys;
	private static final Logger log = LoggerFactory.getLogger(RESTService.class);
	
	@Autowired
	public RESTService(KeyProperties keys) {
		
		this.keys = keys;
		
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));
		headers.set("Authorization", "Client-ID " + this.keys.getClientID());
		
		Map<String, String> payload = new HashMap<String, String>();
		payload.put("refresh_token",	this.keys.getRefreshToken());
		payload.put("client_id",		this.keys.getClientID());
		payload.put("client_secret",	this.keys.getClientSecret());
		payload.put("grant_type",		"refresh_token");
		
		RestTemplate rt = new RestTemplate();
		HttpEntity<Map<String, String>> he = new HttpEntity<Map<String, String>>(payload, headers);
		
		ResponseEntity<Tokens> response = rt.exchange(
				"https://api.imgur.com/oauth2/token", 
				HttpMethod.POST, 
				he, 
				Tokens.class
		);
		
		this.keys.setAccessToken(response.getBody().access_token);

	}
	
	private HttpHeaders getHeaders() {
		
		//Setup our headers.
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));
		
		headers.set("Authorization", "Bearer " + keys.getAccessToken());
		
		return headers;
		
	}
	
	public <T> T request(String endpoint, HttpMethod method, ParameterizedTypeReference<ResponseWrapper<T>> type) {
		
		RestTemplate rt = new RestTemplate();
		HttpEntity<String> he = new HttpEntity<String>("parameters", getHeaders());
		ResponseEntity<ResponseWrapper<T>> response = rt.exchange(endpoint, method, he, type);
		
		log.info(method + ": " + endpoint + ", " + response.getBody());
		log.info(response.getBody().data.toString());
		
		return response.getBody().data;
		
	}
	
}
