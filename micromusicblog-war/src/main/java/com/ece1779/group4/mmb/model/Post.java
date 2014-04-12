package com.ece1779.group4.mmb.model;

import java.beans.Transient;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.appengine.api.datastore.Blob;
import com.googlecode.objectify.Key;
import com.googlecode.objectify.annotation.Cache;
import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Id;

@JsonIgnoreProperties(ignoreUnknown=true)
@Entity
@Cache
public class Post {
	@Id
	String id; //Id is in the format of userAccount_timestamp so it would be unique and easier for query to compare

	@JsonProperty
	String comment;
	
//	@JsonProperty
//	byte[] backgroundImg;
	
	@JsonProperty
	Key<Post> key;
	
	@JsonProperty
	Blob data;
	
	@JsonProperty
	int hit;
	
	@JsonProperty
	long createdTime;
	
	@JsonProperty
	Key<UserInfo> ownerKey;
	
	@JsonProperty
	String format;
	
	@JsonProperty("format")
	public String getFormat() {
		return format;
	}

	public void setFormat(String format) {
		this.format = format;
	}

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

	@JsonProperty("data")
	public byte[] getData() {
		if(data!=null){
           return data.getBytes();
		}
		else{
			return null;
		}
	}

	public void setData(byte[] voiceData) {
		this.data=null;// free memory
		this.data = new Blob(voiceData);
	}
	
	@JsonProperty("ownerKey")
	public Key<UserInfo> getOwnerKey(){
		return this.ownerKey;
	}
	
	public void setOwnerKey(Key<UserInfo> ownerKey){
		this.ownerKey = ownerKey;
	}
	
	@Transient
	public Key<Post> getKey(){
		return Key.create(Post.class, id);
	}
}
