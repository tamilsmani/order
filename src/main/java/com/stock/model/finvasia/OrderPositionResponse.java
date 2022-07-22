package com.stock.model.finvasia;

import java.util.HashMap;
import java.util.Map;
import javax.annotation.Generated;
import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonPropertyOrder({ "stat", "uid", "actid", "exch", "tsym", "prd", "token", "frzqty", "pp", "ls", "ti", "mult",
		"prcftr", "daybuyqty", "daysellqty", "daybuyamt", "daybuyavgprc", "daysellamt", "daysellavgprc", "cfbuyqty",
		"cfsellqty", "openbuyqty", "opensellqty", "openbuyamt", "openbuyavgprc", "opensellamt", "opensellavgprc",
		"dayavgprc", "netqty", "netavgprc", "upldprc", "netupldprc", "lp", "urmtom", "rpnl", "bep" })
public class OrderPositionResponse {

	@JsonProperty("stat")
	private String stat;
	@JsonProperty("uid")
	private String uid;
	@JsonProperty("actid")
	private String actid;
	@JsonProperty("exch")
	private String exch;
	@JsonProperty("tsym")
	private String tsym;
	@JsonProperty("prd")
	private String prd;
	@JsonProperty("token")
	private String token;
	@JsonProperty("frzqty")
	private String frzqty;
	@JsonProperty("pp")
	private String pp;
	@JsonProperty("ls")
	private String ls;
	@JsonProperty("ti")
	private String ti;
	@JsonProperty("mult")
	private String mult;
	@JsonProperty("prcftr")
	private String prcftr;
	@JsonProperty("daybuyqty")
	private String daybuyqty;
	@JsonProperty("daysellqty")
	private String daysellqty;
	@JsonProperty("daybuyamt")
	private String daybuyamt;
	@JsonProperty("daybuyavgprc")
	private String daybuyavgprc;
	@JsonProperty("daysellamt")
	private String daysellamt;
	@JsonProperty("daysellavgprc")
	private String daysellavgprc;
	@JsonProperty("cfbuyqty")
	private String cfbuyqty;
	@JsonProperty("cfsellqty")
	private String cfsellqty;
	@JsonProperty("openbuyqty")
	private String openbuyqty;
	@JsonProperty("opensellqty")
	private String opensellqty;
	@JsonProperty("openbuyamt")
	private String openbuyamt;
	@JsonProperty("openbuyavgprc")
	private String openbuyavgprc;
	@JsonProperty("opensellamt")
	private String opensellamt;
	@JsonProperty("opensellavgprc")
	private String opensellavgprc;
	@JsonProperty("dayavgprc")
	private String dayavgprc;
	@JsonProperty("netqty")
	private String netqty;
	@JsonProperty("netavgprc")
	private String netavgprc;
	@JsonProperty("upldprc")
	private String upldprc;
	@JsonProperty("netupldprc")
	private String netupldprc;
	@JsonProperty("lp")
	private String lp;
	@JsonProperty("urmtom")
	private String urmtom;
	@JsonProperty("rpnl")
	private String rpnl;
	@JsonProperty("bep")
	private String bep;
	@JsonIgnore
	private Map<String, Object> additionalProperties = new HashMap<String, Object>();

	@JsonProperty("stat")
	public String getStat() {
		return stat;
	}

	@JsonProperty("stat")
	public void setStat(String stat) {
		this.stat = stat;
	}

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

	@JsonProperty("prd")
	public String getPrd() {
		return prd;
	}

	@JsonProperty("prd")
	public void setPrd(String prd) {
		this.prd = prd;
	}

	@JsonProperty("token")
	public String getToken() {
		return token;
	}

	@JsonProperty("token")
	public void setToken(String token) {
		this.token = token;
	}

	@JsonProperty("frzqty")
	public String getFrzqty() {
		return frzqty;
	}

	@JsonProperty("frzqty")
	public void setFrzqty(String frzqty) {
		this.frzqty = frzqty;
	}

	@JsonProperty("pp")
	public String getPp() {
		return pp;
	}

	@JsonProperty("pp")
	public void setPp(String pp) {
		this.pp = pp;
	}

	@JsonProperty("ls")
	public String getLs() {
		return ls;
	}

	@JsonProperty("ls")
	public void setLs(String ls) {
		this.ls = ls;
	}

	@JsonProperty("ti")
	public String getTi() {
		return ti;
	}

	@JsonProperty("ti")
	public void setTi(String ti) {
		this.ti = ti;
	}

	@JsonProperty("mult")
	public String getMult() {
		return mult;
	}

	@JsonProperty("mult")
	public void setMult(String mult) {
		this.mult = mult;
	}

	@JsonProperty("prcftr")
	public String getPrcftr() {
		return prcftr;
	}

	@JsonProperty("prcftr")
	public void setPrcftr(String prcftr) {
		this.prcftr = prcftr;
	}

	@JsonProperty("daybuyqty")
	public String getDaybuyqty() {
		return daybuyqty;
	}

	@JsonProperty("daybuyqty")
	public void setDaybuyqty(String daybuyqty) {
		this.daybuyqty = daybuyqty;
	}

	@JsonProperty("daysellqty")
	public String getDaysellqty() {
		return daysellqty;
	}

