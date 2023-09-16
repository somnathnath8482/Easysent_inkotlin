package com.easy.kotlintest.Response.GetGroups;

import com.google.gson.annotations.SerializedName;

public class GroupsItem{

	@SerializedName("date_of_join")
	private String dateOfJoin;

	@SerializedName("creator")
	private String creator;

	@SerializedName("group_primary_id")
	private String groupPrimaryId;

	@SerializedName("user_count")
	private String userCount;

	@SerializedName("user_id")
	private String userId;

	@SerializedName("group_id")
	private String groupId;

	@SerializedName("joind_by")
	private String joindBy;

	@SerializedName("group_user_id")
	private String groupUserId;

	@SerializedName("name")
	private String name;

	@SerializedName("group_desc")
	private String groupDesc;

	@SerializedName("created_at")
	private String createdAt;

	@SerializedName("group_master_id")
	private String groupMasterId;

	@SerializedName("dp")
	private String dp;

	public String getDateOfJoin(){
		return dateOfJoin;
	}

	public String getCreator(){
		return creator;
	}

	public String getGroupPrimaryId(){
		return groupPrimaryId;
	}

	public String getUserCount(){
		return userCount;
	}

	public String getUserId(){
		return userId;
	}

	public String getGroupId(){
		return groupId;
	}

	public String getJoindBy(){
		return joindBy;
	}

	public String getGroupUserId(){
		return groupUserId;
	}

	public String getName(){
		return name;
	}

	public String getGroupDesc(){
		return groupDesc;
	}

	public String getCreatedAt(){
		return createdAt;
	}

	public String getGroupMasterId(){
		return groupMasterId;
	}

	public String getDp() {
		return dp==null ? "":dp;
	}

	public void setDp(String dp) {
		this.dp = dp;
	}
}