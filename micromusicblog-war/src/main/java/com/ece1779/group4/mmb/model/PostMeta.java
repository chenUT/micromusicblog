package com.ece1779.group4.mmb.model;

import java.beans.Transient;
import java.util.ArrayList;
import java.util.List;

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
public class PostMeta {
	@Id
	String id; //Id is in the format of userAccount_timestamp so it would be unique and easier for query to compare

	@JsonProperty
	String comment;
	
	@JsonProperty
	Key<PostMeta> key;
	
	@JsonProperty
	Key<PostMeta> backgroundPostMetaKey;
	
	public Key<PostMeta> getBackgroundPostMetaKey() {
		return backgroundPostMetaKey;
	}

	public void setBackgroundPostMetaKey(Key<PostMeta> backgroundPostMetaKey) {
		this.backgroundPostMetaKey = backgroundPostMetaKey;
	}

	@JsonProperty
	Blob data;
	
	@JsonProperty
	int hit;
	
	@JsonProperty
	long createdTime;
	
	@JsonProperty
	String format;
	
	 List<Key<PostData>> dataList = new ArrayList<Key<PostData>>();
	
	public List<Key<PostData>> getDataMap() {
		return dataList;
	}

	public void setDataMap(List<Key<PostData>> dataMap) {
		this.dataList = dataMap;
	}

	public void addPostData(Key<PostData> data, int index){
		dataList.add(index,data);
	}
	
	public Key<PostData> getPostDataKey(int index){
		return dataList.get(index);
	}
	
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

	//for thread safety
	public synchronized void addHit(){
			hit++;
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
	
	@Transient
	public Key<PostMeta> getKey(){
		return Key.create(PostMeta.class, id);
	}
}
