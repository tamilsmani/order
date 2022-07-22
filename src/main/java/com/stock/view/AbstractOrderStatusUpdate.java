package com.stock.view;

import java.time.LocalDateTime;

import com.stock.client.StockEnum;
import com.stock.model.OrderResponse;
import com.stock.model.TradeDataEnum;

public abstract class AbstractOrderStatusUpdate {
	
	final String LOG_MESSAGE_FORMAT = "%s : [%s]-%s";

	ScalpUI scalpUI;
	AbstractOrderStatusUpdate(ScalpUI scalpUI) {
		this.scalpUI = scalpUI;
	}
	public void closedOrderStatusUpdate(OrderResponse orderResponse) {
		//System.out.println("closedOrderStatusUpdate- called");
		//scalpUI.tradeTableModel.setValueAt(StockEnum.OK.getDesc(), scalpUI.tradeTable.getModel().getRowCount()-1, TradeDataEnum.STATUS.getColumnIndex());
		//logMessage(orderResponse, "closedOrder = " +orderResponse.getOrderId());
		//updatePLSummaryPanel();
	}
	
	protected void updatePLSummaryPanel() {
		
		int count = scalpUI.tradeTableModel.getRowCount();
		
		Float bookPL = 0f;
		Integer openPosition = 0;
		Integer closedPosition = 0;
		
		for(int row=0;row<count;row++) {
			if(scalpUI.tradeTableModel.getValueAt(row, TradeDataEnum.STATUS.getColumnIndex())
					.equals(StockEnum.OK.getDesc())) {
				bookPL += Float.parseFloat(scalpUI.tradeTableModel.getValueAt(row, TradeDataEnum.PL.getColumnIndex()).toString());
				closedPosition++;
			} else {
				openPosition++;
			}
		}
		
		scalpUI.bookedPLAmount.setText(bookPL.toString());
		scalpUI.openPositionValue.setText(openPosition.toString());
		scalpUI.closedPositionValue.setText(closedPosition.toString());

	}
	
	
	private void logMessage(OrderResponse orderResponse, String message) {
		scalpUI.logMessageListModel.addElement(String.format(LOG_MESSAGE_FORMAT, 
				LocalDateTime.now(),orderResponse.getSymbol(),message ));
	}
}