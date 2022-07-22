package com.stock.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class OrderResponse {
	
	String symbol;
	String transType;
	int quantity;
	float average;
	float LTP;
	float exited;
	float profitAndLoss;
	String status;
	String orderId;
}