	@JsonProperty("daysellqty")
	public void setDaysellqty(String daysellqty) {
		this.daysellqty = daysellqty;
	}

	@JsonProperty("daybuyamt")
	public String getDaybuyamt() {
		return daybuyamt;
	}

	@JsonProperty("daybuyamt")
	public void setDaybuyamt(String daybuyamt) {
		this.daybuyamt = daybuyamt;
	}

	@JsonProperty("daybuyavgprc")
	public String getDaybuyavgprc() {
		return daybuyavgprc;
	}

	@JsonProperty("daybuyavgprc")
	public void setDaybuyavgprc(String daybuyavgprc) {
		this.daybuyavgprc = daybuyavgprc;
	}

	@JsonProperty("daysellamt")
	public String getDaysellamt() {
		return daysellamt;
	}

	@JsonProperty("daysellamt")
	public void setDaysellamt(String daysellamt) {
		this.daysellamt = daysellamt;
	}

	@JsonProperty("daysellavgprc")
	public String getDaysellavgprc() {
		return daysellavgprc;
	}

	@JsonProperty("daysellavgprc")
	public void setDaysellavgprc(String daysellavgprc) {
		this.daysellavgprc = daysellavgprc;
	}

	@JsonProperty("cfbuyqty")
	public String getCfbuyqty() {
		return cfbuyqty;
	}

	@JsonProperty("cfbuyqty")
	public void setCfbuyqty(String cfbuyqty) {
		this.cfbuyqty = cfbuyqty;
	}

	@JsonProperty("cfsellqty")
	public String getCfsellqty() {
		return cfsellqty;
	}

	@JsonProperty("cfsellqty")
	public void setCfsellqty(String cfsellqty) {
		this.cfsellqty = cfsellqty;
	}

	@JsonProperty("openbuyqty")
	public String getOpenbuyqty() {
		return openbuyqty;
	}

	@JsonProperty("openbuyqty")
	public void setOpenbuyqty(String openbuyqty) {
		this.openbuyqty = openbuyqty;
	}

	@JsonProperty("opensellqty")
	public String getOpensellqty() {
		return opensellqty;
	}

	@JsonProperty("opensellqty")
	public void setOpensellqty(String opensellqty) {
		this.opensellqty = opensellqty;
	}

	@JsonProperty("openbuyamt")
	public String getOpenbuyamt() {
		return openbuyamt;
	}

	@JsonProperty("openbuyamt")
	public void setOpenbuyamt(String openbuyamt) {
		this.openbuyamt = openbuyamt;
	}

	@JsonProperty("openbuyavgprc")
	public String getOpenbuyavgprc() {
		return openbuyavgprc;
	}

	@JsonProperty("openbuyavgprc")
	public void setOpenbuyavgprc(String openbuyavgprc) {
		this.openbuyavgprc = openbuyavgprc;
	}

	@JsonProperty("opensellamt")
	public String getOpensellamt() {
		return opensellamt;
	}

	@JsonProperty("opensellamt")
	public void setOpensellamt(String opensellamt) {
		this.opensellamt = opensellamt;
	}

	@JsonProperty("opensellavgprc")
	public String getOpensellavgprc() {
		return opensellavgprc;
	}

	@JsonProperty("opensellavgprc")
	public void setOpensellavgprc(String opensellavgprc) {
		this.opensellavgprc = opensellavgprc;
	}

	@JsonProperty("dayavgprc")
	public String getDayavgprc() {
		return dayavgprc;
	}

	@JsonProperty("dayavgprc")
	public void setDayavgprc(String dayavgprc) {
		this.dayavgprc = dayavgprc;
	}

	@JsonProperty("netqty")
	public String getNetqty() {
		return netqty;
	}

	@JsonProperty("netqty")
	public void setNetqty(String netqty) {
		this.netqty = netqty;
	}

	@JsonProperty("netavgprc")
	public String getNetavgprc() {
		return netavgprc;
	}

	@JsonProperty("netavgprc")
	public void setNetavgprc(String netavgprc) {
		this.netavgprc = netavgprc;
	}

	@JsonProperty("upldprc")
	public String getUpldprc() {
		return upldprc;
	}

	@JsonProperty("upldprc")
	public void setUpldprc(String upldprc) {
		this.upldprc = upldprc;
	}

	@JsonProperty("netupldprc")
	public String getNetupldprc() {
		return netupldprc;
	}

	@JsonProperty("netupldprc")
	public void setNetupldprc(String netupldprc) {
		this.netupldprc = netupldprc;
	}

	@JsonProperty("lp")
	public String getLp() {
		return lp;
	}

	@JsonProperty("lp")
	public void setLp(String lp) {
		this.lp = lp;
	}

	@JsonProperty("urmtom")
	public String getUrmtom() {
		return urmtom;
	}

	@JsonProperty("urmtom")
	public void setUrmtom(String urmtom) {
		this.urmtom = urmtom;
	}

	@JsonProperty("rpnl")
	public String getRpnl() {
		return rpnl;
	}

	@JsonProperty("rpnl")
	public void setRpnl(String rpnl) {
		this.rpnl = rpnl;
	}

	@JsonProperty("bep")
	public String getBep() {
		return bep;
	}

	@JsonProperty("bep")
	public void setBep(String bep) {
		this.bep = bep;
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