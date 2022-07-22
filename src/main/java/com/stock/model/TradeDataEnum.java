package com.stock.model;

import lombok.Getter;

@Getter
public enum TradeDataEnum {
	SYMBOL("Symbol",0),
	TRANS("Trans",1),
	QTY("Qty",2),
	AVG("Avg",3),
	MINUS("-",4),
	SL("SL",5),
	PLUS("+",6),
	LTP("LTP",7),
	PL("P/L",8),
	EXIT("Exit",9),
	STATUS("Status",10),
	ORDERID("OrderId",11);

	@Getter
	String desc;
	@Getter
	int columnIndex;
	
	TradeDataEnum(String desc, int columnIndex) {
		this.desc = desc;
		this.columnIndex = columnIndex;
	}
}