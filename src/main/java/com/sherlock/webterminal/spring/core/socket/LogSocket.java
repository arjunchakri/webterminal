package com.sherlock.webterminal.spring.core.socket;

import java.io.File;
import java.lang.reflect.Type;
import java.util.Map;

import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.sherlock.webterminal.logreader.LogReaderProvider;

/**
 * @author anunugonda
 *
 */
public class LogSocket extends TextWebSocketHandler {

  @Override
  public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
  }

  @Override
  public void afterConnectionEstablished(WebSocketSession session) throws Exception {
    //session.sendMessage(new TextMessage("Socket connected."));

  }

  @Override
  protected void handleTextMessage(WebSocketSession session, TextMessage textMessage) throws Exception {
    try {
      String sessionId = session.getId();
      System.out.println("handleTextMessage: " + textMessage.getPayload() + " SessionId: " + sessionId);
      String message = textMessage.getPayload();

      Gson gson = new Gson();
      Type type = new TypeToken<Map<String, String>>() {
      }.getType();
      Map<String, String> inputMap = gson.fromJson(message, type);
      String filename = inputMap.get("fileName");
      String dirName = inputMap.get("dirPath");
      String readerKey = inputMap.get("readerKey");
      
      File logFile = new File(dirName, filename);
      LogReaderProvider.readLog(logFile.getAbsolutePath(), 500, readerKey, session);

    } catch (Exception e) {
      e.printStackTrace();
      LogSocketMessenger.sendMessage(e.getMessage());
    }
  }

}
