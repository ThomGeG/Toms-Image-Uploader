package main.java.services;

import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.beans.factory.annotation.Autowired;

import main.java.model.Account;
import main.java.model.ResponseWrapper;

/** 
 * Service class to handle API requests of an account related variety.
 * 
 * @see <a href="https://api.imgur.com/endpoints/account">api.imgur.com/endpoints/account</a>
 * @see <a href="https://apidocs.imgur.com/#a94d108b-d6e3-4e68-9521-47ea79501c85">apidocs.imgur.com</a>
 * 
 * @author Tom
 *
 */
@Service
public class AccountAPI {

	private final RESTService restAPI;
	
	@Autowired
	public AccountAPI(RESTService api) {
		this.restAPI = api;
	}
	
	/** 
	 * Retrieve standard user information about the current user.
	 * @see <a href="https://api.imgur.com/endpoints/account#account">api.imgur.com</a>
	 */
	public Account whoAmI() {
		return restAPI.request("https://api.imgur.com/3/account/me", HttpMethod.GET, new ParameterizedTypeReference<ResponseWrapper<Account>>() {});
	}
	
}
