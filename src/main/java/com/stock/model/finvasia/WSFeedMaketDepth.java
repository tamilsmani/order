
package com.stock.model.finvasia;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.Generated;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import lombok.AllArgsConstructor;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({ "stat", "request_time", "emsg" })
@Generated("jsonschema2pojo")
public class WSFeedMaketDepth {

	@JsonProperty("t")
	private String t;

	@JsonProperty("k")
	private String k;
	
	public WSFeedMaketDepth(String t, String k) {
		this.t = t;
		this.k = k;
	}
	
	@JsonIgnore
	private Map<String, Object> additionalProperties = new HashMap<String, Object>();

	public String getT() {
		return t;
	}

	public void setT(String t) {
		this.t = t;
	}



	public String getK() {
		return k;
	}

	public void setK(String k) {
		this.k = k;
	}

	public Map<String, Object> getAdditionalProperties() {
		return additionalProperties;
	}

	public void setAdditionalProperties(Map<String, Object> additionalProperties) {
		this.additionalProperties = additionalProperties;
	}
	
	
}