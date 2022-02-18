package com.sherlock.webterminal.teamsapi;

import java.util.Arrays;

public class TeamsMessengerHandler {

  public static void main(String[] args) {
    handle(new TeamsMessageData(null, Arrays.asList("testing1")), "testing123");
    handle(new TeamsMessageData(null, Arrays.asList("testing1")), "testinddg123");
    handle(new TeamsMessageData(null, Arrays.asList("testing1")), "testing 123");
  }

  public static void sendMessage(TeamsMessageData teamsNotifier, String line) {
    String webhookURL = teamsNotifier.getWebhookURL();
    TeamsMessenger.triggerMessage(webhookURL, line);
  }

  public static void handle(TeamsMessageData teamsNotifier, String line) {
    String webhookURL = teamsNotifier.getWebhookURL();
    for (String eachPattern : teamsNotifier.getConsolePatterns()) {
      if (line.contains(eachPattern)) {
        System.out.println("Notifying in teams, pattern matched - " + line);
        TeamsMessenger.triggerMessage(webhookURL, line);
      }
    }
  }

}
