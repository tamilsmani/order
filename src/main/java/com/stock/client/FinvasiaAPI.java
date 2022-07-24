package com.stock.client;

import java.io.FileInputStream;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Properties;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;

import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.client.WebSocketConnectionManager;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.stock.model.ComboItem;
import com.stock.model.OrderRequest;
import com.stock.model.OrderResponse;
import com.stock.model.TradeDataEnum;
import com.stock.model.TradeTableModel;
import com.stock.model.finvasia.Auth;
import com.stock.model.finvasia.AuthResponse;
import com.stock.model.finvasia.FinVasiaOrderResponse;
import com.stock.model.finvasia.MarketDepth;
import com.stock.model.finvasia.Order;
import com.stock.model.finvasia.OrderPositionRequest;
import com.stock.model.finvasia.OrderPositionResponse;
import com.stock.model.finvasia.TradeBookResponse;
import com.stock.model.finvasia.WSFeedMaketDepth;
import com.stock.model.finvasia.WSRequest;
import com.stock.model.finvasia.WSResponse;
import com.stock.view.MarketUpdate;
import com.stock.view.ScalpUI;

import lombok.SneakyThrows;

public class FinvasiaAPI extends AbstractSockAPI implements StockAPI {

	private RestTemplate restTemplate = new RestTemplate();
	String ORDER_EXECUTED_FORMAT = "[%s] - Order Executed  [%s] and Order ref [%s]";
	String ORDER_FAILURE_FORMAT = "[%s] - Order Execution Failure [%s] and Order ref [%s]";
	
	Properties prop = new Properties();

	String finvasiaURI;
	String authURL;
	String orderCreateURL;
	String modifyOrderURL;
	String positionBookURL;
	String tradeBookURL;
	
	ScalpUI scalpUI;
	
	ObjectMapper objectMapper;
	
	String authToken = null;
	Auth authRequest = null;
	public CustomServerWebSocketHandler customServerWebSocketHandler = null;
	WebSocketConnectionManager manager;
	String webSockeURL = null;
	String webSocketAuthURL = null;
	public FinvasiaAPI(ScalpUI scalpUI) throws Exception {
		super(scalpUI);
		//FinvasiaWS finvasiaWS = new FinvasiaWS();
		InputStream propertyFile = new FileInputStream(System.getProperty("config.location"));
		prop.load(propertyFile);
		
		finvasiaURI = prop.getProperty("finvasia.uat.url");
		authURL =  finvasiaURI + "/QuickAuth";
		orderCreateURL = finvasiaURI + "/PlaceOrder";
		modifyOrderURL = finvasiaURI + "/ModifyOrder";
		positionBookURL = finvasiaURI + "/PositionBook";
		tradeBookURL = finvasiaURI + "/TradeBook";
		webSockeURL = prop.getProperty("finvasia.stock.websocket.url");
		webSocketAuthURL = prop.getProperty("finvasia.stock.websocket.auth.url")+ "/QuickAuth";

		this.scalpUI = scalpUI;
		
		createAuthRequest();
		objectMapper();
		doAuth();
		if(authToken!=null) {
			doWSAuth();
			loadOrderPosition();
		}
		
		initalizeMarketDepth();
		startPLCalculation();
	}
	
	public void doWSAuth() {
		customServerWebSocketHandler = new CustomServerWebSocketHandler();
		manager = new WebSocketConnectionManager(
			   new StandardWebSocketClient(),
			   customServerWebSocketHandler,
			   webSockeURL
		  );
		manager.setAutoStartup(true);
		manager.start();
	}
	
	public ObjectMapper objectMapper() {
		objectMapper = new ObjectMapper();
		//objectMapper.registerModule(new JavaTimeModule());
		objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
		return objectMapper;
	}
	
