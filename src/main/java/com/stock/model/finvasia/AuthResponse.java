package com.stock.model.finvasia;

import javax.annotation.processing.Generated;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
@Generated("jsonschema2pojo")
public class AuthResponse {

	@JsonProperty("actid")
	private String actid;
	
	@JsonProperty("susertoken")
	private String susertoken;

	public String getActid() {
		return actid;
	}

	public void setActid(String actid) {
		this.actid = actid;
	}

	public String getSusertoken() {
		return susertoken;
	}

	public void setSusertoken(String susertoken) {
		this.susertoken = susertoken;
	}

	
}