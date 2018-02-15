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
import main.java.model.Image;
import main.java.model.KeyProperties;
import main.java.services.AlbumAPI;
import main.java.services.ImageAPI;
import main.java.services.AccountAPI;

@SpringBootApplication
@EnableConfigurationProperties(KeyProperties.class)
public class Application {
	
	private final AlbumAPI albumAPI;
	private final ImageAPI imageAPI;
	private final AccountAPI accountAPI;
	
	private static final Logger log = LoggerFactory.getLogger(Application.class);
	
	@Autowired
	public Application(
			AlbumAPI albumAPI,
			ImageAPI imageAPI,
			AccountAPI accountAPI
	) {
		this.albumAPI = albumAPI;
		this.imageAPI = imageAPI;
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
			Image i = imageAPI.uploadImage("Screenshot_20180105-123128.png");
			
			
			
			imageAPI.deleteImage(i.id);
			albumAPI.deleteAlbum(a.id);
			
		};
	}

}
