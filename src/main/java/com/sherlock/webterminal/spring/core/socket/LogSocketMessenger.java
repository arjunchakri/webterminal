package com.sherlock.webterminal.spring.core.socket;

import java.util.List;

import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

/**
 * @author anunugonda
 *
 */
public class LogSocketMessenger {

  private static WebSocketSession session = null;

  public static void setWebSocketSession(WebSocketSession sessionObj) {
    session = sessionObj;
  }

  public static boolean sendMessage(String message, List<WebSocketSession> sessions) {
    try {
      if (sessions == null) {
        System.out.println(message);
        return true;
      }
      for (WebSocketSession webSocketSession : sessions) {
        sendMessage(message, webSocketSession);
      }
    } catch (Exception e) {
      e.printStackTrace();
      return false;
    }
    return true;
  }

  public static boolean sendMessage(String message) {
    try {
      if (session != null) {
        session.sendMessage(new TextMessage(message));
        return true;
      }
    } catch (Exception e) {
      e.printStackTrace();
      return false;
    }
    return false;

  }

  public static boolean sendMessage(String message, WebSocketSession session) {
    try {
      if (session == null) {
        //System.out.println("sendMessage:(session == null) : " + message);
        return false;
      }

      synchronized (session) {
        if (session != null) {
          session.sendMessage(new TextMessage(message));
          return true;
        }
      }
    } catch (Exception e) {
      e.printStackTrace();
      return false;
    }
    return false;

  }

  public static void handleMessage(String data) {

  }

}
