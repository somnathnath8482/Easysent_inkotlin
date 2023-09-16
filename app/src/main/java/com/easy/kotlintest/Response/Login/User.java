package com.easy.kotlintest.Response.Login;

import com.google.gson.annotations.SerializedName;

public class User{

	@SerializedName("password")
	private String password;

	@SerializedName("updated_at")
	private String updatedAt;

	@SerializedName("phone")
	private String phone;

	@SerializedName("name")
	private String name;

	@SerializedName("profile_pic")
	private String profilePic;

	@SerializedName("created_at")
	private String createdAt;

	@SerializedName("id")
	private String id;

	@SerializedName("gender")
	private String gender;

	@SerializedName("about")
	private String about;


	@SerializedName("is_verifyed")
	private String isVerifyed;

	@SerializedName("email")
	private String email;

	@SerializedName("username")
	private String username;

	@SerializedName("token")
	private Object token;

	public User(String phone, String name, String profilePic, String gender, String about, String isVerifyed, String email, String username, Object token) {
		this.phone = phone;
		this.name = name;
		this.profilePic = profilePic;
		this.gender = gender;
		this.about = about;
		this.isVerifyed = isVerifyed;
		this.email = email;
		this.username = username;
		this.token = token;
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

	public String getProfilePic(){
		return profilePic;
	}

	public String getCreatedAt(){
		return createdAt;
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

	public Object getToken(){
		return token;
	}

	public String getGender() {
		return gender;
	}

	public String getAbout() {
		return about;
	}
}