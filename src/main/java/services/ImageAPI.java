package main.java.services;

import java.util.Map;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
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
	
	public Image getImage(String id) {
		return restAPI.request("https://api.imgur.com/3/image/" + id, HttpMethod.GET, new ParameterizedTypeReference<ResponseWrapper<Image>>() {});
	}
	
	public ResponseWrapper<?> deleteImage(String id) {
		return restAPI.request("https://api.imgur.com/3/image/" + id, HttpMethod.DELETE, ResponseWrapper.class);
	}
	
	public Image uploadImage(String file) {
		return uploadImage(file, "", "", "");
	}
	
	public Image uploadImage(String file, String albumID, String title, String description) {
		
		// Read data from the file!
		File f = new File(file);
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
