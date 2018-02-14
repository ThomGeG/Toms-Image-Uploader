package main.java;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.beans.factory.annotation.Autowired;

import main.java.services.AlbumService;
import main.java.services.KeyProperties;

@SpringBootApplication
@EnableConfigurationProperties(KeyProperties.class)
public class Application {
	
	private final AlbumService albumAPI;
	private static final Logger log = LoggerFactory.getLogger(Application.class);
	
	@Autowired
	public Application(AlbumService albumAPI) {
		this.albumAPI = albumAPI;
	}
	
	public static void main(String[] args) {	
		SpringApplication.run(Application.class);
	}
	
	@Bean
	public CommandLineRunner run() throws Exception {
		return args -> {
			log.info(albumAPI.getAlbum("up1Gl").toString());
		};
	}

}
