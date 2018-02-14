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

import main.java.model.Album;
import main.java.model.JSONResponse;

@Service
public class AlbumService {
	
	private final KeyProperties keys;
	
	@Autowired
	public AlbumService(KeyProperties keys) {
		this.keys = keys;
	}
	
	public Album getAlbum(String albumID) {

		//Setup our headers.
		HttpHeaders headers = new HttpHeaders();
		headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));
		headers.setContentType(MediaType.APPLICATION_JSON);
		headers.set("Authorization", "Client-ID " + keys.getClientID());
		
		RestTemplate restTemplate = new RestTemplate();
		ResponseEntity<JSONResponse> respEntity = restTemplate.exchange(
				String.format("https://api.imgur.com/3/album/%s", albumID), //API end-point
				HttpMethod.GET, 											//Request method
				new HttpEntity<String>("parameters", headers), 				//Headers
				JSONResponse.class											//Expected response format
		);
		
		JSONResponse a = respEntity.getBody();
		return (Album) a.data;
	
	}
	
}
