package com.sherlock.webterminal.spring.core.socket.remote;

import com.sherlock.webterminal.spring.core.socket.LogSocketMessenger;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import com.sherlock.webterminal.logdownload.LogDownloadRequestProcessor;

/**
 * @author anunugonda
 *
 */
public class RemoteLogDownloadSocket extends TextWebSocketHandler {

  @Override
  public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
  }

  @Override
  public void afterConnectionEstablished(WebSocketSession session) throws Exception {
    LogSocketMessenger.setWebSocketSession(session);
  }

  @Override
  protected void handleTextMessage(WebSocketSession session, TextMessage textMessage) {
    try {
      String sessionId = session.getId();
      System.out.println("handleTextMessage: " + textMessage.getPayload() + " SessionId: " + sessionId);

      LogDownloadRequestProcessor.process(textMessage.getPayload());

    } catch (Exception e) {
      e.printStackTrace();
      LogSocketMessenger.sendMessage(e.getMessage());
    }
  }

}
