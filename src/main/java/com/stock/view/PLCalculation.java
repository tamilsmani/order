package com.stock.view;

import javax.swing.table.DefaultTableModel;

public class PLCalculation implements Runnable {
	
	ScalpUI scalpUI;
	DefaultTableModel tradeTableModel;
	
	PLCalculation(ScalpUI scalpUI) {
		this.scalpUI = scalpUI;
		tradeTableModel = scalpUI.tradeTableModel;
		
	}
	@Override
	public void run() {
		
		//tradeTableModel.getRowCount()
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
}