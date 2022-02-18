package com.sherlock.webterminal.logreader.services.nativelinuxreader;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;

public class LinuxCommandProvider {

  public static void main(String[] args) throws Exception {
    System.out.println(getCommand("grep"));
  }

  public static String getCommand(String command) throws Exception {
    boolean isWindows = System.getProperty("os.name").toLowerCase().contains("windows");
    if (isWindows) {
      String currentDirPath = System.getProperty("user.dir");
      String executablePath = currentDirPath + File.separator + "bin" + File.separator + "commands" + File.separator + command + ".exe";
      File execFile = new File(executablePath);
      if(!execFile.exists()) {
        System.out.println(command + " not found at LinuxCommandProvider.getCommand. Creating it from resources.");
        InputStream executableStream = LinuxCommandProvider.class.getClass().getResourceAsStream("commands" + File.separator + command + ".exe");
        byte[] buffer = new byte[executableStream.available()];
        OutputStream output = new FileOutputStream(execFile);
        output.write(buffer);
        output.close();
      }
      return executablePath;
    } else {
      return command;
    }
  }

}
