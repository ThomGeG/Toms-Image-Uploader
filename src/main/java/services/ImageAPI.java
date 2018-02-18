package main.java.services;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Map;
import java.util.Base64;
import java.util.HashMap;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;

import main.java.model.Image;
import main.java.model.ResponseWrapper;

/** 
 * Service class to handle API requests of related to images.
 * 
 * @see <a href="https://api.imgur.com/endpoints/image">api.imgur.com/endpoints/image</a>
 * @see <a href="https://apidocs.imgur.com/#de179b6a-3eda-4406-a8d7-1fb06c17cb9c1">apidocs.imgur.com</a>
 * 
 * @author Tom
 *
 */
@Service
public class ImageAPI {
	
	private final RESTService restAPI;
	
	@Autowired
	public ImageAPI(RESTService api) {
		this.restAPI = api;
	}
	
	/**
	 * Get information about an image.
	 * @see <a href="https://apidocs.imgur.com/#2078c7e0-c2b8-4bc8-a646-6e544b087d0f">apidocs.imgur.com</a>
	 */
	public Image getImage(String imageID) {
		return restAPI.request("https://api.imgur.com/3/image/" + imageID, HttpMethod.GET, new ParameterizedTypeReference<ResponseWrapper<Image>>() {});
	}
	
	/** 
	 * Deletes an image from imgur.com
	 * @see <a href="https://apidocs.imgur.com/#ca48883b-6964-4ab8-b87f-c274e32a970d">apidocs.imgur.com</a>
	 * @return The boolean success flag from Imgur's response.
	 */
	public Boolean deleteImage(String imageID) {
		return restAPI.request("https://api.imgur.com/3/image/" + imageID, HttpMethod.DELETE, new ParameterizedTypeReference<ResponseWrapper<Boolean>>() {});
	}
	
	/**
	 * Uploads an image to imgur.com
	 * @see <a href="https://apidocs.imgur.com/#c85c9dfc-7487-4de2-9ecd-66f727cf3139">apidocs.imgur.com</a>
	 * @return The newly uploaded image
	 */
	public Image uploadImage(File f) {
		return uploadImage(f, "", "", "");
	}
	
	/**
	 * Uploads an image to imgur.com and automatically includes it in the target album.
	 * @see <a href="https://apidocs.imgur.com/#c85c9dfc-7487-4de2-9ecd-66f727cf3139">apidocs.imgur.com</a>
	 * @return The newly uploaded image
	 */
	public Image uploadImage(File f, String albumID) {
		return uploadImage(f, albumID, "", "");
	}
	
	/**
	 * Uploads an image to imgur.com
	 * @see <a href="https://apidocs.imgur.com/#c85c9dfc-7487-4de2-9ecd-66f727cf3139">apidocs.imgur.com</a>
	 * @return The newly uploaded image
	 */
	public Image uploadImage(File f, String title, String description) {
		return uploadImage(f, "", title, description);
	}
	
	/**
	 * Uploads an image to imgur.com and automatically includes it in the target album.
	 * @see <a href="https://apidocs.imgur.com/#c85c9dfc-7487-4de2-9ecd-66f727cf3139">apidocs.imgur.com</a>
	 * @return The newly uploaded image
	 */
	public Image uploadImage(File f, String albumID, String title, String description) {
		
		// Read data from the file!
		String fileData = null;
        
		try {
			
			byte[] bytes = new byte[(int) f.length()];
			FileInputStream in = new FileInputStream(f);
			
			in.read(bytes);
			Base64.getEncoder().encodeToString(bytes);
			fileData = Base64.getEncoder().encodeToString(bytes);
			
			in.close();
		
		} catch (IOException e) {
			e.printStackTrace();
		}

		//Assemble pay-load data.
		Map<String, String> data = new HashMap<String, String>();
		data.put("album",		albumID);
		data.put("title",		title);
		data.put("description", description);
		data.put("image", 		fileData);
		data.put("name",		f.getName());
		
		//Ship it!
		return restAPI.request("https://api.imgur.com/3/image/", HttpMethod.POST, data, new ParameterizedTypeReference<ResponseWrapper<Image>>() {});
		
	}

}
