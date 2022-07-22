package com.stock.view;

import java.awt.Color;
import java.awt.Component;
import java.awt.KeyEventDispatcher;
import java.awt.event.KeyEvent;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;

import com.stock.client.FinvasiaAPI;
import com.stock.client.StockAPI;
import com.stock.client.StockEnum;
import com.stock.model.OrderRequest;
import com.stock.model.TradeDataEnum;

import lombok.SneakyThrows;

public class CustomKeyEventDispatcher extends AbstractOrderStatusUpdate implements KeyEventDispatcher {

	ScalpUI scalpUI;
	FinvasiaAPI finVasiaAPI;
	ExecutorService executorService = Executors.newFixedThreadPool(10);
	DefaultTableModel tradeTableModel;
	DateTimeFormatter DATE__FORMATTER = DateTimeFormatter.ofPattern("MMM-yyyy");
	
	@SneakyThrows
	CustomKeyEventDispatcher(ScalpUI scalpUI, StockAPI stockAPI) {
		super(scalpUI);
		this.scalpUI = scalpUI;
		this.finVasiaAPI = (FinvasiaAPI)stockAPI;
		finVasiaAPI.executorService = this.executorService;
		
		tradeTableModel = scalpUI.tradeTableModel;
		tableColorRenderer();
		initalizeMarketDepth();
		finVasiaAPI.updateMarketData();
		startPLCalculation();
	}
	
	public ExecutorService getExecutorService() {
		return executorService;
	}
	
	private void startPLCalculation() {
		executorService.submit(() -> {
			while(true) {
				Thread.sleep(200);
				Float pl = 0.0f;
				for(int row=0;row<scalpUI.tradeTableModel.getRowCount();row++) {
					pl = pl + Float.parseFloat(scalpUI.tradeTableModel.getValueAt(row, TradeDataEnum.PL.getColumnIndex()).toString());
				}
				scalpUI.totalProfit.setText(
						String.valueOf(Float.parseFloat(scalpUI.bookedPLAmount.getText()) + pl));
			}
		});
	}
	
	@SneakyThrows
	private void initalizeMarketDepth() {
		Thread.sleep(3000);
		executorService.submit(() -> {
			while(true) {
				Thread.sleep(700);
				finVasiaAPI.customServerWebSocketHandler.sendPeriodicMessages();
				
			}
		});
	}
	private void tableColorRenderer() {
		scalpUI.tradeTable.setDefaultRenderer(Object.class, new DefaultTableCellRenderer(){
            @Override
            public Component getTableCellRendererComponent(JTable table,
                Object value, boolean isSelected, boolean hasFocus, int row, int col) {

                super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, col);
                String status = table.getModel().getValueAt(row, TradeDataEnum.STATUS.getColumnIndex()).toString();
                Float plValue = Float.parseFloat(table.getModel().getValueAt(row, TradeDataEnum.PL.getColumnIndex()).toString());

                if (StockEnum.PENDING.getDesc().equals(status) || StockEnum.OK.getDesc().equals(status)) {
                    if(plValue >= 0) {
                        setBackground(new Color(204,255,204));
                        setForeground(Color.BLACK);
                    } else {
                        setBackground(new Color(255,153,153));
                        setForeground(Color.BLACK);
                    }
                } else {
                    setBackground(table.getBackground());
                    setForeground(table.getForeground());
                }
                return this;
            }
        });
	}
	public boolean dispatchKeyEvent(KeyEvent e) {
		if (scalpUI.setToTradeClicked &&  e.getID() != KeyEvent.VK_ENTER && e.getID() == KeyEvent.KEY_PRESSED) {
			System.out.println(e.getKeyChar());

			if (scalpUI.radioIndexOption.isSelected() || scalpUI.radioIndex.isSelected()) {
				switch (e.getKeyCode()) {
				case 66:
					// BUY
					scalpUI.currentOrderToken = scalpUI.selectedBuySymbolToken;
					scalpUI.currentOrderSymbol.setText(scalpUI.selectedBuySymbol);
					//finVasiaAPI.customServerWebSocketHandler.sendPeriodicMessages();
					
					finVasiaAPI.createOrder(new OrderRequest(scalpUI.selectedBuySymbol,
							scalpUI.lotSize,StockEnum.BUY.getDesc()));
					//updatePLSummaryPanel();
					break;
				case 83:
					// SELL
					
					scalpUI.currentOrderToken = scalpUI.selectedSellSymbolToken;
					scalpUI.currentOrderSymbol.setText(scalpUI.selectedSellSymbol);
					//finVasiaAPI.customServerWebSocketHandler.sendPeriodicMessages();
					
					finVasiaAPI.createOrder(new OrderRequest(scalpUI.selectedSellSymbol, 
							scalpUI.lotSize ,StockEnum.SELL.getDesc()));
					//updatePLSummaryPanel();
					break;
				case 67:
					// CLOSE
					if(StockEnum.BUY.getDesc().equalsIgnoreCase(
							tradeTableModel.getValueAt(tradeTableModel.getRowCount()-1, TradeDataEnum.TRANS.getColumnIndex()).toString())) {
						closedOrderStatusUpdate(finVasiaAPI.createOrder(new OrderRequest(
								tradeTableModel.getValueAt(tradeTableModel.getRowCount()-1, TradeDataEnum.SYMBOL.getColumnIndex()).toString(),
								//scalpUI.selectedSellSymbol, 
								// scalpUI.lotSize, 
								tradeTableModel.getValueAt(tradeTableModel.getRowCount()-1, TradeDataEnum.QTY.getColumnIndex()).toString(),
								StockEnum.SELL.getDesc()
								)));
					} else if(StockEnum.SELL.getDesc().equalsIgnoreCase(
							tradeTableModel.getValueAt(tradeTableModel.getRowCount()-1, TradeDataEnum.TRANS.getColumnIndex()).toString())) {
						closedOrderStatusUpdate(finVasiaAPI.createOrder(new OrderRequest(
//								scalpUI.selectedSellSymbol, 
//								scalpUI.lotSize, 
								tradeTableModel.getValueAt(tradeTableModel.getRowCount()-1, TradeDataEnum.SYMBOL.getColumnIndex()).toString(),
								tradeTableModel.getValueAt(tradeTableModel.getRowCount()-1, TradeDataEnum.QTY.getColumnIndex()).toString(),
								StockEnum.BUY.getDesc()
								)));
					}
					scalpUI.setToTradeClicked = false;
					break;
				case KeyEvent.VK_UP:
					break;
				case KeyEvent.VK_DOWN:
					break;

				}

			} else {
//				 JOptionPane.showMessageDialog(scalpUI, "Enter a valid Number",
//                         "ERROR", JOptionPane.ERROR_MESSAGE);
			}
		}
		return false;
	}

}