package com.sherlock.webterminal.commandexec.superaction.data;

public class SuperActionPackage {

  private String actionId;
  private String commandVars;
  private String contextVars;

  public SuperActionPackage(String actionId, String commandVars, String contextVars) {
    this.actionId = actionId;
    this.commandVars = commandVars;
    this.contextVars = contextVars;
  }

  @Override
  public String toString() {
    return "SuperActionPackage [actionId=" + actionId + ", commandVars=" + commandVars + ", contextVars=" + contextVars
        + "]";
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((actionId == null) ? 0 : actionId.hashCode());
    result = prime * result + ((commandVars == null) ? 0 : commandVars.hashCode());
    result = prime * result + ((contextVars == null) ? 0 : contextVars.hashCode());
    return result;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj)
      return true;
    if (obj == null)
      return false;
    if (getClass() != obj.getClass())
      return false;
    SuperActionPackage other = (SuperActionPackage) obj;
    if (actionId == null) {
      if (other.actionId != null)
        return false;
    } else if (!actionId.equals(other.actionId))
      return false;
    if (commandVars == null) {
      if (other.commandVars != null)
        return false;
    } else if (!commandVars.equals(other.commandVars))
      return false;
    if (contextVars == null) {
      if (other.contextVars != null)
        return false;
    } else if (!contextVars.equals(other.contextVars))
      return false;
    return true;
  }

  public String getActionId() {
    return actionId;
  }

  public void setActionId(String actionId) {
    this.actionId = actionId;
  }

  public String getCommandVars() {
    return commandVars;
  }

  public void setCommandVars(String commandVars) {
    this.commandVars = commandVars;
  }

  public String getContextVars() {
    return contextVars;
  }

  public void setContextVars(String contextVars) {
    this.contextVars = contextVars;
  }

}
