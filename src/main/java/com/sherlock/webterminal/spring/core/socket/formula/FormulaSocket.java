package com.sherlock.webterminal.spring.core.socket.formula;

import com.sherlock.webterminal.spring.core.socket.LogSocketMessenger;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import com.sherlock.webterminal.formulas.CustomFormulaExecutor;

/**
 * @author anunugonda
 *
 */
public class FormulaSocket extends TextWebSocketHandler {

  @Override
  public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
    CustomFormulaExecutor.disconnectClient(session);
  }

  @Override
  public void afterConnectionEstablished(WebSocketSession session) throws Exception {
    //session.sendMessage(new TextMessage("Socket connected."));

    CustomFormulaExecutor.connectClient(session);

  }

  @Override
  protected void handleTextMessage(WebSocketSession session, TextMessage textMessage) throws Exception {
    try {
      String sessionId = session.getId();
      System.out.println("handleTextMessage: " + textMessage.getPayload() + " SessionId: " + sessionId);
      String message = textMessage.getPayload();

    } catch (Exception e) {
      e.printStackTrace();
      LogSocketMessenger.sendMessage(e.getMessage());
    }
  }

}
