package com.sherlock.webterminal.remote;

import java.io.File;
import java.lang.reflect.Type;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

import com.sherlock.webterminal.core.workspace.WorkspaceConstants;
import org.apache.commons.io.FileUtils;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.sherlock.webterminal.core.workspace.ActionsManager;
import com.sherlock.webterminal.utils.RestUtils;

public class RemoteControllerManager {

  public final static String REMOTE_CONTROLLER_HOSTSLIST =
      WorkspaceConstants.APP_USER_WORKSPACE + "/remotecontrol/hosts.list";

  public static void main(String[] args) throws Exception {

    System.out.println(getRemoteHosts());

  }

  public static void addToRemoteHosts(String inputHost) throws Exception {

    if (checkIfValidWebTerminal(inputHost)) {
      addToHostsFile(inputHost);
    } else {
      throw new Exception(inputHost + " is not a valid webterminal host.");
    }

  }

  public static List<String> getRemoteHosts() {
    try {

      File hostList = new File(REMOTE_CONTROLLER_HOSTSLIST);

      if (hostList.exists()) {
        Gson gson = new Gson();
        Type type = new TypeToken<List<String>>() {
        }.getType();
        List<String> inputMap;
        inputMap = gson.fromJson(FileUtils.readFileToString(hostList, Charset.defaultCharset()), type);

        if (inputMap == null) {
          throw new NullPointerException();
        }

        return inputMap;

      }
    } catch (Exception e) {
      e.printStackTrace();
    }

    return new ArrayList<String>();

  }

  private static void addToHostsFile(String inputUrl) {
    try {
      List<String> hostsList = getRemoteHosts();
      if (!hostsList.contains(inputUrl)) {
        hostsList.add(inputUrl);
      }
      ActionsManager.writeToFile(hostsList, REMOTE_CONTROLLER_HOSTSLIST);
    } catch (Exception e) {
      e.printStackTrace();
    }

  }

  /*
   * input with port -> http://localhost:9000
   */
  public static boolean checkIfValidWebTerminal(String inputUrl) {
    try {

      String handshakeURL = inputUrl + "/handshake";

      String response = RestUtils.getResponse(handshakeURL, "GET", "");

      if (response.equals("OK")) {
        return true;
      }

    } catch (Exception e) {
//      e.printStackTrace();
      return false;
    }

    return false;

  }

}
