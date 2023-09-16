package com.easy.kotlintest.Response.Register;

import com.google.gson.annotations.SerializedName;

public class RegisterResponse{

	@SerializedName("code")
	private int code;

	@SerializedName("mail")
	private int mail;

	@SerializedName("success")
	private String success;

	@SerializedName("otp")
	private String otp;

	public int getCode(){
		return code;
	}

	public int getMail(){
		return mail;
	}

	public String getSuccess(){
		return success;
	}

	public String getOtp(){
		return otp;
	}
}