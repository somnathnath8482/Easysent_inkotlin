package com.easy.kotlintest.Response.getIp;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class GetIp{

	@SerializedName("code")
	private int code;

	@SerializedName("ips")
	private List<String> ips;

	public int getCode(){
		return code;
	}

	public List<String> getIps(){
		return ips;
	}
}