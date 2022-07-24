package com.stock.view;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.util.EventObject;

import javax.swing.AbstractCellEditor;
import javax.swing.JButton;
import javax.swing.JTable;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableModel;

import com.stock.client.StockAPI;
import com.stock.client.StockEnum;
import com.stock.model.NFOMasterEnum;
import com.stock.model.OrderRequest;
import com.stock.model.TradeDataEnum;

public class TableOrderExitButtonRenderer extends AbstractCellEditor implements TableCellEditor, TableCellRenderer {
    StockAPI stockAPI;
    String symbol;
    int maxColumnCount;
    ScalpUI scalpUI;
    
    public TableOrderExitButtonRenderer(String symbol, StockAPI stockAPI, ScalpUI scalpUI) {
        this.symbol = symbol;
        this.stockAPI = stockAPI;
        this.scalpUI =  scalpUI;
    }

    @Override
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
        return new JButton(symbol);
    }

    @Override
    public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, final int row, int column) {
        JButton b = new JButton(symbol);
        maxColumnCount = table.getModel().getColumnCount()-1;
        b.addActionListener(new java.awt.event.ActionListener() {
        	TableModel tableModel = table.getModel();
            @Override
            public void actionPerformed(ActionEvent e) {
	               if(!StockEnum.OK.getDesc().equals(tableModel.getValueAt(row, TradeDataEnum.STATUS.getColumnIndex()))) {
	            	   stockAPI.createOrder(new OrderRequest(
	            			   tableModel.getValueAt(row,TradeDataEnum.SYMBOL.getColumnIndex()).toString()	,
	            			   tableModel.getValueAt(row,TradeDataEnum.QTY.getColumnIndex()).toString(),
	            			   StockEnum.BUY.getDesc().equalsIgnoreCase(
	            					   tableModel.getValueAt(row, TradeDataEnum.TRANS.getColumnIndex()).toString()) ?  
	            							   StockEnum.SELL.getDesc() : StockEnum.BUY.getDesc()));
						scalpUI.isOrderOpened = false;
	            	   
	               } else {
	            	   b.removeActionListener(this);
	               }
            }
        });
        return b;
    }

    @Override
    public Object getCellEditorValue() {
        return null;
    }

    @Override
    public boolean isCellEditable(EventObject anEvent) {
        return true;
    }

    @Override
    public boolean shouldSelectCell(EventObject anEvent) {
        return true;
    }
}