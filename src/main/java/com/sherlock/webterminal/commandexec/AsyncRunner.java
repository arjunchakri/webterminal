package com.sherlock.webterminal.commandexec;

import com.sherlock.webterminal.core.workspace.ActionsManager;

public class AsyncRunner implements Runnable {

  private String payload;

  public AsyncRunner(String payload) {
    this.payload = payload;
  }

  @Override
  public void run() {
    try {
      CommandExecutionManager.executeCommand(ActionsManager.getAction(payload));
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

}
