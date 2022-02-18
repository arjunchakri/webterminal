package com.sherlock.webterminal.logreader.configuration;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;

import com.sherlock.webterminal.core.workspace.ActionsManager;
import com.sherlock.webterminal.logreader.services.directory.data.DirectoryInputBean;

public class TempConstants {

  public static Properties properties = new Properties();

  public static String propertyFile = "webserver.properties";

  public static List<DirectoryInputBean> DIRS_TO_READ = Arrays
      .asList(new DirectoryInputBean[] { new DirectoryInputBean("App base", "") });
  

  public static List<DirectoryInputBean> DIRS_TO_READ_LINUX = Arrays
      .asList(new DirectoryInputBean[] { new DirectoryInputBean("App base", "") });
  
  public static String currentAgentPort = "";
  public static String currentAgentMTPPort = "";
  public static String currentAgentHostname = "";
  public static String currentServerType = "server";

  public static String targetAgentHostname = "";
  public static String targetAgentName = "";

  public static String targetAgentPort = "";
  public static String targetAgentMTPPort = "";

  public static String setConfiguredDirs(String csvDirs) {

    if (csvDirs == null) {
      return "csvDirs == null";
    }

    if (csvDirs.isEmpty()) {
      return "csvDirs.isEmpty()";
    }

    try {
      List<DirectoryInputBean> resultList = new ArrayList<DirectoryInputBean>();
      String[] dirArgPieces = csvDirs.split(",");

      for (int i = 0; i < dirArgPieces.length;) {
        String displayName = dirArgPieces[i++];
        String path = dirArgPieces[i++];
        resultList.add(new DirectoryInputBean(displayName, path));
      }

      System.out.println("Dir list configured as " + resultList);
      DIRS_TO_READ = resultList;
      DIRS_TO_READ_LINUX = resultList;
      ActionsManager.writeToFile(DIRS_TO_READ, ActionsManager.DIR_CONFIGURATION_JSON);
      return "Dir list configured as " + resultList;
    } catch (Exception e) {
      System.out.println("Exception during setConfiguredDirs. Current DIRS_TO_READ=" + DIRS_TO_READ.toString());
      e.printStackTrace();
      return e.getMessage();
    }
  }

  public static void initializeProperties() {
    readPropertiesFile();
  }

  public static String get(String key) {
    return properties.getProperty(key);
  }

  private static void readPropertiesFile() {
    try {
      File propFileObject = new File(propertyFile);

      if (!propFileObject.exists()) {
        System.out.println(propertyFile + " not found.");
        return;
      }

      InputStream input = new FileInputStream(propFileObject);
      properties.load(input);
    } catch (FileNotFoundException ex) {
      System.out.println(propertyFile + " not found.");
      throw new IllegalArgumentException();
    } catch (Exception ex) {
      ex.printStackTrace();
    }
  }

}
