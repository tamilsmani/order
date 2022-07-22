package com.stock.model;

import lombok.Getter;

@Getter
public enum NFOMasterEnum {
	
	FUTSTK("FUTSTK", -1),
	FUTIDX("FUTIDX", -1),
	OPTIDX("OPTIDX", -1),
	PE("PE", -1),
	CE("CE", -1),
	NIFTY("NIFTY", -1),
	BANKNIFTY("BANKNIFTY", -1),
	FINNIFTY("FINNIFTY", -1),
	MIDCPNIFTY("MIDCPNIFTY", -1),
	INDEX_OPTION("INDEX_OPTION", -1),
	INDEX_FUTURE("INDEX_FUTURE",-1),
	
	EXCHANGE("Exchange",0),
	TOKEN("Token",1),
	LOT_SIZE("LotSize",2),
	SYMBOL("Symbol",3),
	TRADING_SYMBOL("TradingSymbol",4),
	EXPIRY("Expiry",5),
	INSTRUMENT("Instrument",6),
	OPTION_TYPE("OptionType",7),
	STRIKE_PRICE("StrikePrice",8),
	TICKET_SIZE("TickSize",9);
	
	@Getter
	String code;
	@Getter
	int position;
	
	NFOMasterEnum(String code, int position) {
		this.code = code;
		this.position = position;
	}
}