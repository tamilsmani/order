package com.stock.client;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.stock.client.PaperTrade.CustomServerWebSocketHandler;
import com.stock.model.TradeDataEnum;
import com.stock.view.ScalpUI;

import lombok.SneakyThrows;

public abstract class AbstractSockAPI {
	
	public ExecutorService executorService = Executors.newFixedThreadPool(10);
	ScalpUI scalpUI;
	public CustomServerWebSocketHandler customServerWebSocketHandler = null;

	AbstractSockAPI(ScalpUI scalpUI) {
		this.scalpUI = scalpUI;
	}
	
	@SneakyThrows
	protected void initalizeMarketDepth() {
		Thread.sleep(3000);
		executorService.submit(() -> {
			while(true) {
				Thread.sleep(700);
				this.customServerWebSocketHandler.sendPeriodicMessages();
			}
		});
	}
	
//	-	private void startPLCalculation() {
//		-		executorService.submit(() -> {
//		-			while(true) {
//		-				Thread.sleep(200);
//		-				Float pl = 0.0f;
//		-				for(int row=0;row<scalpUI.tradeTableModel.getRowCount();row++) {
//		-					pl = pl + Float.parseFloat(scalpUI.tradeTableModel.getValueAt(row, TradeDataEnum.PL.getColumnIndex()).toString());
//		-				}
//		-				scalpUI.totalProfit.setText(
//		-						String.valueOf(Float.parseFloat(scalpUI.bookedPLAmount.getText()) + pl));
//		-			}
//		-		});
//		+		
//		 	}
	
	protected void startPLCalculation() {
		executorService.submit(() -> {
			while(true) {
				Thread.sleep(200);
				Float pl = 0.0f;
				Float openPL = 0.0f;
				
				for(int row=0;row<scalpUI.tradeTableModel.getRowCount();row++) {
					// Closed order
					if(StockEnum.OK.getDesc().equalsIgnoreCase(
							scalpUI.tradeTableModel.getValueAt(row, TradeDataEnum.STATUS.getColumnIndex()).toString()) ) {
						pl = pl + Float.parseFloat(scalpUI.tradeTableModel.getValueAt(row, TradeDataEnum.PL.getColumnIndex()).toString());
					} else {
						// Open Order
						openPL = openPL + Float.parseFloat(scalpUI.tradeTableModel.getValueAt(row, TradeDataEnum.PL.getColumnIndex()).toString());
					}
				}
				//scalpUI.bookedPLAmount.setText(String.valueOf(pl));
				scalpUI.openPLAmount.setText(String.format("%.2f", openPL));
				scalpUI.bookedPLAmount.setText(String.format("%.2f",pl));
				scalpUI.totalProfit.setText(String.format("%.2f", openPL + pl));
			}
		});
	}
}