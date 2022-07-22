package com.stock.model;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class StrikeModel {
	
	String exchange;
	String token;
	String lotSize;
	String symbol;
	String tradingSymbol;
	String expiry;
	String instrument;
	String optionType;
	String strikePrice;
	float tickSize;
	
}