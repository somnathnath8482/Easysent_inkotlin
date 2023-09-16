package com.easy.kotlintest.Response.GetGroups;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class GetgroupsResponse{

	@SerializedName("code")
	private int code;

	@SerializedName("groups")
	private List<GroupsItem> groups;

	@SerializedName("message")
	private String message;

	public int getCode(){
		return code;
	}

	public List<GroupsItem> getGroups(){
		return groups;
	}

	public String getMessage(){
		return message;
	}
}