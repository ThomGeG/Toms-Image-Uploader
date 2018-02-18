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
	
	/**
	 * Retrieves information about an album.
	 * @see <a href="https://apidocs.imgur.com/#5369b915-ad8b-47b1-b44b-8e2561e41cee">apidocs.imgur.com</a>
	 */
	public Album getAlbum(String albumID) {
		return restAPI.request("https://api.imgur.com/3/album/" + albumID, HttpMethod.GET, new ParameterizedTypeReference<ResponseWrapper<Album>>() {});
	}
	
	/**
	 * Deletes an album.
	 * @see <a href="https://apidocs.imgur.com/#682edfd5-c273-4cf1-8a28-cbd88e5568c4">apidocs.imgur.com</a>
	 * @return The boolean success flag from Imgur's response.
	 */
	public Boolean deleteAlbum(String albumID) {
		return restAPI.request("https://api.imgur.com/3/album/" + albumID, HttpMethod.DELETE, new ParameterizedTypeReference<ResponseWrapper<Boolean>>() {});
	}
	
	/**
	 * Creates a new album.
	 * @see <a href="https://apidocs.imgur.com/#8f89bd41-28a1-4624-9393-95e12cec509a">apidocs.imgur.com</a>
	 * @return The newly created album.
	 */
	public Album createAlbum() {
		return createAlbum("", "");
	}
	
	
	/**
	 * Creates a new album with provided parameters.
	 * @see <a href="https://apidocs.imgur.com/#8f89bd41-28a1-4624-9393-95e12cec509a">apidocs.imgur.com</a>
	 * @param title Title to appear over the album.
	 * @param description Description to appear under the album.
	 * @return The newly created album.
	 */
	public Album createAlbum(String title, String description) {
		
		Map<String, String> data = new HashMap<String, String>();
		data.put("title",		title);
		data.put("description", description);
		
		return restAPI.request("https://api.imgur.com/3/album/", HttpMethod.POST, new ParameterizedTypeReference<ResponseWrapper<Album>>() {});
	}
	
	/**
	 * Add a series of images to an album.
	 * @see <a href="https://apidocs.imgur.com/#b98029b6-5cc1-4a6f-b4bf-fe1db50869a2">apidocs.imgur.com</a>
	 * @param albumID The album to add images too.
	 * @param ids List of the images id's to add to the album.
	 * @return The boolean success flag from Imgur's response.
	 */
	public Boolean addImages(String albumID, List<String> ids) {
		return restAPI.request(String.format("https://api.imgur.com/3/album/%s/add?ids[]=" + String.join("&ids[]=", ids), albumID), HttpMethod.PUT, new ParameterizedTypeReference<ResponseWrapper<Boolean>>() {});
	}

	/**
	 * Retrieve the images contained within the specified album.
	 * @see <a href="https://apidocs.imgur.com/#7dde894b-a967-4419-9be2-082fbf379109">apidocs.imgur.com</a>
	 * @param albumID The album to retrieve from.
	 */
	public List<Image> getAlbumImages(String albumID) {
		return restAPI.request(String.format("https://api.imgur.com/3/album/%s/images", albumID), HttpMethod.GET, new ParameterizedTypeReference<ResponseWrapper<List<Image>>>() {});
	}
	
}
