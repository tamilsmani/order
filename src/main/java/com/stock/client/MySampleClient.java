package com.stock.client;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.WebSocket;
import java.net.http.WebSocket.Listener;
import java.util.concurrent.ExecutionException;

import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketHttpHeaders;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import lombok.Getter;
import lombok.SneakyThrows;
import lombok.val;

public class MySampleClient extends TextWebSocketHandler {

    @Getter
    private WebSocketSession clientSession;

    public MySampleClient () throws ExecutionException, InterruptedException {
        var webSocketClient = new StandardWebSocketClient();
        this.clientSession = webSocketClient.doHandshake(this, new WebSocketHttpHeaders(), URI.create("wss://kwtest.finvasia.in/NorenWSTP/")).get();
    }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) {
        System.out.println(message.getPayload());
    }

    @SneakyThrows
    public static void main(String[] args)  {
    	  WebSocket ws = HttpClient
                  .newHttpClient()
                  .newWebSocketBuilder()
                  .buildAsync(URI.create("wss://marketdata.tradermade.com/feedadv"), new Listener() { 
                	  
                  })
                  .join();
          while(true){
        	  
        	  
          }
          
//        val sampleClient =  new MySampleClient();
//        sampleClient.getClientSession().sendMessage(new TextMessage("Hello!"));
//        Thread.sleep(2000);
    }
}