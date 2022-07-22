package com.stock.client;

import lombok.Getter;

@Getter
public enum StockEnum {

	BUY("B"), 
	SELL("S"), 
	OK("OK"), 
	NSE("NSE"),
	NFO("NFO"),
	PENDING("Pending"),
	CLOSED ("Closed"),
	NOT_OK("Not_Ok");
	
	@Getter
	String desc;

	StockEnum(String desc) {
		this.desc = desc;
	}
}