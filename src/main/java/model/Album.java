package main.java.model;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown=true)
public class Album {

	public String id;
	public String title;
	public String description;
	public int datetime;
	public String cover;
	public int cover_width;
	public int cover_height;
	public String account_url;
	public int account_id;
	public String privacy;
	public String layout;
	public int views;
	public String link;
	public boolean favourite;
	public boolean nsfw;
	public String section;
	public int order;
	public String deletehash;
	public int images_count;
	public List<Image> images;
	public boolean in_gallery;
	
	@Override
	public String toString() { 
		return String.format("Album[id='%s', title='%s', description='%s', link='%s', views='%s']", id, title, description, link, views);
	}
	
}
