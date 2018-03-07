package main.java.imgur.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/** 
 * Encapsulates the response model of an imgur Token request.<br>
 * This model's sole purpose is requesting new access tokens.<br>
 * Do not worry about storing the refresh_token persistently; it has not changed.
 * 
 * @see <a href="https://api.imgur.com/oauth2#refresh_tokens">api.imgur.com/oath2#refresh_tokens</a>
 * 
 * @author ThomGeG
 */
@JsonIgnoreProperties(ignoreUnknown=true)
public class Tokens {
	
	/** Time until token expires in ? units. Typically 3600. The Imgur API is poorly documented... */
	public int expires_in;
	
	/** Access token to bear and authorise our requests. */
	public String access_token;
	/** Key/Token necessary to request further access tokens. */
	public String refresh_token;
	
	/** Username for the account associated with these tokens. */
	public String account_username;
	
	@Override
	public String toString() {
		return String.format("Tokens[account_username='%s', access_token='%s']", account_username, access_token);
	}

}
