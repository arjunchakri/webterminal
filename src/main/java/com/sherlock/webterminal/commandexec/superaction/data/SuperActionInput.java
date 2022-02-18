package com.sherlock.webterminal.commandexec.superaction.data;

public class SuperActionInput {

  String actionId;
  String command;
  String workspace;
  String script_inputs;

  public SuperActionInput(String actionId, String command, String workspace, String script_inputs) {
    this.actionId = actionId;
    this.command = command;
    this.workspace = workspace;
    this.script_inputs = script_inputs;
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

  public String getScript_inputs() {
    return script_inputs;
  }

  public void setScript_inputs(String script_inputs) {
    this.script_inputs = script_inputs;
  }

  @Override
  public String toString() {
    return "SuperActionInputs [actionId=" + actionId + ", command=" + command + ", workspace=" + workspace
        + ", script_inputs=" + script_inputs + "]";
  }

}
