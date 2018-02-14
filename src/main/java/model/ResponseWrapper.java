package main.java.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown=true)
public class ResponseWrapper<T> {
	
	public int status;
	public boolean success;
	
	public T data;
	
	@Override
	public String toString() {
		return String.format("Response[success=%s, status='%s', data='%s']", success, status, data.getClass());
	}
	
}
