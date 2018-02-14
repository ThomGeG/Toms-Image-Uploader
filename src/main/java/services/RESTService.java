package main.java.services;

import java.util.Arrays;

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

import main.java.model.ResponseWrapper;
import main.java.model.KeyProperties;

@Service
public class RESTService {
	
	private final KeyProperties keys;
	private static final Logger log = LoggerFactory.getLogger(AlbumService.class);
	
	@Autowired
	public RESTService(KeyProperties keys) {
		this.keys = keys;
	}
	
	private HttpHeaders getHeaders() {
		
		//Setup our headers.
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));
		
		headers.set("Authorization", "Bearer " + keys.getAccessToken());
		
		return headers;
		
	}
	
	public <T> ResponseWrapper<T> foo(String endpoint, HttpMethod method, ParameterizedTypeReference<ResponseWrapper<T>> type) {
		
		RestTemplate rt = new RestTemplate();
		HttpEntity<String> he = new HttpEntity<String>("parameters", getHeaders());
		
		ResponseEntity<ResponseWrapper<T>> response = rt.exchange(endpoint, method, he, type);
		
		log.info(response.toString());
		
		return response.getBody();
		
	}
	
}
