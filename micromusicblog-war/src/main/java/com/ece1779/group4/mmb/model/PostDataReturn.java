package com.ece1779.group4.mmb.model;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * the is the json object POJO we return to user when user request post data to play
 * @author chen
 *
 */
public class PostDataReturn {
	//Foreground audio
	@JsonProperty
	byte[] data1;

	//Background audio
	@JsonProperty
	byte[] data2;
	
	@JsonProperty
	String format1;

	@JsonProperty
	String format2;

	@JsonProperty("data1")
	public byte[] getData1() {
		return data1;
	}

	@JsonProperty("data2")
	public byte[] getData2() {
		return data2;
	}

	public void setData1(byte[] data) {
		this.data1 = data;
	}

	public void setData2(byte[] data) {
		this.data2 = data;
	}
	
	@JsonProperty("format1")
	public String getFormat1(){
		return this.format1;
	}
	
	public void setFormat1(String format){
		this.format1 = format;
	}

		@JsonProperty("format2")
	public String getFormat2(){
		return this.format2;
	}
	
	public void setFormat2(String format){
		this.format2 = format;
	}
	
}
