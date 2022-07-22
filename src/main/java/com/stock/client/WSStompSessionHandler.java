package com.stock.client;

import java.lang.reflect.Type;

import org.springframework.messaging.Message;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaders;
import org.springframework.messaging.simp.stomp.StompSession;
import org.springframework.messaging.simp.stomp.StompSessionHandlerAdapter;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class WSStompSessionHandler extends StompSessionHandlerAdapter {

	@Override
	public void afterConnected(StompSession session, StompHeaders connectedHeaders) {
		session.subscribe("/topic/messages", this);
		//session.send("/app/chat", getSampleMessage());
	}

	@Override
	public void handleException(StompSession session, StompCommand command, StompHeaders headers, byte[] payload,
			Throwable exception) {
		log.error("Got an exception", exception);
	}

	@Override
	public Type getPayloadType(StompHeaders headers) {
		return Message.class;
	}

	@Override
	public void handleFrame(StompHeaders headers, Object payload) {
		Message msg = (Message) payload;
		log.info("Received : " + msg);
	}

//	private Message getSampleMessage() {
//		Message msg = new Message();
//		msg.setFrom("Nicky");
//		msg.setText("Howdy!!");
//		return msg;
//	}
}