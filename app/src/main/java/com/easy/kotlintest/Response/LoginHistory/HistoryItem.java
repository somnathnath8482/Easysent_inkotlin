package com.easy.kotlintest.Response.LoginHistory;

import com.google.gson.annotations.SerializedName;

public class HistoryItem{

	@SerializedName("device_id")
	private String deviceId;

	@SerializedName("user_id")
	private String userId;

	@SerializedName("created_at")
	private String createdAt;

	@SerializedName("model")
	private String model;

	@SerializedName("id")
	private String id;

	@SerializedName("manufacturer")
	private String manufacturer;

	public String getDeviceId(){
		return deviceId;
	}

	public String getUserId(){
		return userId;
	}

	public String getCreatedAt(){
		return createdAt;
	}

	public String getModel(){
		return model;
	}

	public String getId(){
		return id;
	}

	public String getManufacturer(){
		return manufacturer;
	}
}