	private void createAuthRequest() {
		authRequest = new Auth();
		authRequest.setUid(prop.getProperty("finvasia.uat.uid"));
		authRequest.setActid(authRequest.getUid());
		authRequest.setPwd(DigestUtils.sha256Hex(prop.getProperty("finvasia.uat.pwd")));
		authRequest.setFactor2(prop.getProperty("finvasia.uat.factor2"));
		authRequest.setVc(prop.getProperty("finvasia.uat.vc"));
		authRequest.setAppkey(DigestUtils.sha256Hex(prop.getProperty("finvasia.uat.uid") + '|' +prop.getProperty("finvasia.uat.appkey")));
		authRequest.setImei(prop.getProperty("finvasia.uat.imei"));
		
	}
	@Override
	@SneakyThrows
	public void doAuth() {
		String authResponseStr = doAPICall(authURL, objectMapper.writeValueAsString(authRequest));
		if(authResponseStr.contains("Not_Ok")) {
			scalpUI.logMessageListModel.addElement("Authentication Failure ");
		} else {
			AuthResponse authResponse = objectMapper.readValue(authResponseStr,AuthResponse.class);
			authToken =  authResponse.getSusertoken();
			authRequest.setUid(authResponse.getActid());
			scalpUI.clientIdValue.setText(authResponse.getActid());
		}
	}
	
	@SneakyThrows
	@Override
	public OrderResponse createOrder(OrderRequest orderRequest) {
		Order order = new Order();
		order.setUid(authRequest.getUid());
		order.setActid(authRequest.getActid());
		order.setExch(StockEnum.NFO.name());
		order.setTsym(orderRequest.getTradingSymbol());
		order.setQty(orderRequest.getQuantity());
		order.setPrc("0");
		order.setPrd("M");
		order.setTrantype(orderRequest.getTransType());
		order.setPrctyp("MKT");
		order.setRet("DAY");
		order.setOrdersource("API");
		
//		order.setUid(authRequest.getUid());
//		order.setActid(authRequest.getActid());
//		order.setExch(StockEnum.NSE.name());
//		order.setTsym(orderRequest.getTradingSymbol());
//		order.setQty(orderRequest.getQuantity());
//		order.setPrc("0");
//		order.setDscqty("0");
//		order.setPrd("C");
//		order.setTrantype(orderRequest.getTransType());
//		order.setPrctyp("MKT");
//		order.setRet("DAY");
//		order.setOrdersource("MOB");
		
		FinVasiaOrderResponse orderResponse = objectMapper.readValue(
				doAPICall(orderCreateURL, objectMapper.writeValueAsString(order)),FinVasiaOrderResponse.class);
		if(StockEnum.OK.name().equalsIgnoreCase(orderResponse.getStatus())) {
			
			OrderPositionRequest orderPositionRequest = new OrderPositionRequest();
			orderPositionRequest.setActid(authRequest.getActid());
			orderPositionRequest.setUid(authRequest.getUid());
			
			String tradeBookResponseStr = doAPICall(tradeBookURL, objectMapper.writeValueAsString(orderPositionRequest));
			boolean isOrderExecuted = false;
			
			// If no order then we get different "response" if not then we can look for the trade book response []
			if(!tradeBookResponseStr.contains("emsg")) {
				TradeBookResponse[] tradeBookResponses = objectMapper.readValue(tradeBookResponseStr,TradeBookResponse[].class);
				for(TradeBookResponse tradeBookResponse : tradeBookResponses) {
					if(tradeBookResponse.getNorenordno().equalsIgnoreCase(orderResponse.getNorenordno())) {
						isOrderExecuted = true;
						break;
					}
				}
			}
			
			if(isOrderExecuted) {
				scalpUI.logMessageListModel.addElement(String.format(ORDER_EXECUTED_FORMAT, orderResponse.getRequestTime(), order.getTsym(),
						orderResponse.getNorenordno()));
			} else {
				scalpUI.logMessageListModel.addElement(String.format(ORDER_FAILURE_FORMAT, orderResponse.getRequestTime(), order.getTsym(),
						orderResponse.getNorenordno()));
			}
			
			loadOrderPosition();
			updateMarketData();
		}
		return new OrderResponse(orderRequest.getTradingSymbol(), orderRequest.getTransType(), 50, 50.55f, 50.55f, 0, 0, StockEnum.PENDING.getDesc(), "O-1");
	}

