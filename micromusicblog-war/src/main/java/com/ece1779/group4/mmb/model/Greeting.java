package com.ece1779.group4.mmb.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Id;

@JsonIgnoreProperties(ignoreUnknown=true)
@Entity
public class Greeting {
		@Id 
		Long id;
		
		@JsonProperty
	    String content;
	    
	    public Greeting(){}
	    
	    public Greeting(String content) {
	        this.content = content;
	    }
	    
		@JsonProperty("content")
	    public String getContent() {
			return content;
		}
	
		@JsonProperty("id")
		public Long getId(){
			return this.id;
		}
		
		public void setId(long id){
			this.id = id;
		}
		
		public void setContent(String content) {
			this.content = content;
		}
}
