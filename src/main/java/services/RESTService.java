package main.java.services;

import java.util.Arrays;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class RESTService {
	
	private final KeyProperties keys;
	
	@Autowired
	public RESTService(KeyProperties keys) {
		this.keys = keys;
	}
	
	private HttpHeaders getHeaders() {
		
		//Setup our headers.
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));
		
		headers.set("Authorization", "Client-ID " + keys.getClientID());
		
		return headers;
		
	}
	
	public <C> ResponseEntity<C> foo(String endpoint, HttpMethod method, Class<C> c) {
		ResponseEntity<C> response = new RestTemplate().exchange(endpoint, method, new HttpEntity<String>("parameters", getHeaders()), c);
		return response;
	}
	
}
