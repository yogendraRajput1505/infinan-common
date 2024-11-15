package com.infinan.common.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;

@Entity
public class KiteLoginCredentials {

	@Id
	private String userId;
	
	private String accessToken;
	
	private String publicToken;
	
	
	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public String getAccessToken() {
		return accessToken;
	}

	public void setAccessToken(String accessToken) {
		this.accessToken = accessToken;
	}

	public String getPublicToken() {
		return publicToken;
	}

	public void setPublicToken(String publicToken) {
		this.publicToken = publicToken;
	}

	@Override
	public String toString() {
		return "KiteLoginCredentials [userId=" + userId + ", accessToken=" + accessToken + ", publicToken=" + publicToken
				+ "]";
	}
	
	
}
