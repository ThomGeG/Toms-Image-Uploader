package main.java.services;

import java.util.Map;
import java.util.Arrays;
import java.util.HashMap;

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

import main.java.model.Tokens;
import main.java.model.ResponseWrapper;
import main.java.storage.KeyProperties;

/** 
 * Service class to act as the closest point of contact to the Imgur API.
 * 
 * @see <a href="https://api.imgur.com/">api.imgur.com</a>
 * @see <a href="https://apidocs.imgur.com/">apidocs.imgur.com</a>
 * 
 * @author Tom
 *
 */
@Service
public class RESTService {
	
	private final KeyProperties keys;
	private static final Logger log = LoggerFactory.getLogger(RESTService.class);
	
	@Autowired
	public RESTService(KeyProperties keys) {
		
		this.keys = keys;
		
		Map<String, String> payload = new HashMap<String, String>();
		payload.put("refresh_token",	this.keys.getRefreshToken());
		payload.put("client_id",		this.keys.getClientID());
		payload.put("client_secret",	this.keys.getClientSecret());
		payload.put("grant_type",		"refresh_token");

		Tokens t = request("https://api.imgur.com/oauth2/token", HttpMethod.POST, payload, Tokens.class);
		
		this.keys.setAccessToken(t.access_token);

	}

	private HttpHeaders getHeaders() {
		
		//Setup our headers.
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));
		
		if(keys.getAccessToken() != null)
			headers.set("Authorization", "Bearer " + keys.getAccessToken());
		else
			headers.set("Authorization", "Client-ID "+ keys.getClientID());
			
		return headers;
		
	}
	
	public <C> C request(String endpoint, HttpMethod method, Class<C> c) {
		return request(endpoint, method, new HashMap<String, String>(), c);
	}
	
	public <C> C request(String endpoint, HttpMethod method, Map<String, String> data, Class<C> c) {
		
		RestTemplate rt = new RestTemplate();
		HttpEntity<Map<String, String>> he = new HttpEntity<Map<String, String>>(data, getHeaders());
		ResponseEntity<C> response = rt.exchange(endpoint, method, he, c);
		
		log.info(method + ": " + endpoint + ", " + response.getBody());
		
		return response.getBody();
		
	}
		
	public <T> T request(String endpoint, HttpMethod method, ParameterizedTypeReference<ResponseWrapper<T>> type) {
		return request(endpoint, method, new HashMap<String, String>(), type);
	}
	
	public <T> T request(String endpoint, HttpMethod method, Map<String, String> data, ParameterizedTypeReference<ResponseWrapper<T>> type) {
		
		log.info(method + ": " + endpoint);

		for(String key : data.keySet())
			log.info("\t" + key + ": '" + (key.compareTo("image") != 0 ? data.get(key) : "Some image data...") + "'");
		
		RestTemplate rt = new RestTemplate();
		HttpEntity<Map<String, String>> he = new HttpEntity<Map<String, String>>(data, getHeaders());
		ResponseEntity<ResponseWrapper<T>> response = rt.exchange(endpoint, method, he, type);
		
		log.info("");
		log.info("\t" + response.getBody().toString());
		log.info("\t" + response.getBody().data.toString());
		log.info("");
		
		return response.getBody().data;
		
	}
	
}
