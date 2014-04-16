package com.ece1779.group4.mmb.model;

import java.beans.Transient;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.googlecode.objectify.Key;
import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Id;

/**
 * The is the postdata entity stored in datastore
 * @author chen
 *
 */
@Entity
public class PostData {
	@Id
	Long postId;
	
	public Long getPostId() {
		return postId;
	}

	public void setPostId(Long postId) {
		this.postId = postId;
	}

	@JsonProperty
	byte[] data;
	
	@JsonProperty
	String format;

	@JsonProperty("data")
	public byte[] getData() {
		return data;
	}

	public void setData(byte[] data) {
		this.data = data;
	}
	
	@JsonProperty("format")
	public String getFormat(){
		return this.format;
	}
	
	public void setFormat(String format){
		this.format = format;
	}
	
	@Transient
	public Key<PostData> getKey(){
		return Key.create(PostData.class, postId);
	}
}

