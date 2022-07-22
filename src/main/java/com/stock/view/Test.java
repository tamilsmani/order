package com.stock.view;

import java.util.concurrent.CountDownLatch;

import org.springframework.web.socket.client.WebSocketConnectionManager;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;

public class Test {
	  final static CountDownLatch messageLatch = new CountDownLatch(1);

	    public static void main(String[] args) {
	        try {
	        	// wss://demo.piesocket.com/v3/channel_1?api_key=VCXCEuvhGcBDP7XhiJJUDvR1e1D3eiVjgZ9VRiaV&notify_self
//	            WebSocketContainer container = ContainerProvider.getWebSocketContainer();
//	            String uri = "wss://shoonyatrade.finvasia.com/NorenWSTP/";
//	            System.out.println("Connecting to " + uri);
//	            container.connectToServer(MyClientEndpoint.class, URI.create(uri));
//	            messageLatch.await(100, TimeUnit.SECONDS);
//	        	
	        	
	        	
	        	   WebSocketConnectionManager manager = new WebSocketConnectionManager(
	        			   new StandardWebSocketClient(),
	                       //new ServerWebSocketHandler(),4
	        			   null,
	                       "wss://shoonyatrade.finvasia.com/NorenWSTP/"
	               );
	               manager.setAutoStartup(true);
	               manager.start();
//	               while(true) {
//	            	   
//	               }
	              // return manager;
	               
	        } catch (Exception ex) {
	        	System.out.println(ex);
	            //Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
	        }
	    }
}