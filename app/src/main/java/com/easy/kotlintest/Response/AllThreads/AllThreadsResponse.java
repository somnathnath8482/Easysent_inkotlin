package com.easy.kotlintest.Response.AllThreads;

import java.util.List;

public class AllThreadsResponse{
	private int code;
	private List<ThreadsItem> threads;
	private String message;

	public int getCode(){
		return code;
	}

	public List<ThreadsItem> getThreads(){
		return threads;
	}

	public String getMessage(){
		return message;
	}
}