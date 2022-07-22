package com.stock.model.finvasia;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.processing.Generated;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({ "apkversion", "uid", "pwd", "factor2", "imei", "source", "vc", "appkey" })
@Generated("jsonschema2pojo")
public class Auth {

	@JsonProperty("apkversion")
	private String apkversion = "1.0.0";
	@JsonProperty("uid")
	private String uid;
	@JsonProperty("pwd")
	private String pwd;
	@JsonProperty("factor2")
	private String factor2;
	@JsonProperty("imei")
	private String imei;
	@JsonProperty("source")
	private String source = "API";
	@JsonProperty("vc")
	private String vc;
	@JsonProperty("appkey")
	private String appkey;
	@JsonProperty("actid")
	private String actid;
	
	@JsonIgnore
	private Map<String, Object> additionalProperties = new HashMap<String, Object>();

	@JsonProperty("apkversion")
	public String getApkversion() {
		return apkversion;
	}

	@JsonProperty("apkversion")
	public void setApkversion(String apkversion) {
		this.apkversion = apkversion;
	}

	@JsonProperty("uid")
	public String getUid() {
		return uid;
	}

	@JsonProperty("uid")
	public void setUid(String uid) {
		this.uid = uid;
	}

	@JsonProperty("pwd")
	public String getPwd() {
		return pwd;
	}

	@JsonProperty("pwd")
	public void setPwd(String pwd) {
		this.pwd = pwd;
	}

	@JsonProperty("factor2")
	public String getFactor2() {
		return factor2;
	}

	@JsonProperty("factor2")
	public void setFactor2(String factor2) {
		this.factor2 = factor2;
	}

	@JsonProperty("imei")
	public String getImei() {
		return imei;
	}

	@JsonProperty("imei")
	public void setImei(String imei) {
		this.imei = imei;
	}

	@JsonProperty("source")
	public String getSource() {
		return source;
	}

	@JsonProperty("source")
	public void setSource(String source) {
		this.source = source;
	}

	@JsonProperty("vc")
	public String getVc() {
		return vc;
	}

	@JsonProperty("vc")
	public void setVc(String vc) {
		this.vc = vc;
	}

	@JsonProperty("appkey")
	public String getAppkey() {
		return appkey;
	}

	@JsonProperty("appkey")
	public void setAppkey(String appkey) {
		this.appkey = appkey;
	}

	public String getActid() {
		return actid;
	}

	public void setActid(String actid) {
		this.actid = actid;
	}

	@JsonAnyGetter
	public Map<String, Object> getAdditionalProperties() {
		return this.additionalProperties;
	}

	@JsonAnySetter
	public void setAdditionalProperty(String name, Object value) {
		this.additionalProperties.put(name, value);
	}

}