package com.easy.kotlintest.Response.Chats_By_Thread;

import com.easy.kotlintest.Encription.Encripter;
import com.google.gson.annotations.SerializedName;


public class ChatsItem{

	@SerializedName("attachment")
	private String attachment;

	@SerializedName("sender")
	private String sender;

	@SerializedName("created_at")
	private String createdAt;

	@SerializedName("id")
	private String id;

	@SerializedName("thread")
	private String thread;

	@SerializedName("replay_of")
	private String replay_of;

	@SerializedName("message")
	private String message;

	@SerializedName("type")
	private String type;

	@SerializedName("reciver")
	private String reciver;

	@SerializedName("seen")
	private String seen;

	@SerializedName("is_deleted")
	private String is_deleted;

	public String getAttachment(){
		return attachment;
	}

	public String getSender(){
		return sender;
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

	public String getMessage(){
		//return message;

		Encripter encripter = new Encripter(getSender());

		return encripter.decrepit(message);

	}

	public String getType(){
		return type;
	}

	public String getReciver(){
		return reciver;
	}

	public String getSeen(){
		return seen;
	}

	public String getReplay_of() {
		return replay_of;
	}

	public String getIs_deleted() {
		return is_deleted;
	}
}