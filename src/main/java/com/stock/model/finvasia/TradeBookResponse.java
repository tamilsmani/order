
package com.stock.model.finvasia;

import javax.annotation.Generated;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonPropertyOrder({ "stat", "request_time", "emsg" })
public class TradeBookResponse {

	@JsonProperty("stat")
	private String stat;
	@JsonProperty("request_time")
	private String requestTime;
	@JsonProperty("emsg")
	private String emsg;
	
	@JsonProperty("norenordno")
	private String norenordno;
	
	@JsonProperty("stat")
	public String getStat() {
		return stat;
	}

	@JsonProperty("stat")
	public void setStat(String stat) {
		this.stat = stat;
	}

	@JsonProperty("request_time")
	public String getRequestTime() {
		return requestTime;
	}

	@JsonProperty("request_time")
	public void setRequestTime(String requestTime) {
		this.requestTime = requestTime;
	}

	@JsonProperty("emsg")
	public String getEmsg() {
		return emsg;
	}

	@JsonProperty("emsg")
	public void setEmsg(String emsg) {
		this.emsg = emsg;
	}

	public String getNorenordno() {
		return norenordno;
	}

	public void setNorenordno(String norenordno) {
		this.norenordno = norenordno;
	}

}