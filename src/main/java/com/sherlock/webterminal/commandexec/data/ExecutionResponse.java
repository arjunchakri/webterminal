package com.sherlock.webterminal.commandexec.data;

import java.util.List;

public class ExecutionResponse {

  private int returnCode;
  private List<String> consoleLogs;

  public ExecutionResponse(int returnCode, List<String> consoleLogs) {
    super();
    this.returnCode = returnCode;
    this.consoleLogs = consoleLogs;
  }

  public int getReturnCode() {
    return returnCode;
  }

  public void setReturnCode(int returnCode) {
    this.returnCode = returnCode;
  }

  public List<String> getConsoleLogs() {
    return consoleLogs;
  }

  public void setConsoleLogs(List<String> consoleLogs) {
    this.consoleLogs = consoleLogs;
  }

}
