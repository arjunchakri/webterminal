package com.sherlock.webterminal.teamsapi;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.NoSuchElementException;
import java.util.Scanner;

public class TeamsMessenger {

  public static void triggerMessage(String teamsHookURL, String message) {

    if (teamsHookURL == null || teamsHookURL.isEmpty()) {
      System.out.println("teamsHookURL is empty/null");
      return;
    }

    try {
      String jsonPaylaod = "{\"text\": \"" + message + "\"}";
      getResponse(teamsHookURL, "POST", jsonPaylaod);
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  public static String getResponse(String url, String requestMethod, String payload) throws Exception {
    String methodName = "getResponse";
    try {
      url = url.replace(" ", "%20");
      HttpURLConnection con = getConnection(url, requestMethod, payload);

      if (con.getResponseCode() == 200) {

        InputStream response = null;
        response = con.getInputStream();
        String json = null;
        Scanner scanner = null;
        if (response != null) {
          try {
            scanner = new Scanner(response);
            json = scanner.useDelimiter("\\A").next();

          } finally {
            if (scanner != null) {
              scanner.close();
            }
            con.disconnect();
          }
          return json;
        }
      }

    } catch (MalformedURLException e) {
      e.printStackTrace();
    } catch (IOException e) {
      e.printStackTrace();
    } catch (NoSuchElementException e) {
      e.printStackTrace();
    }
    return null;
  }

  public static synchronized HttpURLConnection getConnection(String projectsURL, String requestMethod, String payload)
      throws MalformedURLException, IOException {
    URL url;
    url = new URL(projectsURL);
    HttpURLConnection con = (HttpURLConnection) url.openConnection();
    con.setRequestMethod(requestMethod);
    con.setRequestProperty("Accept", "application/json");
    con.setRequestProperty("content-type", "application/json");

    if (payload != null) {
      con.setDoOutput(true);

      OutputStreamWriter writer = new OutputStreamWriter(con.getOutputStream(), "UTF-8");
      writer.write(payload);
      writer.close();

    }
    return con;
  }

  public static void main(String[] args) {
    triggerMessage(
        "<TEAMS WEBHOOKURL HERE>",
        "testing from java code");
  }

}
