package com.sherlock.webterminal.remote;

import java.net.URISyntaxException;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;

import org.springframework.web.socket.WebSocketSession;

import com.google.gson.Gson;
import com.sherlock.webterminal.formulas.data.FormulaWebInput;
import com.sherlock.webterminal.remote.data.RemoteHost;
import com.sherlock.webterminal.spring.core.socket.LogSocketMessenger;

public class RemoteListener {

  //Current server webconnections
  static volatile Map<String, WebSocketSession> listenerSessions = new ConcurrentHashMap<String, WebSocketSession>();

  //Map of hostname -> (sockets)
  static volatile Map<String, RemoteHost> remoteSessions = new ConcurrentHashMap<String, RemoteHost>();

  public static void main(String[] args) throws URISyntaxException {
    String host = "http://localhost:8080/";

    System.out.println("path=" + host.replace("http://", "").replace("https://", "").replaceAll("/", ""));
  }

  public static String trimURL(String input) throws Exception {
    return input.replace("http://", "").replace("https://", "").replaceAll("/", "");
  }

  public static void handleRemoteEvent(String host, FormulaWebInput webInput) {
    if (listenerSessions.size() <= 0) {
      //Do nothing if no listeners are there
      return;
    }

    notifyClients(listenerSessions.values());

  }

  static {
    try {
      startFormulaeListeners();
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  public static void startFormulaeListeners() throws Exception {

    List<String> remoteHosts = RemoteControllerManager.getRemoteHosts();

    for (String eachRemoteHost : remoteHosts) {

      RemoteHost hostSession = new RemoteHost();

      hostSession.setHost(eachRemoteHost);
      hostSession.setTrimmedHost(trimURL(eachRemoteHost));

      hostSession.setCachedResults(new ConcurrentHashMap<String, FormulaWebInput>());
      hostSession.setProcessService(Executors.newSingleThreadExecutor());
      hostSession.getProcessService().execute(new RemoteRunnable(hostSession));
      remoteSessions.put(eachRemoteHost, hostSession);

    }

  }

  public static void connectClient(WebSocketSession session) {

    String id = session.getId();
    listenerSessions.put(id, session);
    System.out.println("connectedClient -> " + id + " || numofclients=" + listenerSessions.size());

    notifyClients(Arrays.asList(session));

  }

  public static void disconnectClient(WebSocketSession session) {

    String id = session.getId();

    if (id == null) {
      return;
    }

    listenerSessions.remove(id);
    System.out.println("disconnect -> " + id + " || numofclients=" + listenerSessions.size());

  }

  public static void notifyClients(Collection<WebSocketSession> webSocketSessions) {
    for (WebSocketSession eachSession : webSocketSessions) {
      Gson gson = new Gson();
      LogSocketMessenger.sendMessage(gson.toJson(remoteSessions), eachSession);
    }
  }

}
