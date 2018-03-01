package main.java.services.imgur;

import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Service class to dadkalkj.
 * 
 * @see main.java.services.imgur.AlbumAPI
 * @see main.java.services.imgur.ImageAPI
 * @see main.java.services.imgur.AccountAPI
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
