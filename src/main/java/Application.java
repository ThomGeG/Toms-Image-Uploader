package main.java;

import java.io.File;
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

import main.java.model.Album;
import main.java.model.Image;
import main.java.model.Account;
import main.java.services.AlbumAPI;
import main.java.services.ImageAPI;
import main.java.services.AccountAPI;
import main.java.storage.KeyProperties;
import main.java.storage.StorageService;
import main.java.storage.StorageProperties;

@SpringBootApplication
@EnableConfigurationProperties({
	KeyProperties.class, 
	StorageProperties.class
})
public class Application {
	
	private final AlbumAPI albumAPI;
	private final ImageAPI imageAPI;
	private final AccountAPI accountAPI;

	private final StorageService fileSystem;
	
	private static final Logger log = LoggerFactory.getLogger(Application.class);
	
	@Autowired
	public Application(
			AlbumAPI albumAPI,
			ImageAPI imageAPI,
			AccountAPI accountAPI,
			StorageService fileSystem
	) {
		this.albumAPI = albumAPI;
		this.imageAPI = imageAPI;
		this.accountAPI = accountAPI;
		this.fileSystem = fileSystem;
	}
	
	public static void main(String[] args) {	
		SpringApplication.run(Application.class);
	}
	
	@Bean
	public CommandLineRunner run() throws Exception {
		return args -> {
			
			Map<String, String> localAlbums = fileSystem.getLocalAlbums();
			
			for(String albumID : localAlbums.keySet()) {
				
				//Acquire our images for comparison.
				List<Image> remoteImages = albumAPI.getAlbumImages(albumID);						//Images hosted on imgur.com
				Collection<File> localImages = fileSystem.getLocalImages(localAlbums.get(albumID)); //Images on the local file system.
				
				//Placeholder for images w/o a counterpart on imgur.com
				List<File> buffer = new ArrayList<File>();
				
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
				
				List<Image> uploadedImages = new ArrayList<Image>();
				for(File f : buffer)
					uploadedImages.add(imageAPI.uploadImage(f, albumID));
				
			}
			
		};
	}

}
