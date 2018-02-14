package main.java.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown=true)
public class Image {
	
	public String id;
	public String title;
	public String description;
	public int datetime;
	public String type;
	public boolean animated;
	public int width;
	public int height;
	public int size;
	public int views;
	public int bandwidth;
	public String deletehash;
	public String name;
	public String link;
	public String gifv;
	public String mp4;
	public String mp4_size;
	public String looping;
	public boolean favourite;
	public boolean nsfw;
	public String vote;
	public boolean in_gallery;
	
	@Override
	public String toString() { 
		return String.format("Image[id='%s', title='%s', link='%s', views='%s']", id, title, link, views);
	}

}
