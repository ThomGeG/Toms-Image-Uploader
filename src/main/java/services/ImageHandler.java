package main.java.services;

import java.nio.file.Path;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import main.java.services.imgur.ImageAPI;

/**
 * Simple strategy for uploading created images.
 * @author Tom
 */
@Service
public class ImageHandler implements FileEventHandler<String> {
	
	private final ImageAPI imageAPI;
	
	@Autowired
	public ImageHandler(ImageAPI imageAPI) {
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
