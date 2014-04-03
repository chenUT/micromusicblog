package com.ece1779.group4.mmb.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Id;

@JsonIgnoreProperties(ignoreUnknown=true)
@Entity
public class Post {
	@Id
	Long id;

	@JsonProperty
	String comment;
	
	@JsonProperty
	byte[] backgroundImg;
	
	@JsonProperty
	byte[] voiceData;
	
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	@JsonProperty("comment")
	public String getComment() {
		return comment;
	}

	public void setComment(String comment) {
		this.comment = comment;
	}

	@JsonProperty("background")
	public byte[] getBackground() {
		return backgroundImg;
	}

	public void setBackground(byte[] backgroundImg) {
		this.backgroundImg = backgroundImg;
	}

	@JsonProperty("voiceData")
	public byte[] getData() {
		return voiceData;
	}

	public void setData(byte[] voiceData) {
		this.voiceData = voiceData;
	}
}
