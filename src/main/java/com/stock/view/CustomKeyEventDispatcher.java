package com.stock.view;

import java.awt.Color;
import java.awt.Component;
import java.awt.KeyEventDispatcher;
import java.awt.event.KeyEvent;
import java.time.format.DateTimeFormatter;

import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;

import com.stock.client.StockAPI;
import com.stock.client.StockEnum;
import com.stock.model.NFOMasterEnum;
import com.stock.model.OrderRequest;
import com.stock.model.TradeDataEnum;

import lombok.SneakyThrows;

public class CustomKeyEventDispatcher extends AbstractOrderStatusUpdate implements KeyEventDispatcher {

	ScalpUI scalpUI;
	StockAPI stockAPI;
	
	DefaultTableModel tradeTableModel;
	DateTimeFormatter DATE__FORMATTER = DateTimeFormatter.ofPattern("MMM-yyyy");
	
	@SneakyThrows
	CustomKeyEventDispatcher(ScalpUI scalpUI, StockAPI stockAPI) {
		super(scalpUI);
		this.scalpUI = scalpUI;
		this.stockAPI = stockAPI;
		
		tradeTableModel = scalpUI.tradeTableModel;
		tableColorRenderer();
		
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
		//System.out.println(scalpUI.setToTradeClicked);
		if (scalpUI.setToTradeClicked && e.getID() != KeyEvent.VK_ENTER && e.getID() == KeyEvent.KEY_PRESSED) {

			if (scalpUI.radioIndexOption.isSelected() || scalpUI.radioIndex.isSelected()) {
				switch (e.getKeyCode()) {
				case 66:
					if(!scalpUI.isOrderOpened) {
						// BUY
						scalpUI.currentOrderToken = scalpUI.selectedBuySymbolToken;
						scalpUI.currentOrderSymbol.setText(scalpUI.selectedBuySymbol);
						//finVasiaAPI.customServerWebSocketHandler.sendPeriodicMessages();
						
						stockAPI.createOrder(new OrderRequest(scalpUI.selectedBuySymbol,
								scalpUI.lotSize,StockEnum.BUY.getDesc()));
						//updatePLSummaryPanel();
						scalpUI.isOrderOpened = true;
					} else {
						scalpUI.logMessageListModel.addElement("Already order is opened hence cannnot create order");
					}
					break;
				case 83:
					// SELL
					if(!scalpUI.isOrderOpened) {
						scalpUI.currentOrderToken = scalpUI.selectedSellSymbolToken;
						scalpUI.currentOrderSymbol.setText(scalpUI.selectedSellSymbol);
						//finVasiaAPI.customServerWebSocketHandler.sendPeriodicMessages();
						if(scalpUI.selectedTradeOption.equalsIgnoreCase(NFOMasterEnum.INDEX_OPTION.getCode())) {
							stockAPI.createOrder(new OrderRequest(scalpUI.selectedSellSymbol, 
								scalpUI.lotSize ,StockEnum.BUY.getDesc()));
						} else {
							stockAPI.createOrder(new OrderRequest(scalpUI.selectedSellSymbol, 
									scalpUI.lotSize ,StockEnum.SELL.getDesc()));
						}
						//updatePLSummaryPanel();
						scalpUI.isOrderOpened = true;
					} else {
						scalpUI.logMessageListModel.addElement("Already order is opened hence cannnot create order");
					}
					break;
				case 67:
					// CLOSE
					if(scalpUI.isOrderOpened) {
						if(StockEnum.BUY.getDesc().equalsIgnoreCase(
								tradeTableModel.getValueAt(tradeTableModel.getRowCount()-1, TradeDataEnum.TRANS.getColumnIndex()).toString())) {
							stockAPI.createOrder(new OrderRequest(
									tradeTableModel.getValueAt(tradeTableModel.getRowCount()-1, TradeDataEnum.SYMBOL.getColumnIndex()).toString(),
									//scalpUI.selectedSellSymbol, 
									// scalpUI.lotSize, 
									tradeTableModel.getValueAt(tradeTableModel.getRowCount()-1, TradeDataEnum.QTY.getColumnIndex()).toString(),
									StockEnum.SELL.getDesc()
									));
						} else if(StockEnum.SELL.getDesc().equalsIgnoreCase(
								tradeTableModel.getValueAt(tradeTableModel.getRowCount()-1, TradeDataEnum.TRANS.getColumnIndex()).toString())) {
							stockAPI.createOrder(new OrderRequest(
	//								scalpUI.selectedSellSymbol, 
	//								scalpUI.lotSize, 
									tradeTableModel.getValueAt(tradeTableModel.getRowCount()-1, TradeDataEnum.SYMBOL.getColumnIndex()).toString(),
									tradeTableModel.getValueAt(tradeTableModel.getRowCount()-1, TradeDataEnum.QTY.getColumnIndex()).toString(),
									StockEnum.BUY.getDesc()
									));
	
						}
						scalpUI.logMessageListModel.addElement("Order - " + 
								tradeTableModel.getValueAt(tradeTableModel.getRowCount()-1, TradeDataEnum.SYMBOL.getColumnIndex()).toString()
						+" Closed");
						//scalpUI.setToTradeClicked = false;
						scalpUI.isOrderOpened = false;
					} else {
						scalpUI.logMessageListModel.addElement("No open order exist to close");
					}
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