	@Override
	@SneakyThrows
	public void loadOrderPosition() {
		OrderPositionRequest orderPositionRequest = new OrderPositionRequest();
		orderPositionRequest.setUid(authRequest.getUid());
		orderPositionRequest.setActid(authRequest.getActid());
		
		Float bookPL = 0f;
		Integer openPosition = 0;
		Integer closedPosition = 0;
		
		// // Mock
		 Path fileName
         = Path.of("D:\\Javaworksapce\\scalp\\src\\test\\resources\\finvasia\\order-position-response.json");
		 String orderPositionResponseStr = Files.readString(fileName);
		 
		//String orderPositionResponseStr = doAPICall(positionBookURL, objectMapper.writeValueAsString(orderPositionRequest));
		if(!orderPositionResponseStr.contains("emsg")) {
			OrderPositionResponse[] orderPositionResponses = objectMapper.readValue(orderPositionResponseStr, OrderPositionResponse[].class);
			
			int rowCount = scalpUI.tradeTableModel.getRowCount();
			for(int i=0;i<rowCount;i++) {
				scalpUI.tradeTableModel.removeRow(0);
			}
			for( OrderPositionResponse  orderPositionResponse : orderPositionResponses) {
				TradeTableModel tradeTableModel = new TradeTableModel();
				
				tradeTableModel.setTradingSymbol(orderPositionResponse.getTsym());
				// Closed order PL
				if(Integer.parseInt(orderPositionResponse.getNetqty()) ==0) {
					bookPL += Float.parseFloat(orderPositionResponse.getRpnl());
					closedPosition++;
					
				//if(orderPositionResponse.getNetqty().equals("0")) {
	//				tradeTableModel.setTransType("-");
	//				tradeTableModel.setQuantity(0);
	//				tradeTableModel.setMinusButton(null);
	//				tradeTableModel.setPlusButton(null);
	//				tradeTableModel.setSl(0.0f);
	//				tradeTableModel.setLtp(0.0f);
	//				tradeTableModel.setPl(Float.parseFloat( orderPositionResponse.getRpnl()));
	//				tradeTableModel.setExitButton(null);
	//				tradeTableModel.setStatus(StockEnum.OK.getDesc());
				} else {
					// Open orders
					openPosition++;
					int buyQuantity = Integer.parseInt(orderPositionResponse.getDaybuyqty());
					int sellQuantity = Integer.parseInt(orderPositionResponse.getDaysellqty());
					int diffQuantity = buyQuantity - sellQuantity;
					if(diffQuantity >0) {
						tradeTableModel.setTransType(StockEnum.BUY.getDesc());
						tradeTableModel.setQuantity(diffQuantity);
						tradeTableModel.setAvg(Float.parseFloat(orderPositionResponse.getDaybuyavgprc()));
						tradeTableModel.setSl(Float.parseFloat(orderPositionResponse.getDaybuyavgprc()) - 
								Float.parseFloat(scalpUI.stopLossTxt.getText()));
						tradeTableModel.setLtp(tradeTableModel.getAvg());
						tradeTableModel.setPl(tradeTableModel.getAvg()- tradeTableModel.getLtp());
	
					} else {
						tradeTableModel.setTransType(StockEnum.SELL.getDesc());
						tradeTableModel.setQuantity(sellQuantity-buyQuantity);
						tradeTableModel.setAvg(Float.parseFloat(orderPositionResponse.getDaysellavgprc()));
						tradeTableModel.setSl(Float.parseFloat(orderPositionResponse.getDaysellavgprc()) + 
								Float.parseFloat(scalpUI.stopLossTxt.getText()));
						tradeTableModel.setLtp(tradeTableModel.getAvg());
						tradeTableModel.setPl(tradeTableModel.getAvg()+ tradeTableModel.getLtp());
	
					}
					/*
					tradeTableModel.setMinusButton(new TableButtonRenderer("-", this));
					tradeTableModel.setPlusButton(new TableButtonRenderer("+", this));
					tradeTableModel.setExitButton(new TableOrderExitButtonRenderer("Exit", this));
					*/
					tradeTableModel.setStatus(StockEnum.PENDING.getDesc());
				
					scalpUI.tradeTableModel.addRow(new Object[] {
							tradeTableModel.getTradingSymbol(),
							tradeTableModel.getTransType(),
							tradeTableModel.getQuantity(),
							tradeTableModel.getAvg(),
							tradeTableModel.getMinusButton(),
							tradeTableModel.getSl(),
							tradeTableModel.getPlusButton(),
							tradeTableModel.getLtp(),
							tradeTableModel.getPl(),
							tradeTableModel.getExitButton(),
							tradeTableModel.getStatus()
					});
				}
			}
			scalpUI.tradeTableModel.fireTableDataChanged();
			// PL Summary
			scalpUI.bookedPLAmount.setText(bookPL.toString());
			scalpUI.openPositionValue.setText(openPosition.toString());
			scalpUI.closedPositionValue.setText(closedPosition.toString());
			
			scalpUI.currentOrderSymbol.setText(scalpUI.tradeTableModel.getValueAt(0, TradeDataEnum.SYMBOL.getColumnIndex()).toString());
		} else {
			scalpUI.logMessageListModel.addElement("No open order ");
		}
	}
	
