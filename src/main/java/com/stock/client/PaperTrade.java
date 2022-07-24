package com.stock.client;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Properties;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;

import javax.swing.JLabel;

import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
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
import com.stock.model.finvasia.MarketDepth;
import com.stock.model.finvasia.Order;
import com.stock.model.finvasia.WSFeedMaketDepth;
import com.stock.model.finvasia.WSRequest;
import com.stock.model.finvasia.WSResponse;
import com.stock.view.MarketUpdate;
import com.stock.view.ScalpUI;

import lombok.SneakyThrows;

public class PaperTrade extends AbstractSockAPI implements StockAPI {

	private RestTemplate restTemplate = new RestTemplate();
	String ORDER_EXECUTED_FORMAT = "Paper Trade [%s] order Executed";
	
	Properties prop = new Properties();

	String finvasiaURI;
	String authURL;
	String orderCreateURL;
	String modifyOrderURL;
	String positionBookURL;
	String tradeBookURL;
	
	ScalpUI scalpUI;
	
	ObjectMapper objectMapper;
	
	String authToken = "DUMMY_AUTH_TOKEN";
	Auth authRequest = new Auth();
	WebSocketConnectionManager manager;
	String webSockeURL = null;
	String webSocketAuthURL = null;
	public PaperTrade(ScalpUI scalpUI) throws Exception {
		super(scalpUI);
		//FinvasiaWS finvasiaWS = new FinvasiaWS();
		InputStream propertyFile = new FileInputStream(new File(System.getProperty("config.location")));
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
		objectMapper();
		doWSAuth();

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
	
	@Override
	public void doAuth() {
	}
	
	@SneakyThrows
	@Override
	public OrderResponse createOrder(OrderRequest orderRequest) {
		doAPICall("","");
		
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
		loadPaperTradeOrderPosition(order);
		updateMarketData();
		updatePLSummary();
		return new OrderResponse(orderRequest.getTradingSymbol(), orderRequest.getTransType(), 50, 50.55f, 50.55f, 0, 0, StockEnum.PENDING.getDesc(), "O-1");
	}

	@Override
	public void loadOrderPosition() {
	}
	
	@SneakyThrows
	public void loadPaperTradeOrderPosition(Order order) {
		int row = scalpUI.tradeTableModel.getRowCount();
		if(row == 0) {
			createOrderInTableModel(order);
		} else {
			row = row -1;
			// Close Last Open Order
			if(StockEnum.PENDING.getDesc().equalsIgnoreCase(
					scalpUI.tradeTable.getValueAt(row, TradeDataEnum.STATUS.getColumnIndex()).toString())) {
				scalpUI.tradeTable.setValueAt(StockEnum.OK.getDesc(), row, TradeDataEnum.STATUS.getColumnIndex());
				
				scalpUI.tradeTable.setValueAt(null, row, TradeDataEnum.MINUS.getColumnIndex());
				scalpUI.tradeTable.setValueAt(null, row, TradeDataEnum.PLUS.getColumnIndex());
				scalpUI.tradeTable.setValueAt(null, row, TradeDataEnum.EXIT.getColumnIndex());
	
			} else {
				createOrderInTableModel(order);
			}
		}
		scalpUI.tradeTableModel.fireTableDataChanged();
	}
	private void createOrderInTableModel(Order order) {
		TradeTableModel tradeTableModel = new TradeTableModel();
		tradeTableModel.setTradingSymbol(order.getTsym());
		tradeTableModel.setTransType(order.getTrantype());
		tradeTableModel.setQuantity(Integer.parseInt(order.getQty()));
		
		if(StockEnum.BUY.getDesc().equalsIgnoreCase(order.getTrantype()) && 
				order.getTsym().equalsIgnoreCase(((ComboItem)scalpUI.indexOptionPECombo.getSelectedItem()).getKey())) {
			tradeTableModel.setAvg(Float.parseFloat(getLTPValue(scalpUI.optionPEAsk)));
			tradeTableModel.setSl(tradeTableModel.getAvg() -
					Float.parseFloat(scalpUI.stopLossTxt.getText()));

//			if(StockEnum.BUY.getDesc().equalsIgnoreCase(order.getTrantype()))
//				tradeTableModel.setAvg(Float.parseFloat(getLTPValue(scalpUI.optionPEAsk)));
//			else 
//				tradeTableModel.setAvg(Float.parseFloat(getLTPValue(scalpUI.optionPEBid)));
			
		} else if(StockEnum.BUY.getDesc().equalsIgnoreCase(order.getTrantype()) && 
				order.getTsym().equalsIgnoreCase(((ComboItem)scalpUI.indexOptionCECombo.getSelectedItem()).getKey())) {
			tradeTableModel.setAvg(Float.parseFloat(getLTPValue(scalpUI.optionCEAsk)));
			tradeTableModel.setSl(tradeTableModel.getAvg() -
					Float.parseFloat(scalpUI.stopLossTxt.getText()));
//			if(StockEnum.BUY.getDesc().equalsIgnoreCase(order.getTrantype()))
//				tradeTableModel.setAvg(Float.parseFloat(getLTPValue(scalpUI.optionCEAsk)));
//			else 
//				tradeTableModel.setAvg(Float.parseFloat(getLTPValue(scalpUI.optionCEBid)));
			
		} else if(order.getTsym().equalsIgnoreCase(((ComboItem)scalpUI.indexCombo.getSelectedItem()).getKey())) {
			if(StockEnum.BUY.getDesc().equalsIgnoreCase(order.getTrantype())) {
				tradeTableModel.setAvg(Float.parseFloat(getLTPValue(scalpUI.indexAsk)));
				tradeTableModel.setSl(tradeTableModel.getAvg() -
						Float.parseFloat(scalpUI.stopLossTxt.getText()));
			} else {
				tradeTableModel.setAvg(Float.parseFloat(getLTPValue(scalpUI.indexBid)));
				tradeTableModel.setSl(tradeTableModel.getAvg() +
						Float.parseFloat(scalpUI.stopLossTxt.getText()));
			}
			
		}
		
		tradeTableModel.setLtp(tradeTableModel.getAvg());
		tradeTableModel.setPl(tradeTableModel.getAvg()- tradeTableModel.getLtp());
		tradeTableModel.setStatus(StockEnum.PENDING.getDesc());
		
//		tradeTableModel.setMinusButton(new TableButtonRenderer("-", this, scalpUI.customKeyEventDispatcher));
//		tradeTableModel.setPlusButton(new TableButtonRenderer("+", this, scalpUI.customKeyEventDispatcher));
//		tradeTableModel.setExitButton(new TableOrderExitButtonRenderer("Exit", this));
		
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
		
		scalpUI.logMessageListModel.addElement(String.format(ORDER_EXECUTED_FORMAT, order.getTsym()));
		
		scalpUI.tradeTable.scrollRectToVisible(
				scalpUI.tradeTable.getCellRect(scalpUI.tradeTable.getRowCount() - 1, 0, true));
		
	}
	public void updatePLSummary() {
		int count = scalpUI.tradeTableModel.getRowCount();
		
		Float bookPL = 0f;
		Integer openPosition = 0;
		Integer closedPosition = 0;
		
		for(int row=0;row<count;row++) {
			if(scalpUI.tradeTableModel.getValueAt(row, TradeDataEnum.STATUS.getColumnIndex())
					.equals(StockEnum.OK.getDesc())) {
				bookPL += Float.parseFloat(scalpUI.tradeTableModel.getValueAt(row, TradeDataEnum.PL.getColumnIndex()).toString());
				closedPosition++;
			} else {
				openPosition++;
			}
		}
		
		scalpUI.bookedPLAmount.setText(bookPL.toString());
		scalpUI.openPositionValue.setText(openPosition.toString());
		scalpUI.closedPositionValue.setText(closedPosition.toString());
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
		} catch(Exception ex) {
			return "";
		}
		return  "";
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
	public String getLTPValue(JLabel inputLabel) {
		String currentPrice;
		do {
			currentPrice = inputLabel.getText();
			System.out.print("-");

		} while(!StringUtils.hasLength(currentPrice));

		return currentPrice;
	}
}
