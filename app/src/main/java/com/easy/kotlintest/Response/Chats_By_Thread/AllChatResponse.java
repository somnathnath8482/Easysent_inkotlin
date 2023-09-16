package com.easy.kotlintest.Response.Chats_By_Thread;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class AllChatResponse{

	@SerializedName("code")
	private int code;

	@SerializedName("chats")
	private List<ChatsItem> chats;

	@SerializedName("message")
	private String message;

	public int getCode(){
		return code;
	}

	public List<ChatsItem> getChats(){
		return chats;
	}

	public String getMessage(){
		return message;
	}
}