	public void updateMarketData() {
		for(int row=0;row<scalpUI.tradeTableModel.getRowCount();row++) {
			if(StockEnum.PENDING.getDesc().equalsIgnoreCase(
					scalpUI.tradeTableModel.getValueAt(row, TradeDataEnum.STATUS.getColumnIndex()).toString())) {
				executorService.submit(new MarketUpdate(row,scalpUI, this));
			}
		}
	}
	
	private String doAPICall(String url, String payload) {
		ResponseEntity<String> response = null;
		try {
			if(!payload.contains("jData"))
				payload="jData="+payload;
			response = restTemplate.postForEntity(url, payload+"&jKey="+authToken, String.class);
		} catch(HttpClientErrorException.Unauthorized ex) {
			doAuth();
			doAPICall(url, payload);
		} catch(HttpClientErrorException.BadRequest bd) {
			scalpUI.logMessageListModel.addElement("Finvasia API Error: " + bd.getMessage());
			return bd.getResponseBodyAsString();
		} catch(Exception ex) {
			scalpUI.logMessageListModel.addElement("Finvasia API Error: " + ex.getMessage());
			return null;
		}
		return response.getBody();
	}

	public class CustomServerWebSocketHandler extends TextWebSocketHandler  {

	    private final Set<WebSocketSession> sessions = new CopyOnWriteArraySet<>();
	    String WS_CONNECTION_STATUS_FORMAT = "Connected to Live feed..";
	    String MARKET_FEED_SYMBOL_FORMAT = "NFO|%s";
	    String MARKET_FEED_FORMAT = "%s#%s#%s";

		@Override
	    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
	        sessions.add(session);

	        Auth wsAuthRequest = new Auth();
	        wsAuthRequest.setUid(prop.getProperty("finvasia.stock.websocket.uid"));
	        wsAuthRequest.setActid(wsAuthRequest.getUid());
	        wsAuthRequest.setPwd(DigestUtils.sha256Hex(prop.getProperty("finvasia.stock.websocket.pwd")));
	        wsAuthRequest.setFactor2(prop.getProperty("finvasia.stock.websocket.factor2"));
	        wsAuthRequest.setVc(prop.getProperty("finvasia.stock.websocket.vc"));
	        wsAuthRequest.setAppkey(DigestUtils.sha256Hex(prop.getProperty("finvasia.stock.websocket.uid") 
	        		+ '|' +prop.getProperty("finvasia.stock.websocket.appkey")));
	        wsAuthRequest.setImei(prop.getProperty("finvasia.stock.websocket.imei"));
			
	        String wsAuthResponseStr = restTemplate.postForEntity(webSocketAuthURL, 
	        		"jData="+objectMapper.writeValueAsString(wsAuthRequest),
	        		String.class).getBody();
	        
