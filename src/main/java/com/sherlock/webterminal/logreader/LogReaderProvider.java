package com.sherlock.webterminal.logreader;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.web.socket.WebSocketSession;

import com.sherlock.webterminal.logreader.services.ApacheReverseFileReaderAPI;

public class LogReaderProvider {

  public static volatile Map<String, ApacheReverseFileReaderAPI> fileReaderMap =
      new HashMap<String, ApacheReverseFileReaderAPI>();


  public static List<String> readLogHTTP(String logFile, int lastNumberOfLines, String readerKey) throws Exception {
    ApacheReverseFileReaderAPI fileReader = null;

    fileReader = fileReaderMap.get(readerKey);
    if (fileReader == null) {
      System.out.println("Creating file object for " + logFile);
      fileReader = new ApacheReverseFileReaderAPI(logFile);
      System.out.println("Registering object with the key " + readerKey);
      fileReaderMap.put(readerKey, fileReader);
    }
    
    if (!fileReader.getFilePath().equals(logFile)) {
      System.out.println("Switching file reader to " + logFile);
      fileReader = new ApacheReverseFileReaderAPI(logFile);
      fileReaderMap.put(readerKey, fileReader);
    }

    return fileReader.readLastLines(lastNumberOfLines, null);
  }
  
  public static List<String> readLog(String logFile, int lastNumberOfLines, String readerKey, WebSocketSession session) throws Exception {
    ApacheReverseFileReaderAPI fileReader = null;

    fileReader = fileReaderMap.get(readerKey);
    if (fileReader == null) {
      System.out.println("Creating file object for " + logFile);
      fileReader = new ApacheReverseFileReaderAPI(logFile);
      System.out.println("Registering object with the key " + readerKey);
      fileReaderMap.put(readerKey, fileReader);
    }
    
    if (!fileReader.getFilePath().equals(logFile)) {
      System.out.println("Switching file reader to " + logFile);
      fileReader = new ApacheReverseFileReaderAPI(logFile);
      fileReaderMap.put(readerKey, fileReader);
    }

    return fileReader.readLastLines(lastNumberOfLines, session);
  }


}
