package com.stock.model.finvasia;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class FinVasiaOrderResponse {
	
	@JsonProperty("request_time")
	String requestTime;
	
	@JsonProperty("stat")
	String status;
	
	@JsonProperty("norenordno")
	String norenordno;

	public String getRequestTime() {
		return requestTime;
	}

	public void setRequestTime(String requestTime) {
		this.requestTime = requestTime;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getNorenordno() {
		return norenordno;
	}

	public void setNorenordno(String norenordno) {
		this.norenordno = norenordno;
	}
	
	
}