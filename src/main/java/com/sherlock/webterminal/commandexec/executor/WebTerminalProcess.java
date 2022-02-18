package com.sherlock.webterminal.commandexec.executor;

import java.util.List;
import java.util.concurrent.ExecutorService;

import com.sherlock.webterminal.spring.core.socket.LogSocketMessenger;
import org.springframework.web.socket.WebSocketSession;

import com.sherlock.webterminal.commandexec.CommandExecutionManager;
import com.sherlock.webterminal.commandexec.exception.WebListenterException;

public class WebTerminalProcess {

  private ExecutorService processService;
  private List<WebSocketSession> socketSessions;
  private CommandExecutionManager executionManager;
  private Process process;

  private String processName;

  public WebTerminalProcess() {
    super();
  }

  public void clear() {

    try {
      if (process != null) {
        process.destroy();
        process.destroyForcibly();
      }
    } catch (Exception e) {
      e.printStackTrace();
    }
    try {

      processService.shutdown();
      processService.shutdownNow();
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  public Process getProcess() {
    return process;
  }

  public void setProcess(Process process) {
    this.process = process;
  }

  public WebTerminalProcess(ExecutorService processService, List<WebSocketSession> socketSessions,
      CommandExecutionManager executionManager, String processName) {
    super();
    this.processService = processService;
    this.socketSessions = socketSessions;
    this.executionManager = executionManager;
    this.processName = processName;
  }

  public CommandExecutionManager getExecutionManager() {
    return executionManager;
  }

  public void setExecutionManager(CommandExecutionManager executionManager) {
    this.executionManager = executionManager;
  }

  public ExecutorService getProcessService() {
    return processService;
  }

  public void setProcessService(ExecutorService processService) {
    this.processService = processService;
  }

  public List<WebSocketSession> getSocketSessions() {
    return socketSessions;
  }

  public void setSocketSessions(List<WebSocketSession> socketSessions) {
    this.socketSessions = socketSessions;
  }

  public String getProcessName() {
    return processName;
  }

  public void setProcessName(String processName) {
    this.processName = processName;
  }

  public void sendMessage(String message, String runnerKey) throws WebListenterException {
    LogSocketMessenger.sendMessage(message, socketSessions);
  }

}
