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
import com.stock.model.TradeDataEnum;

public class TableButtonRenderer extends AbstractCellEditor implements TableCellEditor, TableCellRenderer {
    String symbol;
    float INCREMENT_VALUE = 0.5f;
    StockAPI stockAPI;
    int maxColumnCount = 0;
    ScalpUI scalpUI;
    
    String value = null;
    public TableButtonRenderer(String symbol, StockAPI stockAPI) {
        this.symbol = symbol;
        this.stockAPI = stockAPI;
    }

    @Override
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
    	JButton button = new JButton(symbol);
        return button;
    }

    @Override
    public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, final int row, int column) {

	        maxColumnCount = table.getModel().getColumnCount()-1;
	        JButton b = new JButton(symbol);
	        b.addActionListener(new java.awt.event.ActionListener() {
	
	            @Override
	            public void actionPerformed(ActionEvent e) {
	               JButton button =  ((JButton) e.getSource());
	               TableModel tableModel = table.getModel();
	               Float slValue = Float.parseFloat(tableModel.getValueAt(row, 5).toString());
	               if(!StockEnum.OK.getDesc().equals(table.getModel().getValueAt(row, TradeDataEnum.STATUS.getColumnIndex()))) {
		                if("-".equals(button.getText()) &&  slValue >= INCREMENT_VALUE) {
		                    tableModel.setValueAt(Float.parseFloat(tableModel.getValueAt(row, column+1).toString()) - INCREMENT_VALUE, row, column+1); 
		                } else if("+".equals(button.getText())) {
		                    tableModel.setValueAt(Float.parseFloat(tableModel.getValueAt(row, column-1).toString()) + INCREMENT_VALUE, row, column-1); 
		                }

		               // stockAPI.modifyOrder(new OrderRequest(null,null,0,tableModel.getValueAt(row, maxColumnCount).toString()));
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