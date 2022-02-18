package com.sherlock.webterminal.spring.core.socket.remote;

import com.sherlock.webterminal.remote.RemoteListener;
import com.sherlock.webterminal.spring.core.socket.LogSocketMessenger;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

/**
 * @author anunugonda
 *
 */
public class RemoteControlSocket extends TextWebSocketHandler {

  @Override
  public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
    RemoteListener.disconnectClient(session);
  }

  @Override
  public void afterConnectionEstablished(WebSocketSession session) throws Exception {
    //session.sendMessage(new TextMessage("Socket connected."));

    RemoteListener.connectClient(session);

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
