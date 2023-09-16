package com.easy.kotlintest.Response.GetGroupUser;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class GetGroupResponse{

	@SerializedName("code")
	private int code;

	@SerializedName("message")
	private String message;

	@SerializedName("users")
	private List<UsersItem> users;

	public int getCode(){
		return code;
	}

	public String getMessage(){
		return message;
	}

	public List<UsersItem> getUsers(){
		return users;
	}
}