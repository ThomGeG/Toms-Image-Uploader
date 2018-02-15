package main.java;

import java.util.Arrays;

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
			
			Album a = albumAPI.createAlbum("Test album", "The album I've been using to test the new Java version of the image uploader.");
			
			Image i1 = imageAPI.uploadImage("C:\\Users\\Tom\\Desktop\\Posters\\01-0899_l.jpg");
			Image i2 = imageAPI.uploadImage("C:\\Users\\Tom\\Desktop\\Posters\\ODDWORLD_SOULSTORM_ARTPRINT_NEW_FRAME.jpg");
			Image i3 = imageAPI.uploadImage("C:\\Users\\Tom\\Desktop\\Posters\\TASTE_RUPTURE_TIN_SIGN.jpg");
			
			albumAPI.addImages(a.id, Arrays.asList(i1.id, i2.id, i3.id));
			albumAPI.getAlbumImages(a.id);
			
			imageAPI.deleteImage(i1.id);
			imageAPI.deleteImage(i2.id);
			imageAPI.deleteImage(i3.id);
			
			albumAPI.deleteAlbum(a.id);
			
		};
	}

}
