package com.ece1779.group4.mmb.model;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * json POJO return to user describe general user information in search result
 * @author chen
 *
 */
public class UserDetail {
	@JsonProperty
    String accountName;
	
	@JsonProperty("accountName")
	public String getAccountName() {
		return accountName;
	}

	public void setAccountName(String accountName) {
		this.accountName = accountName;
	}

	@JsonProperty
	String profileName;
	
	public void setProfileName(String profileName){
		this.profileName = profileName;
	}
	@JsonProperty("profileName")
	public String getProfileName(){
		return this.profileName;
	}
	
	@JsonProperty
	boolean isFollowing;
	

	public void setIsFollowing(boolean isFollowing){
		this.isFollowing = isFollowing;
	}
	@JsonProperty("isFollowing")
	public boolean getIsFollowing(){
		return this.isFollowing;
	}
}
