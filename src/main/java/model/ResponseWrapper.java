package main.java.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * A wrapper for responses from Imgur's RESTful API.
 * Imgur wraps its request payloads with both a status code and success flag. Actual requested data is contained inside this wrapper.
 *
 * @param <T> Model of data expected to be returned, such as main.java.model.Album or main.java.model.Image.
 * 
 * @author Tom
 */
@JsonIgnoreProperties(ignoreUnknown=true)
public class ResponseWrapper<T> {
	
	/** HTTP status code of request attempt. */
	public int status;
	/** Success flag for request. If false, no data was returned and an error occured. */
	public boolean success;
	
	/** Payload data of your request. */
	public T data;
	
	@Override
	public String toString() {
		return String.format("Response[success=%s, status='%s', data='%s']", success, status, data.getClass());
	}
	
}
