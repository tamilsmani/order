package com.stock.model;

import com.stock.view.TableButtonRenderer;
import com.stock.view.TableOrderExitButtonRenderer;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class TradeTableModel {
	String tradingSymbol;
	String transType;
	Integer quantity;
	Float avg;
	TableButtonRenderer minusButton;
	Float sl;
	TableButtonRenderer plusButton;
	Float ltp;
	Float pl;
	TableOrderExitButtonRenderer exitButton;
	String status;

}