package main.java.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown=true)
public class Account {
	
	int id;
	String url;
	String bio;
	double reputation;
	int created;

}
