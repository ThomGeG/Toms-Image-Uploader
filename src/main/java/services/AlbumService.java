package main.java.services;

import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import main.java.model.Album;
import main.java.model.JSONResponse;

@Service
public class AlbumService {
	
	private final RESTService restAPI;
	
	@Autowired
	public AlbumService(RESTService api) {
		this.restAPI = api;
	}
	
	public Album getAlbum(String albumID) {

		ResponseEntity<JSONResponse> response = restAPI.foo("https://api.imgur.com/3/album/" + albumID, HttpMethod.GET, JSONResponse.class);
		
		JSONResponse a = response.getBody();
		return (Album) a.data;
	
	}
	
}
