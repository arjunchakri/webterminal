package com.sherlock.webterminal.remote;

import java.util.Map;

import com.google.gson.Gson;
import com.sherlock.webterminal.formulas.data.FormulaWebInput;
import com.sherlock.webterminal.remote.data.RemoteHost;
import com.sherlock.webterminal.remote.socketclient.SocketConnectionEstablish;
import com.sherlock.webterminal.remote.socketclient.WebsocketClientEndpoint;

public class RemoteRunnable implements Runnable {

  private RemoteHost remoteHost;
  private WebsocketClientEndpoint clientEndpoint;

  public RemoteRunnable(RemoteHost remoteHost) {
    super();
    this.remoteHost = remoteHost;
  }

  @Override
  public void run() {
    try {

      final String host = remoteHost.getHost();
      final String trimmedHost = remoteHost.getTrimmedHost();
      final Map<String, FormulaWebInput> cachedResults = remoteHost.getCachedResults();

      clientEndpoint = SocketConnectionEstablish.connect(trimmedHost, "formula_listener_socket", null,
          new WebsocketClientEndpoint.MessageHandler() {
            public void handleMessage(String message) {
              System.out.println(trimmedHost + " -remotenotify-> " + message);

              Gson gson = new Gson();
              FormulaWebInput webInput = gson.fromJson(message, FormulaWebInput.class);
              cachedResults.put(webInput.getName(), webInput);
              RemoteListener.handleRemoteEvent(host, webInput);
            }
          });

    } catch (Exception e) {
      e.printStackTrace();
    }

  }

}
