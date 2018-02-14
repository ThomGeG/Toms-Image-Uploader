package main.java.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/** 
 * Encapsulates the response model of an Imgur Account.
 * For more information see:
 * 	@see <a href="https://api.imgur.com/models/account">https://api.imgur.com/models/account</a>
 * 
 * @author ThomGeG
 */
@JsonIgnoreProperties(ignoreUnknown=true)
public class Account {
	
	/** Unique identifier for the account. */
	public int id;
	/** Direct URL link to the the image. */
	public String url;
	/** Small descriptive field the user has filled about themself.*/
	public String bio;
	/** The users reputation in its numerical form. */
	public float reputation;
	/** Epoch time of account creation. */
	public int created;
	
	@Override
	public String toString() {
		return String.format("Account[id='%s', url='%s', created='%s']", id, url, created);
	}

}
