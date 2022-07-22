package com.stock.model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class OrderRequest{
	
	public OrderRequest(String tradingSymbol, String quantity, String transType) {
		super();
		this.tradingSymbol = tradingSymbol;
		this.quantity = quantity;
		this.transType = transType;
	}

	String tradingSymbol;
	String quantity;
	String transType;
	
	String orderNumber;
	
}
