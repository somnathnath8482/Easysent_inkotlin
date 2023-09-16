package com.easy.kotlintest.Response.LoginHistory;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class LoginHistoryResponse{

	@SerializedName("code")
	private int code;

	@SerializedName("history")
	private List<HistoryItem> history;

	@SerializedName("message")
	private String message;

	public int getCode(){
		return code;
	}

	public List<HistoryItem> getHistory(){
		return history;
	}

	public String getMessage(){
		return message;
	}
}