package main.java.services.io;

import java.nio.file.Path;

import org.springframework.stereotype.Component;
import org.springframework.beans.factory.annotation.Autowired;

import main.java.imgur.api.ImageAPI;

/**
 * An implementation of the FileEventHandler class.
 * This strategy simply uploads created images to their corresponding album.
 * 
 * @see main.java.services.io.FileEventHandler
 * 
 * @author Tom
 */
@Component
public class ImageUploadHandler implements FileEventHandler<String> {
	
	private ImageAPI imageAPI;
	
	@Autowired
	public ImageUploadHandler(ImageAPI imageAPI) {
		this.imageAPI = imageAPI;
	}

	@Override
	public void created(Path p, String albumID) {
		imageAPI.uploadImage(p.toFile(), albumID);
	}

	@Override
	public void deleted(Path p, String albumID) {
		//Do nothing.
	}

	@Override
	public void modified(Path p, String albumID) {
		//Do nothing.
	}

}
