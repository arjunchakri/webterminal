package com.sherlock.webterminal.remote.socketclient;

import java.net.URI;

public class SocketConnectionEstablish {

  public static void main(String[] args) throws Exception {

    connect("localhost:9000", "action_execution_socket",
        "{\"actionId\":\"Java - jps\",\"command\":\"jps -m\",\"workspace\":\"\",\"script_inputs\":\"{}\"}", null);
//    testSocket("hostname:9000", "formula_listener_socket", message);
    Thread.sleep(500000);

  }

  /*
   * 
   * host ->
   * testhostname:9000
   * 
   * socketName ->
   * action_execution_socket
   * formula_listener_socket
   * 
   * message ->
   * {\"actionId\":\"Java - jps\",\"command\":\"jps -m\",\"workspace\":\"\",\"script_inputs\":\"{}\"}
   */
  public static WebsocketClientEndpoint connect(String host, String socketName, String message,
      WebsocketClientEndpoint.MessageHandler incomingMsgHandler) throws Exception {

    WebsocketClientEndpoint clientEndPoint = new WebsocketClientEndpoint(new URI("ws://" + host + "/" + socketName));

    if (incomingMsgHandler != null) {
      clientEndPoint.addMessageHandler(incomingMsgHandler);
    } else {
      clientEndPoint.addMessageHandler(new WebsocketClientEndpoint.MessageHandler() {
        public void handleMessage(String message) {
          System.out.println(message);
        }
      });
    }

    if (message != null) {
      clientEndPoint.sendMessage(message);
    }

    return clientEndPoint;
  }

}
