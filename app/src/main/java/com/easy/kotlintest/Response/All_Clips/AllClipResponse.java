package com.easy.kotlintest.Response.All_Clips;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class AllClipResponse{

	@SerializedName("code")
	private int code;

	@SerializedName("clips")
	private List<ClipsItem> clips;

	@SerializedName("message")
	private String message;

	public int getCode(){
		return code;
	}

	public List<ClipsItem> getClips(){
		return clips;
	}

	public String getMessage(){
		return message;
	}
}