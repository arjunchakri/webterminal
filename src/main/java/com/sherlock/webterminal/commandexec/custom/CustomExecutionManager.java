package com.sherlock.webterminal.commandexec.custom;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.sherlock.webterminal.core.workspace.ActionsManager;
import org.apache.commons.io.FilenameUtils;

import com.sherlock.webterminal.commandexec.executor.WebListenersHandler;

public class CustomExecutionManager {

  public static void main(String[] args) throws Exception {
    compileFile(new File(
        "TESTDIR"),
        "Pre.java");
  }

  public static List<String> compileFile(File workspace, String fileName) throws Exception {
    return compileFile(workspace, fileName, null);
  }

  public static List<String> compileFile(File workspace, String fileName, String runnerKey) throws Exception {
    File javaSourceFile = new File(workspace, fileName);
    if (!javaSourceFile.exists() || !javaSourceFile.canRead()) {
      return new ArrayList<String>(Arrays.asList("NO_FILE"));
    }
    List<String> logs = null;
    boolean isWindows = System.getProperty("os.name").toLowerCase().contains("windows");
    String classpathSeparator = (isWindows) ? ";" : ":";

    File pluginLibrary = new File(workspace.getAbsolutePath(), "libs");
    StringBuilder classpathArgBuilder = new StringBuilder();
    List<String> classpathJarList = new ArrayList<String>();
    if (pluginLibrary.isDirectory()) {
      File[] listFiles = pluginLibrary.listFiles();

      for (File eachFile : listFiles) {
        if (eachFile.isFile() && eachFile.canRead()) {
          String jarPath = eachFile.getAbsolutePath();
          if (jarPath.endsWith("jar")) {
            classpathJarList.add("libs/" + eachFile.getName());
          }
        }
      }

      classpathArgBuilder.append(" -cp ");
      classpathArgBuilder.append(String.join(classpathSeparator, classpathJarList));
    }

    String classpath = classpathArgBuilder.toString();

    try {

      if (classpath.isEmpty()) {
        classpath = "-cp .";
      } else {
        classpath = classpath + classpathSeparator + ".";
      }

      // TODO Refactor custom java path - Point 1
      String configuredJavaHome = ActionsManager.getConfiguredJavaHome();

      String compileCommand = configuredJavaHome + "javac " + classpath + " " + javaSourceFile.getName();

      System.out.println(" >> " + compileCommand);
      execute(compileCommand, workspace, runnerKey);

      String runCommand = configuredJavaHome + "java " + classpath + " " + FilenameUtils.removeExtension(javaSourceFile.getName());
      System.out.println(" >> " + runCommand);

      logs = execute(runCommand, workspace, runnerKey);
//      System.out.println("runLogs=" + logs);
    } catch (Exception e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
      throw e;
    }

    return logs;
  }

  public static List<String> execute(String commandExec, File contextDirectory, String runnerKey) throws Exception {
    WebListenersHandler.sendMessage("<span style='color:grey'>[ PRE - running " + commandExec + " ]</span> ",
        runnerKey);
    List<String> logs = new ArrayList<String>();
    ProcessBuilder pb = null;
    boolean isWindows = System.getProperty("os.name").toLowerCase().contains("windows");
    if (isWindows) {
      pb = new ProcessBuilder("cmd", "/c", commandExec);
    } else {
      pb = new ProcessBuilder("bash", "-c", commandExec);
    }
    pb.directory(contextDirectory);
    Process p = pb.start();
    InputStream inputstream = p.getInputStream();
    InputStreamReader inputstreamreader = new InputStreamReader(inputstream);
    BufferedReader bufferedreader = new BufferedReader(inputstreamreader);
    String line;
    while ((line = bufferedreader.readLine()) != null) {
      System.out.println(line);
      logs.add(line);
      WebListenersHandler.sendMessage(line, runnerKey);

    }

    boolean failed = false;

    BufferedReader errorBufferedStream = new BufferedReader(new InputStreamReader(p.getErrorStream()));
    while ((line = errorBufferedStream.readLine()) != null) {
      System.out.println("ERROR>> " + line);
      logs.add("ERROR>> " + line);
      WebListenersHandler.sendMessage("<span style='color:red'>" + line + "</span>", runnerKey);
      failed = true;
    }

    if (failed) {
      throw new Exception(commandExec + " execution failed. Halting the process.");
    }

    return logs;
  }

}
