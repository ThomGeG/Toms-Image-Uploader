package main.java.model.imgur;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/** 
 * Encapsulates the response model of an Imgur Image.
 * 
 * @see <a href="https://api.imgur.com/models/image">https://api.imgur.com/models/image</a>
 * 
 * @author ThomGeG
 */
@JsonIgnoreProperties(ignoreUnknown=true)
public class Image {
	
	/** Unique identifier for the image. */
	public String id;
	/** Short title of the image. */
	public String title;
	/** Large body of text that may accompany the image. */
	public String description;
	/** When the image was uploaded, epoch time. */
	public long datetime;
	/** Number of times the image has been viewed. */
	public int views;
	/** Indication of whether the image is marked as NSFW. Defaults to null if no information is available. */
	public boolean nsfw;
	/** Original filename before upload. */
	public String name;
	/** Direct URL link to the the image. */
	public String link;
	
	/** Image MIME type. */
	public String type;
	/** Is the image animated */
	public boolean animated;
	/** Image width in pixels. */
	public int width;
	/** Image height in pixels. */
	public int height;
	/** Size of the image in bytes. */
	public long size;
	
	/** Bandwidth consumed by the image in bytes */
	public long bandwidth;
	/** The delete hash of the image. Utilised for administration of anonymous images & albums. */
	public String deletehash;
	
	@Override
	public String toString() { 
		return String.format("Image[id='%s', title='%s', name='%s', link='%s', views='%s']", id, title, name, link, views);
	}

}
