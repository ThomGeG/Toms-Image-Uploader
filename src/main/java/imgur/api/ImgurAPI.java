package main.java.imgur.api;

import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Wrapper/Facade to bring all imgur API's under one roof.
 * 
 * @see main.java.imgur.api.AlbumAPI
 * @see main.java.imgur.api.ImageAPI
 * @see main.java.imgur.api.AccountAPI
 * 
 * @see <a href="https://api.imgur.com/">api.imgur.com</a>
 * @see <a href="https://apidocs.imgur.com/">apidocs.imgur.com</a>
 *  
 * @author Tom
 */
@Service
public class ImgurAPI {
	
	public final AlbumAPI albums;
	public final ImageAPI images;
	public final AccountAPI accounts;
	
	@Autowired
	public ImgurAPI(AlbumAPI albums, ImageAPI images, AccountAPI accounts) {
		this.albums = albums;
		this.images = images;
		this.accounts = accounts;
	}
	
}
