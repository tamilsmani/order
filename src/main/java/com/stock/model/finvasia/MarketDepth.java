
package com.stock.model.finvasia;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.Generated;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
@Generated("jsonschema2pojo")
public class MarketDepth {

	@JsonProperty("tk")
	private String tk;
	 
	@JsonProperty("bp1")
	private String bp1;
	
	@JsonProperty("sp1")
	private String sp1;

	public String getBp1() {
		return bp1;
	}

	public void setBp1(String bp1) {
		this.bp1 = bp1;
	}

	public String getSp1() {
		return sp1;
	}

	public void setSp1(String sp1) {
		this.sp1 = sp1;
	}

	public String getTk() {
		return tk;
	}

	public void setTk(String tk) {
		this.tk = tk;
	}
	
}