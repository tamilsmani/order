package com.stock.view;

import javax.swing.JLabel;

import org.springframework.util.StringUtils;

import com.stock.client.FinvasiaAPI;
import com.stock.client.StockEnum;
import com.stock.model.NFOMasterEnum;
import com.stock.model.OrderRequest;
import com.stock.model.TradeDataEnum;

import lombok.SneakyThrows;

public class MarketUpdate extends AbstractOrderStatusUpdate implements Runnable {
		
	int row;
	//TableModel tradeTableModel;
	String orderType;
	ScalpUI scalpUI;
	FinvasiaAPI finVasiaAPI;
	
	public MarketUpdate(int row, ScalpUI scalpUI, FinvasiaAPI finVasiaAPI) {
		super(scalpUI);
		this.row = row;
		this.scalpUI = scalpUI;
		//this.tradeTableModel = scalpUI.tradeTableModel;
		this.orderType = scalpUI.selectedTradeOption;
		
		this.finVasiaAPI = finVasiaAPI;
		
	}
	public void run()  {
		//System.out.println("started row=" +row);
		try {

			String transactionType = scalpUI.tradeTableModel.getValueAt(row, TradeDataEnum.TRANS.getColumnIndex()).toString();
			Float avgPrice = Float.parseFloat(scalpUI.tradeTableModel.getValueAt(row, TradeDataEnum.AVG.getColumnIndex()).toString());
			Float qty = Float.parseFloat(scalpUI.tradeTableModel.getValueAt(row, TradeDataEnum.QTY.getColumnIndex()).toString());
			// Check order is closed order
			while(!StockEnum.OK.getDesc().equalsIgnoreCase(scalpUI.tradeTableModel.getValueAt(row, TradeDataEnum.STATUS.getColumnIndex()).toString())) {
				//System.out.println("bid-price=" + scalpUI.indexBid.getText());
				//System.out.println("Running row=" +row);
				if(scalpUI.setToTradeClicked) {
					updateCellData(transactionType);
				
					Float ltpValue = Float.parseFloat(scalpUI.tradeTableModel.getValueAt(row, TradeDataEnum.LTP.getColumnIndex()).toString());
					Float slValue =  Float.parseFloat(scalpUI.tradeTableModel.getValueAt(row, TradeDataEnum.SL.getColumnIndex()).toString());
					//System.out.println("row=" +row +" - ltp=" + ltpValue);
					
					// SL use case
					// B -  LTP <= SL  | S - LTP >= SL
					if(false && (StockEnum.BUY.getDesc().equalsIgnoreCase(transactionType) && ltpValue <= slValue ||
						StockEnum.SELL.getDesc().equalsIgnoreCase(transactionType) && ltpValue >= slValue	)) {
						//System.out.println("SL Triggered");
						
						if(StockEnum.BUY.getDesc().equalsIgnoreCase(transactionType)) {
							finVasiaAPI.createOrder(new OrderRequest(scalpUI.selectedSellSymbol, StockEnum.SELL.getDesc(), 
									scalpUI.lotSizeTxt.getText()));
						} else if(StockEnum.SELL.getDesc().equalsIgnoreCase(transactionType)) {
							finVasiaAPI.createOrder(new OrderRequest(scalpUI.selectedSellSymbol, StockEnum.BUY.getDesc(), 
									scalpUI.lotSizeTxt.getText()));
						}
						
						scalpUI.tradeTable.getColumnModel().getColumn(TradeDataEnum.MINUS.getColumnIndex()).setCellRenderer(null);
						scalpUI.tradeTable.getColumnModel().getColumn(TradeDataEnum.PLUS.getColumnIndex()).setCellEditor(null);
						scalpUI.tradeTable.getColumnModel().getColumn(TradeDataEnum.EXIT.getColumnIndex()).setCellEditor(null);
						
						scalpUI.tradeTableModel.setValueAt(null, row, TradeDataEnum.MINUS.getColumnIndex());
						scalpUI.tradeTableModel.setValueAt(null, row, TradeDataEnum.PLUS.getColumnIndex());
						scalpUI.tradeTableModel.setValueAt(null, row, TradeDataEnum.EXIT.getColumnIndex());
	
						break;
					}
					
					//Float avgPrice = Float.parseFloat(model.getValueAt(row, TradeDataEnum.AVG.getColumnIndex()).toString());
					//Float qty = Float.parseFloat(model.getValueAt(row, TradeDataEnum.QTY.getColumnIndex()).toString());
					
					if(StockEnum.BUY.getDesc().equalsIgnoreCase(transactionType)) {
						scalpUI.tradeTableModel.setValueAt(String.valueOf((ltpValue - avgPrice) * qty), row, TradeDataEnum.PL.getColumnIndex());
					} else {
						scalpUI.tradeTableModel.setValueAt(String.valueOf((avgPrice - ltpValue) * qty), row, TradeDataEnum.PL.getColumnIndex());
					}
				}
				//scalpUI.tradeTableModel.setValueAt(ltpValue, row, TradeDataEnum.LTP.getColumnIndex());
				
			}
		} catch(ArrayIndexOutOfBoundsException ex) {
			System.out.println("Ignore " +ex);	
		} catch(Exception ex) {
			scalpUI.logMessageListModel.addElement("Error" + ex.getMessage());
			System.out.println(ex);	
		}
		
		System.out.println("done row=" +row);
	}
	
	@SneakyThrows
	private void updateCellData(String transactionType) {
		if(NFOMasterEnum.INDEX_FUTURE.getCode().equalsIgnoreCase(scalpUI.selectedTradeOption)) {
			if(StockEnum.BUY.getDesc().equalsIgnoreCase(transactionType)) {
				scalpUI.tradeTableModel.setValueAt(getLTPValue(scalpUI.indexBid, row), row, TradeDataEnum.LTP.getColumnIndex());
			} else {
				scalpUI.tradeTableModel.setValueAt(getLTPValue(scalpUI.indexAsk, row), row, TradeDataEnum.LTP.getColumnIndex());
			}
		} else if(NFOMasterEnum.INDEX_OPTION.getCode().equalsIgnoreCase(scalpUI.selectedTradeOption)) {
			if(StockEnum.BUY.getDesc().equalsIgnoreCase(transactionType)) {
				scalpUI.tradeTableModel.setValueAt(getLTPValue(scalpUI.optionCEBid, row), row, TradeDataEnum.LTP.getColumnIndex());
			} else {
				scalpUI.tradeTableModel.setValueAt(getLTPValue(scalpUI.optionPEBid, row), row, TradeDataEnum.LTP.getColumnIndex());
			}
		}
		scalpUI.tradeTableModel.fireTableDataChanged();
		Thread.sleep(500);
		//scalpUI.tradeTableModel.fireTableCellUpdated(row, TradeDataEnum.LTP.getColumnIndex());
	}
	public String getLTPValue(JLabel inputLabel, int row) {
		String currentPrice;
		do {
			currentPrice = inputLabel.getText();
			System.out.print("-");

		} while(!StringUtils.hasLength(currentPrice));

		return currentPrice;
	}
}