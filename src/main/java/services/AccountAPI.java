package main.java.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import main.java.model.Account;

@Service
public class AccountAPI {

	private final RESTService restAPI;
	
	@Autowired
	public AccountAPI(RESTService api) {
		this.restAPI = api;
	}
	
	public Account getMe() {
		return null;
		//https://api.imgur.com/3/account/me
	}
	
}
