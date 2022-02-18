package com.sherlock.webterminal.spring.core.socket.folderlistener;

import java.lang.reflect.Type;
import java.util.Map;

import com.sherlock.webterminal.spring.core.socket.LogSocketMessenger;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.sherlock.webterminal.filemanager.FileManager;

/**
 * @author anunugonda
 *
 */
public class FolderListenerSocket extends TextWebSocketHandler {

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
      String folderpath = inputMap.get("folderpath");
      String savecontents = inputMap.get("savecontents");

      FileManager.startFolderListener(folderpath, savecontents, session);

    } catch (Exception e) {
      e.printStackTrace();
      LogSocketMessenger.sendMessage(e.getMessage());
    }
  }

}
