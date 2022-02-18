package com.sherlock.webterminal.commandexec.executor;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.sherlock.webterminal.spring.core.socket.LogSocketMessenger;
import org.springframework.web.socket.WebSocketSession;

import com.sherlock.webterminal.commandexec.exception.WebListenterException;

public class WebListenersHandler {

  private static Map<String, List<WebSocketSession>> webListeners =
      new ConcurrentHashMap<String, List<WebSocketSession>>();

  public static void sendMessage(String message, String runnerKey) throws WebListenterException {
    List<WebSocketSession> allListeners = webListeners.get(runnerKey);

    LogSocketMessenger.sendMessage(message, allListeners);
  }

  public static void clearListeners(String runnerKey) {
    if (runnerKey == null) {
      return;
    }

    webListeners.remove(runnerKey);
  }

  public static void addListener(String runnerKey, WebSocketSession session) {
    if (runnerKey == null) {
      return;
    }

    List<WebSocketSession> allListeners = new ArrayList<WebSocketSession>();

    if (webListeners.containsKey(runnerKey)) {
      allListeners = webListeners.get(runnerKey);
    }

    allListeners.add(session);
    webListeners.put(runnerKey, allListeners);

  }

}
