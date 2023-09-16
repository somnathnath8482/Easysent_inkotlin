package com.easy.kotlintest.Response.All_Clips;

import com.google.gson.annotations.SerializedName;

public class Data{

	@SerializedName("file")
	private String file;

	@SerializedName("comments")
	private String comments;

	@SerializedName("user_id")
	private String userId;

	@SerializedName("file_name")
	private String fileName;

	@SerializedName("created_at")
	private String createdAt;

	@SerializedName("reactions")
	private String reactions;

	@SerializedName("id")
	private String id;

	@SerializedName("views")
	private String views;

	@SerializedName("desc")
	private Object desc;

	@SerializedName("likes")
	private String likes;

	@SerializedName("dis_likes")
	private String dis_likes;

	public String getFile(){
		return file;
	}

	public String getComments(){
		return comments;
	}

	public String getUserId(){
		return userId;
	}

	public String getFileName(){
		return fileName;
	}

	public String getCreatedAt(){
		return createdAt;
	}

	public String getReactions(){
		return reactions;
	}

	public String getId(){
		return id;
	}

	public String getViews(){
		return views;
	}

	public Object getDesc(){
		return desc;
	}

	public String getLikes(){
		return likes;
	}

	public String getDis_likes() {
		return dis_likes;
	}
}