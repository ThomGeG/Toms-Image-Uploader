package main.java;

import java.io.File;
import java.nio.file.Paths;
import java.util.Map;
import java.util.List;
import java.util.ArrayList;
import java.util.Collection;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

import main.java.imgur.api.ImgurAPI;
import main.java.imgur.model.Image;
import main.java.services.FileEventListener;
import main.java.services.ImageHandler;
import main.java.storage.KeyProperties;
import main.java.storage.StorageService;
import main.java.storage.StorageProperties;

@SpringBootApplication
@EnableConfigurationProperties({
	KeyProperties.class, 
	StorageProperties.class
})
public class Application {
	
	private final ImgurAPI imgur;				//Facade to the imgur API.
	private final ImageHandler imageHandler;	
	private final StorageService fileSystem;
	
	private static final Logger log = LoggerFactory.getLogger(Application.class);
	
	@Autowired
	public Application(
			ImgurAPI imgur,
			ImageHandler imageHandler,
			StorageService fileSystem
	) {
		this.imgur = imgur;
		this.imageHandler = imageHandler;
		this.fileSystem = fileSystem;
	}
	
	public static void main(String[] args) {	
		SpringApplication.run(Application.class);
	}
	
	@Bean
	public CommandLineRunner run() throws Exception {
		return args -> {
			
			Map<String, String> localAlbums = fileSystem.getLocalAlbums();					//AlbumID -> Directory
			FileEventListener<String> fel = new FileEventListener<String>(imageHandler);
			
			for(String albumID : localAlbums.keySet()) {
				
				//Acquire our images for comparison.
				List<Image> remoteImages = imgur.albums.getAlbumImages(albumID);						//Images hosted on imgur.com
				Collection<File> localImages = fileSystem.getLocalImages(localAlbums.get(albumID)); //Images on the local file system.
				
				//Placeholder for images w/o a counterpart on imgur.com
				List<File> buffer = new ArrayList<File>();
				
				//Start finding all the missing ones!
				for(File f : localImages) {
					boolean found = false;
					for(Image i : remoteImages)
						if(f.getName().compareTo(i.name) == 0) {
							found = true;
							break;
						}
					if(!found)
						buffer.add(f);
				}
				
				//Upload missing images.
				List<Image> uploadedImages = new ArrayList<Image>();
				for(File f : buffer)
					uploadedImages.add(imgur.images.uploadImage(f, albumID));
				
				//Register this album/directory on the file listener to upload new ones!
				fel.register(Paths.get(localAlbums.get(albumID)), albumID);
				
			}
			
			//Start the listener to upload new files!
			new Thread(fel).start();

		};
	}

}
