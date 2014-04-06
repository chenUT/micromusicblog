package com.ece1779.group4.mmb.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.googlecode.objectify.annotation.Cache;
import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Id;

@JsonIgnoreProperties(ignoreUnknown=true)
@Entity
public class Post {
	@Id
	String id; //Id is in the format of userAccount_timestamp so it would be unique and easier for query to compare

	@JsonProperty
	String comment;
	
//	@JsonProperty
//	byte[] backgroundImg;
	
	@JsonProperty
	byte[] voiceData;
	
	@JsonProperty
	int hit;
	
	@JsonProperty
	long createdTime;
	
	@JsonProperty
	String userAccount;
	
	@JsonProperty("hit")
	public int getHit() {
		return hit;
	}

	public void setHit(int hit) {
		this.hit = hit;
	}

	@JsonProperty("createdTime")
	public long getCreatedTime() {
		return createdTime;
	}

	public void setCreatedTime(long createdTime) {
		this.createdTime = createdTime;
	}

	@JsonProperty("id")
	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	@JsonProperty("comment")
	public String getComment() {
		return comment;
	}

	public void setComment(String comment) {
		this.comment = comment;
	}

//	@JsonProperty("background")
//	public byte[] getBackground() {
//		return backgroundImg;
//	}
//
//	public void setBackground(byte[] backgroundImg) {
//		this.backgroundImg = backgroundImg;
//	}

	@JsonProperty("voiceData")
	public byte[] getData() {
		return voiceData;
	}

	public void setData(byte[] voiceData) {
		this.voiceData = voiceData;
	}
	
	@JsonProperty("userAccount")
	public String getUserAccount(){
		return this.userAccount;
	}
	
	public void setUserAccount(String userAccount){
		this.userAccount = userAccount;
	}
}
