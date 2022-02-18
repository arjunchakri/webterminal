package com.sherlock.webterminal.logdownload.webinteractions;

import com.google.gson.Gson;

public class MessageProtocolBean {

  String action;
  String data;
  String metadata;

  public static String createBean(String configuredAction, String configuredData) {
    Gson gson = new Gson();
    MessageProtocolBean bean = new MessageProtocolBean(configuredAction, configuredData, "");
    return gson.toJson(bean);
  }

  public MessageProtocolBean(String action, String data, String metadata) {
    super();
    this.action = action;
    this.data = data;
    this.metadata = metadata;
  }

  public String getAction() {
    return action;
  }

  public void setAction(String action) {
    this.action = action;
  }

  public String getData() {
    return data;
  }

  public void setData(String data) {
    this.data = data;
  }

  public String getMetadata() {
    return metadata;
  }

  public void setMetadata(String metadata) {
    this.metadata = metadata;
  }

}