    		if(StringUtils.hasText(wsAuthResponseStr)) {
				AuthResponse authResponse = objectMapper.readValue(wsAuthResponseStr,AuthResponse.class);
				
				WSRequest wsRequest = new WSRequest();
		        wsRequest.setActid(wsAuthRequest.getActid());
		        wsRequest.setUid(wsAuthRequest.getUid());
		        wsRequest.setSusertoken(authResponse.getSusertoken());
		        TextMessage message = new TextMessage(objectMapper.writeValueAsString(wsRequest));
		        session.sendMessage(message);
			} else {
				scalpUI.logMessageListModel.addElement("Live Market Feed not connected ");
			}
	    }

	    @Override
	    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) {
	        sessions.remove(session);
	        manager.stop();
    		scalpUI.logMessageListModel.addElement("Live Feed connection closed");
	        manager.start();
	    }

	//    @Scheduled(fixedRate = 10000)
	    @SneakyThrows
	    public void sendPeriodicMessages()  {
	    	//if(scalpUI.currentOrderToken !=null) {
		        for (WebSocketSession session : sessions) {
		            if (session.isOpen()) {
		            	WSFeedMaketDepth wsFeedMaketDepth = new  WSFeedMaketDepth("d", 
		            			String.format(MARKET_FEED_FORMAT, 
		            			String.format(MARKET_FEED_SYMBOL_FORMAT, ((ComboItem)scalpUI.indexOptionPECombo.getSelectedItem()).getToken()),
		            			String.format(MARKET_FEED_SYMBOL_FORMAT, ((ComboItem)scalpUI.indexOptionCECombo.getSelectedItem()).getToken()),
		            			// RELIANCE-EQ - NSE|2885
		            			//"NSE|2885"));
		            			String.format(MARKET_FEED_SYMBOL_FORMAT, ((ComboItem)scalpUI.indexCombo.getSelectedItem()).getToken())));
		                session.sendMessage(new TextMessage(objectMapper.writeValueAsString(wsFeedMaketDepth)));
		            }
		        }
	    	//}
	    }

	    @Override
	    public void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
	    	//System.out.println("marketDepth.getTk()");
	    	WSResponse wsresponse = objectMapper.readValue( message.getPayload(), WSResponse.class);
	    	if(StockEnum.OK.desc.equalsIgnoreCase(wsresponse.getS())) {
	    		scalpUI.logMessageListModel.addElement(WS_CONNECTION_STATUS_FORMAT);
	    	} else {
	    		
	    		MarketDepth marketDepth = objectMapper.readValue( message.getPayload(), MarketDepth.class);
	    		//System.out.println(marketDepth.getTk());
	    		//for(MarketDepth marketDepth : marketDept) {
	    			if(marketDepth.getTk().equalsIgnoreCase(((ComboItem)scalpUI.indexOptionPECombo.getSelectedItem()).getToken())) {
	    				scalpUI.optionPEBid.setText(marketDepth.getBp1());
	    				scalpUI.optionPEAsk.setText(marketDepth.getSp1());
	    			} else if(marketDepth.getTk().equalsIgnoreCase(((ComboItem)scalpUI.indexOptionCECombo.getSelectedItem()).getToken())) {
	    				scalpUI.optionCEBid.setText(marketDepth.getBp1());
	    				scalpUI.optionCEAsk.setText(marketDepth.getSp1());
	    			} else if(marketDepth.getTk().equalsIgnoreCase(((ComboItem)scalpUI.indexCombo.getSelectedItem()).getToken())) {
	    				scalpUI.indexBid.setText(marketDepth.getBp1());
	    				scalpUI.indexAsk.setText(marketDepth.getSp1());
	    			}
	    		//}
//	    		scalpUI.currentOrderBidPrice.setText(marketDept.getBp1());
//	    		scalpUI.currentOrderOfferPrice.setText(marketDept.getSp1());
	    	}
	       // String response = String.format("response from server to '%s'", HtmlUtils.htmlEscape(request));
	      //  logger.info("Server sends: {}", response);
	    	//Thread.sleep(100);
	      //  sendPeriodicMessages();
	       // session.sendMessage(new TextMessage("{\"t\":\"d\",\"k\":\"NSE|22#BSE|508123\"}"));
	    }

	    @Override
	    public void handleTransportError(WebSocketSession session, Throwable exception) {
	    	System.out.println(exception);
	    }

	}
}
