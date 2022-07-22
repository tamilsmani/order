package com.stock.model;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class FutureOptionMaster {
	
	String exchange;
	String token;
	int lotSize;
	String Symbol;
	String tradingSymbol;
	String expiry;
	String instrument;
	String optionType;
	String strikePrice;
	float tickSize;
}