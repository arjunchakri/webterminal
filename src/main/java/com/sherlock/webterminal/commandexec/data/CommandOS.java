package com.sherlock.webterminal.commandexec.data;

public class CommandOS {

  String windows;
  String linux;

  public CommandOS(String windows, String linux) {
    super();
    this.windows = windows;
    this.linux = linux;
  }

  @Override
  public String toString() {
    return "CommandOS [windows=" + windows + ", linux=" + linux + "]";
  }

  public String getWindows() {
    return windows;
  }

  public void setWindows(String windows) {
    this.windows = windows;
  }

  public String getLinux() {
    return linux;
  }

  public void setLinux(String linux) {
    this.linux = linux;
  }

}
