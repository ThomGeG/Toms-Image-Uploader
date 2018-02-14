package main.java.services;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;

import main.java.model.Album;
import main.java.model.ResponseWrapper;

@Service
public class AlbumService {
	
	private final RESTService restAPI;
	private static final Logger log = LoggerFactory.getLogger(AlbumService.class);
	
	@Autowired
	public AlbumService(RESTService api) {
		this.restAPI = api;
	}
	
	public Album getAlbum(String albumID) {

		ResponseWrapper<Album> response = restAPI.foo("https://api.imgur.com/3/album/" + albumID, HttpMethod.GET, new ParameterizedTypeReference<ResponseWrapper<Album>>() {});
		log.info(response.toString());
		
		return response.data;
	
	}
	
	public Album createAlbum() {
		return null;
	}
	
}
