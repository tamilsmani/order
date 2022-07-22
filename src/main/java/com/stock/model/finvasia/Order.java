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
@JsonPropertyOrder({ "uid", "actid", "exch", "tsym", "qty", "prc", "s_prdt_ali", "trantype", "prctyp", "ret", "remarks",
		"ordersource", "prd" })
@Generated("jsonschema2pojo")
public class Order {

	@JsonProperty("uid")
	private String uid;
	@JsonProperty("actid")
	private String actid;
	@JsonProperty("exch")
	private String exch;
	@JsonProperty("tsym")
	private String tsym;
	@JsonProperty("qty")
	private String qty;
	@JsonProperty("dscqty")
	private String dscqty;
	
	@JsonProperty("prc")
	private String prc;
	@JsonProperty("s_prdt_ali")
	private String sPrdtAli;
	@JsonProperty("trantype")
	private String trantype;
	@JsonProperty("prctyp")
	private String prctyp;
	@JsonProperty("ret")
	private String ret;
	@JsonProperty("remarks")
	private String remarks;
	@JsonProperty("ordersource")
	private String ordersource;
	@JsonProperty("prd")
	private String prd;
	@JsonProperty("norenordno")
	private String norenordno;
	
	@JsonIgnore
	private Map<String, Object> additionalProperties = new HashMap<String, Object>();

	@JsonProperty("uid")
	public String getUid() {
		return uid;
	}

	@JsonProperty("uid")
	public void setUid(String uid) {
		this.uid = uid;
	}

	@JsonProperty("actid")
	public String getActid() {
		return actid;
	}

	@JsonProperty("actid")
	public void setActid(String actid) {
		this.actid = actid;
	}

	@JsonProperty("exch")
	public String getExch() {
		return exch;
	}

	@JsonProperty("exch")
	public void setExch(String exch) {
		this.exch = exch;
	}

	@JsonProperty("tsym")
	public String getTsym() {
		return tsym;
	}

	@JsonProperty("tsym")
	public void setTsym(String tsym) {
		this.tsym = tsym;
	}

	@JsonProperty("qty")
	public String getQty() {
		return qty;
	}

	@JsonProperty("qty")
	public void setQty(String qty) {
		this.qty = qty;
	}

	@JsonProperty("prc")
	public String getPrc() {
		return prc;
	}

	@JsonProperty("prc")
	public void setPrc(String prc) {
		this.prc = prc;
	}

	@JsonProperty("s_prdt_ali")
	public String getsPrdtAli() {
		return sPrdtAli;
	}

	@JsonProperty("s_prdt_ali")
	public void setsPrdtAli(String sPrdtAli) {
		this.sPrdtAli = sPrdtAli;
	}

	@JsonProperty("trantype")
	public String getTrantype() {
		return trantype;
	}

	@JsonProperty("trantype")
	public void setTrantype(String trantype) {
		this.trantype = trantype;
	}

	@JsonProperty("prctyp")
	public String getPrctyp() {
		return prctyp;
	}

	@JsonProperty("prctyp")
	public void setPrctyp(String prctyp) {
		this.prctyp = prctyp;
	}

	@JsonProperty("ret")
	public String getRet() {
		return ret;
	}

	@JsonProperty("ret")
	public void setRet(String ret) {
		this.ret = ret;
	}

	@JsonProperty("remarks")
	public String getRemarks() {
		return remarks;
	}

	@JsonProperty("remarks")
	public void setRemarks(String remarks) {
		this.remarks = remarks;
	}

	@JsonProperty("ordersource")
	public String getOrdersource() {
		return ordersource;
	}

	@JsonProperty("ordersource")
	public void setOrdersource(String ordersource) {
		this.ordersource = ordersource;
	}

	@JsonProperty("prd")
	public String getPrd() {
		return prd;
	}

	@JsonProperty("prd")
	public void setPrd(String prd) {
		this.prd = prd;
	}

	public String getNorenordno() {
		return norenordno;
	}

	public void setNorenordno(String norenordno) {
		this.norenordno = norenordno;
	}

	public String getDscqty() {
		return dscqty;
	}

	public void setDscqty(String dscqty) {
		this.dscqty = dscqty;
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