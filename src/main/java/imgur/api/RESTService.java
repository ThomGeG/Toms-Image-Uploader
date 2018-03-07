package main.java.imgur.api;

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

import main.java.imgur.model.Tokens;
import main.java.imgur.model.ResponseWrapper;
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
class RESTService {
	
	private final KeyProperties keys;
	private static final Logger log = LoggerFactory.getLogger(RESTService.class);
	
	@Autowired
	public RESTService(KeyProperties keys) {
		
		this.keys = keys;
		
		//KeyProperties doesn't store the access token between executions, so we need a new one!
		
		//Setup our payload containing the necessary data
		Map<String, String> payload = new HashMap<String, String>();
		payload.put("refresh_token",	this.keys.getRefreshToken());
		payload.put("client_id",		this.keys.getClientID());
		payload.put("client_secret",	this.keys.getClientSecret());
		payload.put("grant_type",		"refresh_token");

		//Request the new token(s).
		Tokens t = request("https://api.imgur.com/oauth2/token", HttpMethod.POST, payload, Tokens.class);
		
		//:D
		this.keys.setAccessToken(t.access_token);

	}

	/** Helper method to reduce some boilerplate configuration of the request headers. */
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

	/**
	 * Makes a request to the specified API end-point using the specified HTTP method (GET, POST, DELETE, etc.).
	 * This function various from other overloaded methods in that it is for end-points that do <b>not</b> feature a response wrapper, such as when renewing tokens.<br>
	 * 
	 * <br>This function requires a class, <i>C</i>, to be passed to it such as below:<br>
	 * {@code request("https://api.imgur.com/oauth2/token", HttpMethod.POST, Tokens.class);}
	 * 
	 * @param <C> Class/type of the expected response type/model, such as {@link main.java.imgur.model.Album}, {@link main.java.imgur.model.Image} or even just {@code Boolean}.
	 */
	public <C> C request(String endpoint, HttpMethod method, Class<C> c) {
		return request(endpoint, method, new HashMap<String, String>(), c);
	}
	
	/**
	 * Makes a request to the specified API end-point using the specified HTTP method (GET, POST, DELETE, etc.).
	 * This function various from other overloaded methods in that it is for end-points that do <b>not</b> feature a response wrapper, such as when renewing tokens.<br>
	 *
	 * <br>This function requires a class, <i>C</i>, to be passed to it such as below:<br>
	 * {@code request("https://api.imgur.com/oauth2/token", HttpMethod.POST, payload, Tokens.class);}
	 *
	 * @param <C> Class/type of the expected response type/model, such as {@link main.java.imgur.model.Album}, {@link main.java.imgur.model.Image} or even just {@code Boolean}.
	 */
	public <C> C request(String endpoint, HttpMethod method, Map<String, String> data, Class<C> c) {
		
		RestTemplate rt = new RestTemplate();
		HttpEntity<Map<String, String>> he = new HttpEntity<Map<String, String>>(data, getHeaders());
		ResponseEntity<C> response = rt.exchange(endpoint, method, he, c);
		
		log.info(method + ": " + endpoint + ", " + response.getBody());
		
		return response.getBody();
		
	}
	
	/**
	 * Makes a request to the specified API end-point using the specified HTTP method (GET, POST, DELETE, etc.).
	 * This function various from other overloaded methods in that it is for end-points that <b>do</b> feature a response wrapper.<br>
	 *
	 * <br>This function requires a parameterised type reference to be passed to it, such as seen below: 
	 * <pre>{@code request("https://api.imgur.com/3/image/" + imageID, HttpMethod.GET, new ParameterizedTypeReference<ResponseWrapper<Image>>() {});}</pre>
	 *
	 * @param <T> Class/type of the expected response type/model, such as {@link main.java.imgur.model.Album}, {@link main.java.imgur.model.Image} or even just {@code Boolean}.
	 */
	public <T> T request(String endpoint, HttpMethod method, ParameterizedTypeReference<ResponseWrapper<T>> type) {
		return request(endpoint, method, new HashMap<String, String>(), type);
	}
	
	/**
	 * Makes a request to the specified API end-point using the specified HTTP method (GET, POST, DELETE, etc.).
	 * This function various from other overloaded methods in that it is for end-points that <b>do</b> feature a response wrapper.<br>
	 *
	 * <br>This function requires a parameterised type reference to be passed to it, such as seen below: 
	 * <pre>{@code request("https://api.imgur.com/3/image/" + imageID, HttpMethod.GET, new ParameterizedTypeReference<ResponseWrapper<Image>>() {});}</pre>
	 *
	 * @param <T> Class/type of the expected response type/model, such as {@link main.java.imgur.model.Album}, {@link main.java.imgur.model.Image} or even just {@code Boolean}.
	 */
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
