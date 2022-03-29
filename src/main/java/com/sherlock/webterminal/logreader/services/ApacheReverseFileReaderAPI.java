package com.sherlock.webterminal.logreader.services;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.sherlock.webterminal.logreader.services.directory.data.DirectoryInputBean;
import com.sherlock.webterminal.spring.core.socket.LogSocketMessenger;
import org.apache.commons.io.input.ReversedLinesFileReader;
import org.springframework.web.socket.WebSocketSession;

public class ApacheReverseFileReaderAPI {

  private ReversedLinesFileReader object = null;

  private String filePath = null;

  public ApacheReverseFileReaderAPI(String filePath) throws IOException {
    super();
    this.object = new ReversedLinesFileReader(new File(filePath));
    this.filePath = filePath;
  }

  public String getFilePath() {
    return filePath;
  }

  public List<String> readLastLines(int lastNumberOfLines, WebSocketSession session) throws Exception {
    List<String> linesCollection = new ArrayList<String>();
    int counter = 0;

    while (counter < lastNumberOfLines) {
      String eachLine = readNextLine();
      if (eachLine == null) {
        break;
      }

//      System.out.println(eachLine);
      linesCollection.add(eachLine);
//      LogSocketMessenger.sendMessage(eachLine);
      if (session != null) {
        LogSocketMessenger.sendMessage(eachLine, session);
      }

      counter++;
    }
    System.out.println(counter);
    return linesCollection;
  }

  public List<String> readLastLines(int lastNumberOfLines) throws Exception {
    List<String> linesCollection = new ArrayList<String>();
    int counter = 0;

    while (counter < lastNumberOfLines) {
      String eachLine = readNextLine();
      if (eachLine == null) {
        break;
      }
//      System.out.println(eachLine);
      linesCollection.add(eachLine);
//      LogSocketMessenger.sendMessage(eachLine);

      counter++;
    }
    System.out.println(counter);
    return linesCollection;
  }

  private String readNextLine() throws Exception {
    return object.readLine();
  }

  private void closeStream() throws Exception {
    object.close();
  }


  public static void main(String[] args) throws Exception {
    ApacheReverseFileReaderAPI readerObject =
            new ApacheReverseFileReaderAPI("my.log");
//    ApacheReverseFileReaderAPI readerObject = new ApacheReverseFileReaderAPI("C:\\Users\\anunugonda\\Documents\\InstallationTiming\\core-6.7.1\\core-6.7.1.log");

//    readerObject.readLastLines(500);
//    System.out.println("--------------------");
//    readerObject.readLastLines(50);
//    System.out.println("--------------------");
//    readerObject.readLastLines(50);

    readerObject.closeStream();
//    readLastLines("C:\\Users\\anunugonda\\Documents\\InstallationTiming\\core-6.7.1\\core-6.7.1.log");
  }
}
