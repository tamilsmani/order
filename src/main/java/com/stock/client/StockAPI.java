package com.stock.client;

import com.stock.model.OrderRequest;
import com.stock.model.OrderResponse;

public interface StockAPI {
    
	public void doAuth();
    public OrderResponse createOrder(OrderRequest orderRequest);

   // public OrderResponse modifyOrder(OrderRequest orderRequest);
    //public OrderResponse closeOrder(OrderRequest orderRequest);

   // public OrderResponse squreOffOrder(OrderRequest orderRequest);
    
    public void loadOrderPosition();
}
