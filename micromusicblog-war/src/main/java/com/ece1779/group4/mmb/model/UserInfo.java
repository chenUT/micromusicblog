package com.ece1779.group4.mmb.model;

import java.beans.Transient;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.googlecode.objectify.Key;
import com.googlecode.objectify.annotation.Cache;
import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Id;
import com.googlecode.objectify.annotation.Index;

@JsonIgnoreProperties(ignoreUnknown=true)
@Cache
@Entity
public class UserInfo {

	@Id
	@JsonProperty
	String accountName;
	
	@Index
	@JsonProperty
	String profileName;
	
	@JsonProperty
	List<Key<UserInfo>> followings = new ArrayList<Key<UserInfo>>();
	
	@JsonProperty
	List<Key<Post>> posts = new ArrayList<Key<Post>>();

	@JsonProperty
	int followerCount;

	@JsonProperty("accountName")
	public String getAccountName() {
		return accountName;
	}
	
	@JsonProperty("profileName")
	public String getProfileName(){
		return this.profileName;
	}
	
	public void setProfileName(String profileName){
		this.profileName = profileName;
	}
	
	public void setAccountName(String name) {
		this.accountName = name;
	}

	@JsonProperty("followings")
	public List<Key<UserInfo>> getFollowings() {
		return followings;
	}

	public void setFollowings(List<Key<UserInfo>> flollowings) {
		this.followings = flollowings;
	}

	@JsonProperty("posts")
	public List<Key<Post>> getPosts() {
		return posts;
	}

	public void setPosts(List<Key<Post>> posts) {
		this.posts = posts;
	}
	
	@JsonProperty("followerCount")
	public int getFollowerCount(){
		return this.followerCount;
	}
	
	public void setFollowerCount(int followerCount){
		this.followerCount=followerCount;
	}
	
	public void incrementFollowerCount(){
		this.followerCount += 1;
	}
	
	public void addFollowing(Key<UserInfo> following){
		this.followings.add(following);
	}
	
	public void removeFollowing(Key<UserInfo> following){
		this.followings.remove(following);
	}
	
	@Transient
	public Key<UserInfo> getKey() {
	   return Key.create(UserInfo.class, accountName);
	}

	public void decrementFollowerCount() {
		this.followerCount += 1;
	}
}