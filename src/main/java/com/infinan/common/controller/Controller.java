package com.infinan.common.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import com.infinan.common.service.LoginToKite;

@RestController
@CrossOrigin
public class Controller {
	@Autowired
	LoginToKite kite;
	
	@PostMapping("login") 
	public String kiteLoginHandler(String req_token) {
		return kite.connectToKite(req_token);
	}
}
