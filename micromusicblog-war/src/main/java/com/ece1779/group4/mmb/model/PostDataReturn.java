package com.ece1779.group4.mmb.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.googlecode.objectify.annotation.Id;

public class PostDataReturn {
	@JsonProperty
	byte[][] data;
	
	@JsonProperty
	String[] format;

	@JsonProperty("data")
	public byte[][] getData() {
		return data;
	}

	public void setData(byte[][] data) {
		this.data = data;
	}
	
	@JsonProperty("format")
	public String[] getFormat(){
		return this.format;
	}
	
	public void setFormat(String[] format){
		this.format = format;
	}
	
}
