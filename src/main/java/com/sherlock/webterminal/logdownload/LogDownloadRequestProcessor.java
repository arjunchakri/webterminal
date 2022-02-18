package com.sherlock.webterminal.logdownload;

import java.io.File;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.sherlock.webterminal.logdownload.processor.LogDownloadManager;
import com.sherlock.webterminal.logdownload.webinteractions.MessageProtocolBean;
import com.sherlock.webterminal.spring.core.socket.LogSocketMessenger;

public class LogDownloadRequestProcessor {

  public static List<String> webOutput = new ArrayList<String>();
  
  public static List<String> process(String message) throws Exception {
    webOutput = new ArrayList<String>();
    
    Gson gson = new Gson();
    Type type = new TypeToken<Map<String, String>>() {
    }.getType();
    Map<String, String> inputMap = gson.fromJson(message, type);
    String configuredAction = inputMap.get("action");
    if (configuredAction.equals("download")) {

      String fileName = inputMap.get("fileName");
      String dirPath = inputMap.get("dirPath");
      String readerKey = inputMap.get("readerKey");

      File targetFile = new File(dirPath, fileName);

      File zippedFile = LogDownloadManager.getZippedFile(targetFile);

      DownloadFileRepository.addFile(zippedFile, readerKey);

      sendUrl("download?key=" + readerKey);
    }
    return webOutput;
  }

  public static String sendDisplayMessage(String message) {
    String protocolMessage = MessageProtocolBean.createBean("display", message);
    LogSocketMessenger.sendMessage(protocolMessage);
    webOutput.add(protocolMessage);
    
    return protocolMessage;
  }

  public static String sendUrl(String url) {
    String protocolMessage = MessageProtocolBean.createBean("click", url);
    LogSocketMessenger.sendMessage(protocolMessage);
    webOutput.add(protocolMessage);

    return protocolMessage;
  }

}
