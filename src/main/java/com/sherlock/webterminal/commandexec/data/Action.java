package com.sherlock.webterminal.commandexec.data;

public class Action {

  String actionId;
  String command;
  CommandOS command_os;
  String workspace;
  String compatibility;
  String description;
  String showconfirmation;

  public Action(String actionId, String command, String workspace) {
    super();
    this.actionId = actionId;
    this.command = command;
    this.workspace = workspace;
  }

  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  public CommandOS getCommand_os() {
    return command_os;
  }

  public void setCommand_os(CommandOS command_os) {
    this.command_os = command_os;
  }

  public String getCompatibility() {
    return compatibility;
  }

  public void setCompatibility(String compatibility) {
    this.compatibility = compatibility;
  }

  public String getActionId() {
    return actionId;
  }

  public void setActionId(String actionId) {
    this.actionId = actionId;
  }

  public String getCommand() {
    return command;
  }

  public void setCommand(String command) {
    this.command = command;
  }

  public String getWorkspace() {
    return workspace;
  }

  public void setWorkspace(String workspace) {
    this.workspace = workspace;
  }

  @Override
  public String toString() {
    return "Action [actionId=" + actionId + ", command=" + command + ", command_os=" + command_os + ", workspace="
        + workspace + ", compatibility=" + compatibility + ", description=" + description + "]";
  }

}
