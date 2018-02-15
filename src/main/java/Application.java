package main.java;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.beans.factory.annotation.Autowired;

import main.java.model.Album;
import main.java.model.KeyProperties;
import main.java.model.ResponseWrapper;
import main.java.services.AccountAPI;
import main.java.services.AlbumAPI;

@SpringBootApplication
@EnableConfigurationProperties(KeyProperties.class)
public class Application {
	
	private final AlbumAPI albumAPI;
	private final AccountAPI accountAPI;
	
	private static final Logger log = LoggerFactory.getLogger(Application.class);
	
	@Autowired
	public Application(
			AlbumAPI albumAPI,
			AccountAPI accountAPI
	) {
		this.albumAPI = albumAPI;
		this.accountAPI = accountAPI;
	}
	
	public static void main(String[] args) {	
		SpringApplication.run(Application.class);
	}
	
	@Bean
	public CommandLineRunner run() throws Exception {
		return args -> {
			
			accountAPI.whoAmI();
			
			Album a = albumAPI.createAlbum();
			
			//Upload an image to the album.
			
			albumAPI.deleteAlbum(a.id);
			
		};
	}

}
