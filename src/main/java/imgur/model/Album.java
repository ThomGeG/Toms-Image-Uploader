package main.java.imgur.model;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/** 
 * Encapsulates the response model of an Imgur Album.
 * 
 * @see <a href="https://api.imgur.com/models/album">https://api.imgur.com/models/album</a>
 * 
 * @author ThomGeG
 */
@JsonIgnoreProperties(ignoreUnknown=true)
public class Album {

	/** Unique identifier for the album. */
	public String id;
	/** Short title of the album. */
	public String title;
	/** Large body of text that may accompany the album. */
	public String description;
	/** Album creation, epoch time. */
	public int datetime;
	/** Number of times the album has been viewed. */
	public int views;
	/** Indication of whether the album is marked as NSFW. Defaults to null if no information is available. */
	public boolean nsfw;
	/** Direct URL link to the the image. */
	public String link;
	
	/** 
	 * A list of the images contained within the album. 
	 * WARNING: Each image in the list is incomplete, there will be null/missing attributes. 
	 * Be sure to use the image end-points if you require exhaustive image meta-data.
	 */
	public List<Image> images;
	/** Number of images in the album. */
	public int images_count;
	
	/** ID of the albums cover image. */
	public String cover;
	/** Cover image width in pixels. */
	public int cover_width;
	/** Cover image height in pixels. */
	public int cover_height;
	/** The layout of the album. */
	public String layout;
	
	/** The account ID of the albums owner. */
	public int account_id;
	/** Direct URL link to the albums owner's account page. */
	public String account_url;
	
	/** The privacy level of the album (public, hidden, secret). */
	public String privacy;

	/** Order of the album in user's album list. */
	public int order;
	/** The delete hash of the album. Utilised for administration of anonymous images & albums. */
	public String deletehash;

	/** Has the album been submitted to the gallery. */
	public boolean in_gallery;
	
	@Override
	public String toString() { 
		return String.format("Album[id='%s', title='%s', images_count='%s', link='%s', views='%s']", id, title, images_count, link, views);
	}
	
}
