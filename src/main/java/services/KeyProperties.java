package main.java.services;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties("services")
public class KeyProperties {

	@Value("${CLIENT_ID}")
	private String CLIENT_ID;
	@Value("${CLIENT_SECRET}")
	private String CLIENT_SECRET;
	@Value("${REFRESH_TOKEN}")
	private String REFRESH_TOKEN;
	@Value("${ACCESS_TOKEN}")
	private String ACCESS_TOKEN;
	
	public String getClientID() {
		return CLIENT_ID;
	}

	public String getClientSecret() {
		return CLIENT_SECRET;
	}

	public String getRefreshToken() {
		return REFRESH_TOKEN;
	}

	public String getAccessToken() {
		return ACCESS_TOKEN;
	}

	public void setAccessToken(String ACCESS_TOKEN) {
		ACCESS_TOKEN = this.ACCESS_TOKEN;
	}
	
	@Override
	public String toString() {
		return String.format("Keys[clid='%s', secret='%s', refresh_token='%s', access_token='%s']", CLIENT_ID, CLIENT_SECRET, REFRESH_TOKEN, ACCESS_TOKEN);
	}
	
}
