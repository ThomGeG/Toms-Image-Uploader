package main.java.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown=true)
public class JSONResponse {
	
	public boolean success;
	public int status;
	
	public Album data;
	
	@Override
	public String toString() {
		return String.format("Response[success=%s, status='%s', data=%s]", success, status, data.toString());
	}
	
}
