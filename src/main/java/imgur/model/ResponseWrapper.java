package main.java.imgur.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * A wrapper for responses from Imgur's RESTful API.<br><br>
 * Imgur wraps most of its response payloads with both a status code and success flag. The requested data is contained alongside under the identifier <i>data</i>.
 * This class/POJO sits over the expected response object to maintain type safety while preventing some serious code smells from duplicated, verbose, tightly coupled code.
 * 
 * @param <T> Class/type of the expected response type/model, such as {@link main.java.imgur.model.Album}, {@link main.java.imgur.model.Image} or even just {@code Boolean}.
 * 
 * @author Tom
 */
@JsonIgnoreProperties(ignoreUnknown=true)
public class ResponseWrapper<T> {
	
	/** HTTP status code of request attempt. */
	public int status;
	/** Success flag for request attempt. If false, no data was returned and an error occurred. */
	public boolean success;
	
	/** Payload data of your request. */
	public T data;
	
	@Override
	public String toString() {
		return String.format("Response[success=%s, status='%s', data='%s']", success, status, data.getClass());
	}
	
}
