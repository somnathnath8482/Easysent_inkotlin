package com.easy.kotlintest.Response.All_Clips;

import com.google.gson.annotations.SerializedName;

public class ClipsItem{

	@SerializedName("rect")
	private Rect rect;

	@SerializedName("data")
	private Data data;

	public Rect getRect(){
		return rect;
	}

	public Data getData(){
		return data;
	}
}