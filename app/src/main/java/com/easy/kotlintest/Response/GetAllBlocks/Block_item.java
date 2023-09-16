package com.easy.kotlintest.Response.GetAllBlocks;

import com.google.gson.annotations.SerializedName;

public class Block_item {

	@SerializedName("to_user")
	private String toUser;

	@SerializedName("updated_at")
	private String updatedAt;

	@SerializedName("created_at")
	private String createdAt;

	@SerializedName("id")
	private String id;

	@SerializedName("thread")
	private String thread;

	@SerializedName("from_user")
	private String fromUser;

	public String getToUser(){
		return toUser;
	}

	public String getUpdatedAt(){
		return updatedAt;
	}

	public String getCreatedAt(){
		return createdAt;
	}

	public String getId(){
		return id;
	}

	public String getThread(){
		return thread;
	}

	public String getFromUser(){
		return fromUser;
	}
}