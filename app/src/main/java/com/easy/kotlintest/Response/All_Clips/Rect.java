package com.easy.kotlintest.Response.All_Clips;

import com.google.gson.annotations.SerializedName;

public class Rect{

	@SerializedName("user_id")
	private String userId;

	@SerializedName("is_like")
	private String isLike;

	@SerializedName("created_at")
	private String createdAt;

	@SerializedName("clip_id")
	private String clipId;

	@SerializedName("comment")
	private String comment;

	@SerializedName("id")
	private String id;

	public String getUserId(){
		return userId;
	}

	public String getIsLike(){
		return isLike;
	}

	public String getCreatedAt(){
		return createdAt;
	}

	public String getClipId(){
		return clipId;
	}

	public String getComment(){
		return comment;
	}

	public String getId(){
		return id;
	}
}