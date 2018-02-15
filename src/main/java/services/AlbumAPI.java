package main.java.services;

import java.util.Map;
import java.util.List;
import java.util.HashMap;

import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.beans.factory.annotation.Autowired;

import main.java.model.Album;
import main.java.model.Image;
import main.java.model.ResponseWrapper;

/** 
 * Service class to handle API requests of an account related variety.
 * 
 * @see <a href="https://api.imgur.com/endpoints/album">api.imgur.com/endpoints/album</a>
 * @see <a href="https://apidocs.imgur.com/#3606f862-8281-48f1-b0f7-49a5f77da0e1">apidocs.imgur.com</a>
 * 
 * @author Tom
 *
 */
@Service
public class AlbumAPI {
	
	private final RESTService restAPI;
	
	@Autowired
	public AlbumAPI(RESTService api) {
		this.restAPI = api;
	}
	
	public Album getAlbum(String albumID) {
		return restAPI.request("https://api.imgur.com/3/album/" + albumID, HttpMethod.GET, new ParameterizedTypeReference<ResponseWrapper<Album>>() {});
	}
	
	public Boolean deleteAlbum(String albumID) {
		return restAPI.request("https://api.imgur.com/3/album/" + albumID, HttpMethod.DELETE, new ParameterizedTypeReference<ResponseWrapper<Boolean>>() {});
	}
	
	public Album createAlbum() {
		return createAlbum("", "");
	}
	
	public Album createAlbum(String title, String description) {
		
		Map<String, String> data = new HashMap<String, String>();
		data.put("title",		title);
		data.put("description", description);
		
		return restAPI.request("https://api.imgur.com/3/album/", HttpMethod.POST, new ParameterizedTypeReference<ResponseWrapper<Album>>() {});
	}
	
	public Boolean addImages(String id, List<String> ids) {
		return restAPI.request(String.format("https://api.imgur.com/3/album/%s/add?ids[]=" + String.join("&ids[]=", ids), id), HttpMethod.PUT, new ParameterizedTypeReference<ResponseWrapper<Boolean>>() {});
	}

	public List<Image> getAlbumImages(String id) {
		return restAPI.request(String.format("https://api.imgur.com/3/album/%s/images", id), HttpMethod.GET, new ParameterizedTypeReference<ResponseWrapper<List<Image>>>() {});
	}
	
}
