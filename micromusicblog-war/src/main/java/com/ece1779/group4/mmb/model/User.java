package com.ece1779.group4.mmb.model;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.googlecode.objectify.Key;
import com.googlecode.objectify.annotation.Cache;
import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Id;

@JsonIgnoreProperties(ignoreUnknown=true)
@Entity
@Cache
public class User {

	@Id
	Long id;
	
	@JsonProperty
	String name;
	
	@JsonProperty
	List<Key<User>> followings;
	
	@JsonProperty
	List<Key<Post>> posts;

	@JsonProperty("id")
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	@JsonProperty("name")
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@JsonProperty("followings")
	public List<Key<User>> getFollowings() {
		return followings;
	}

	public void setFollowings(List<Key<User>> followings) {
		this.followings = followings;
	}

	@JsonProperty("posts")
	public List<Key<Post>> getPosts() {
		return posts;
	}

	public void setPosts(List<Key<Post>> posts) {
		this.posts = posts;
	}
}
