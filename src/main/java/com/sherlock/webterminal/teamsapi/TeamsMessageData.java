package com.sherlock.webterminal.teamsapi;

import java.util.List;

public class TeamsMessageData {

  private String webhookURL;
  private List<String> consolePatterns;

  public TeamsMessageData(String webhookURL, List<String> consolePatterns) {
    super();
    this.webhookURL = webhookURL;
    this.consolePatterns = consolePatterns;
  }

  public String getWebhookURL() {
    return webhookURL;
  }

  public void setWebhookURL(String webhookURL) {
    this.webhookURL = webhookURL;
  }

  public List<String> getConsolePatterns() {
    return consolePatterns;
  }

  public void setConsolePatterns(List<String> consolePatterns) {
    this.consolePatterns = consolePatterns;
  }


}
