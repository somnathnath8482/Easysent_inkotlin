package com.easy.kotlintest.Response.Login;

import com.google.gson.annotations.SerializedName;

public class LoginResponse{
	public void setUser(User user) {
		this.user = user;
	}

	@SerializedName("code")
	private int code;

	@SerializedName("message")
	private String message;

	@SerializedName("login_token")
	private String login_token;

	@SerializedName("user")
	private User user;

	public int getCode(){
		return code;
	}

	public String getMessage(){
		return message;
	}

	public User getUser(){
		return user;
	}

	public String getlogin_token() {
		return login_token;
	}
}