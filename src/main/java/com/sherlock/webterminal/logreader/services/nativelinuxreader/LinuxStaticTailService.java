package com.sherlock.webterminal.logreader.services.nativelinuxreader;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;

import com.sherlock.webterminal.spring.core.socket.LogSocketMessenger;

public class LinuxStaticTailService {

  public static void main(String[] args) throws Exception {

    int numOfLines = 1000;
    String filePath = "C:\\Users\\anunugonda\\Documents\\InstallationTiming\\core-6.7.1\\core-6.7.1.log";

    tailLog(filePath, numOfLines);
  }

  public static void tailLog(String filePath, int numOfLines) throws Exception {

    String tailExec = LinuxCommandProvider.getCommand("tail");
    String currentDirPath = System.getProperty("user.dir");

    String tailCommand = tailExec + " -n " + numOfLines + " " + filePath;

    ProcessBuilder pb = null;
    boolean isWindows = System.getProperty("os.name").toLowerCase().contains("windows");
    if (isWindows) {
      pb = new ProcessBuilder("cmd", "/c", tailCommand);
    } else {
      pb = new ProcessBuilder("bash", "-c", tailCommand);
    }
    File dir = new File(currentDirPath);
    pb.directory(dir);
    Process p = pb.start();
//    p.waitFor();
    InputStream inputstream = p.getInputStream();
    InputStreamReader inputstreamreader = new InputStreamReader(inputstream);
    BufferedReader bufferedreader = new BufferedReader(inputstreamreader);
    String line;
    while ((line = bufferedreader.readLine()) != null) {
      System.out.println(line);
      LogSocketMessenger.sendMessage(line);
    }

  }

}
