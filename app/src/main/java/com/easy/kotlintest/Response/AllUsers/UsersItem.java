package com.easy.kotlintest.Response.AllUsers;

import com.google.gson.annotations.SerializedName;

public class UsersItem{

	@SerializedName("gender")
	private String gender;

	@SerializedName("about")
	private String about;

	@SerializedName("profile_pic")
	private String profilePic;

	@SerializedName("created_at")
	private String createdAt;

	@SerializedName("token")
	private String token;

	@SerializedName("password")
	private String password;

	@SerializedName("updated_at")
	private String updatedAt;

	@SerializedName("phone")
	private String phone;

	@SerializedName("name")
	private String name;

	@SerializedName("id")
	private String id;

	@SerializedName("is_verifyed")
	private String isVerifyed;

	@SerializedName("email")
	private String email;

	@SerializedName("username")
	private String username;

	public String getGender(){
		return gender;
	}

	public String getAbout(){
		return about;
	}

	public String getProfilePic(){
		return profilePic;
	}

	public String getCreatedAt(){
		return createdAt;
	}

	public String getToken(){
		return token;
	}

	public String getPassword(){
		return password;
	}

	public String getUpdatedAt(){
		return updatedAt;
	}

	public String getPhone(){
		return phone;
	}

	public String getName(){
		return name;
	}

	public String getId(){
		return id;
	}

	public String getIsVerifyed(){
		return isVerifyed;
	}

	public String getEmail(){
		return email;
	}

	public String getUsername(){
		return username;
	}
}