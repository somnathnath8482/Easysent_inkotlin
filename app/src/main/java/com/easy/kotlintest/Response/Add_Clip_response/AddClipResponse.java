package com.easy.kotlintest.Response.Add_Clip_response;

import com.google.gson.annotations.SerializedName;

public class AddClipResponse{

	@SerializedName("code")
	private int code;

	@SerializedName("success")
	private String success;

	@SerializedName("name")
	private String name;

	@SerializedName("attach")
	private String attach;

	@SerializedName("ok")
	private int ok;

	@SerializedName("type")
	private String type;

	@SerializedName("status")
	private String status;

	public int getCode(){
		return code;
	}

	public String getSuccess(){
		return success;
	}

	public String getName(){
		return name;
	}

	public String getAttach(){
		return attach;
	}

	public int getOk(){
		return ok;
	}

	public String getType(){
		return type;
	}

	public String getStatus(){
		return status;
	}
}