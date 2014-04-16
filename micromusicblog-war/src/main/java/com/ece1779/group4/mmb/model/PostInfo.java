package com.ece1779.group4.mmb.model;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * post summary we return back to user as general post info
 * @author chen
 *
 */
public class PostInfo {
	
	@JsonProperty
	String comment;
	
	@JsonProperty("comment")
	public String getComment() {
		return comment;
	}

	public void setComment(String comment) {
		this.comment = comment;
	}

	@JsonProperty("postKey")
	public String getPostKey() {
		return postKey;
	}

	public void setPostKey(String postKey) {
		this.postKey = postKey;
	}

	@JsonProperty
	String ownerKey;
	
	@JsonProperty("ownerKey")
	public String getOwnerKey(){
		return this.ownerKey;
	}
	
	public void setOwnerKey(String ownerKey){
		this.ownerKey = ownerKey;
	}
	
	@JsonProperty
	String postKey;
	
	@JsonProperty
	String creater;
	
	
	@JsonProperty("creater")
	public String getCreater() {
		return creater;
	}

	public void setCreater(String creater) {
		this.creater = creater;
	}
	
	@JsonProperty
	long createTime;
	
	@JsonProperty("createTime")
	public long getCreateTime() {
		return createTime;
	}

	public void setCreateTime(long createTime) {
		this.createTime = createTime;
	}
	
}
