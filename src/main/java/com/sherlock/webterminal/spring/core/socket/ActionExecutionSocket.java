package com.sherlock.webterminal.spring.core.socket;

import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import com.sherlock.webterminal.commandexec.executor.TerminalExecutorRepository;

/**
 * @author anunugonda
 *
 */
public class ActionExecutionSocket extends TextWebSocketHandler {

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

      TerminalExecutorRepository.addProcess(message, session);

//      Gson gson = new Gson();
//      Type type = new TypeToken<Map<String, String>>() {
//      }.getType();
//      Map<String, String> inputMap = gson.fromJson(message, type);
//      String actionId = inputMap.get("actionId");
//      String command = inputMap.get("command");
//      String workspace = inputMap.get("workspace");
//      String script_inputs = inputMap.get("script_inputs");
//
//      CommandExecutionManager.executeCommand(actionId, command, workspace, script_inputs, session);

    } catch (Exception e) {
      e.printStackTrace();
      LogSocketMessenger.sendMessage(e.getMessage());
    }
  }

}
