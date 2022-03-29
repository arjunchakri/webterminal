package com.sherlock.webterminal.core.workspace;

import java.io.File;
import java.io.FileWriter;
import java.io.Writer;
import java.lang.reflect.Type;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.apache.commons.io.FileUtils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.sherlock.webterminal.commandexec.data.Action;
import com.sherlock.webterminal.commandexec.data.CommandOS;
import com.sherlock.webterminal.commandexec.data.TerminalConstants;
import com.sherlock.webterminal.commandexec.superaction.data.SuperActionPackage;
import com.sherlock.webterminal.logdownload.processor.ZipFileService;
import org.springframework.util.StringUtils;

public class ActionsManager {

  public final static String PLUGIN_WORKSPACE = WorkspaceConstants.APP_WORKSPACE + "plugins/";
  public final static String CONFIG_WORKSPACE = WorkspaceConstants.APP_WORKSPACE + "config/";

  public final static String CONFIG_JAVAHOME_JSON = CONFIG_WORKSPACE + "javahome.json/";

  public final static String DIR_CONFIGURATION_JSON =
      WorkspaceConstants.APP_USER_WORKSPACE + "/fileviewer/configured_dirs.json";
  public final static String CURRENT_DIR_CONFIGURATION =
      WorkspaceConstants.APP_USER_WORKSPACE + "/fileviewer/currentdir.cached";
  public final static String DIR_TEMP_DIRECTORY = WorkspaceConstants.APP_USER_WORKSPACE + "/fileviewer/temp";

  public final static String PLUGIN_ACTION_WS = PLUGIN_WORKSPACE + "%s/";
  public final static String PLUGIN_ACTION_JSON = PLUGIN_ACTION_WS + "action.json";
  public final static String PLUGIN_SUPER_ACTION_JSON = PLUGIN_ACTION_WS + "superaction.json";

  public static void main(String[] args) throws Exception {
//    System.out.println(getAllActions());
    System.out.println("getConfiguredJavaHome -> " + getConfiguredJavaHome());
  }

  public static List<Action> getAllActions() throws Exception {
    List<Action> actionsList = new ArrayList<Action>();
    File workspace = new File(PLUGIN_WORKSPACE);

    if (!workspace.exists()) {
      createDefaultAction();
    }

    File[] listFiles = workspace.listFiles();

    if (listFiles.length == 0) {
      createDefaultAction();
      listFiles = workspace.listFiles();
    }

    for (File eachFile : listFiles) {
      if (eachFile.isDirectory()) {
        Action action;

        try {
          action = getAction(eachFile.getName());
        } catch (Exception e) {
          System.out.println("Error occured for the action directory: " + eachFile.getName());
          continue;
        }

        //CHECK COMPATIBLE OS
        try {
          String compatibility = action.getCompatibility();

          if (compatibility != null && !checkCompatility(compatibility)) {
            System.out.println("Skipping as not compatibile " + eachFile.getName());
            continue;
          }
        } catch (Exception e) {
          System.out.println("while checking for OS " + eachFile.getName());
          continue;
        }

        actionsList.add(action);
      }
    }

    Collections.sort(actionsList, new Comparator<Action>() {
      public int compare(Action o1, Action o2) {
        return o1.getActionId().compareTo(o2.getActionId());
      };
    });

    return actionsList;
  }

  private static boolean checkCompatility(String compatibility) {
    //TODO improve OS detection with ENUM flags
    boolean isWindows = System.getProperty("os.name").toLowerCase().contains("windows");
    if (isWindows) {
      if (compatibility.toLowerCase().contains(TerminalConstants.OS_WINDOWS)) {
        return true;
      }
    } else {
      if (compatibility.toLowerCase().contains(TerminalConstants.OS_LINUX)) {
        return true;
      }
    }
    return false;
  }

  private static void createDefaultAction() throws Exception {
    saveAction(new Action("jps", "jps", ""));
  }

  public static Action getAction(String actionId) throws Exception {
    String targetPath = String.format(PLUGIN_ACTION_JSON, actionId);
    File targetFile = new File(targetPath);

    if (!targetFile.exists()) {
      return null;
    }

    String actionContent = FileUtils.readFileToString(targetFile);
    Gson gson = new Gson();
    Action responseAction = gson.fromJson(actionContent, Action.class);
    if (responseAction.getCommand() == null) {
      responseAction.setCommand(getOSCommand(responseAction));
    }
    responseAction.setActionId(actionId);
    return responseAction;
  }

  private static String getOSCommand(Action responseAction) throws Exception {
    boolean isWindows = System.getProperty("os.name").toLowerCase().contains("windows");
    CommandOS command_os = responseAction.getCommand_os();

    if (command_os != null) {
      if (isWindows) {
        return command_os.getWindows();
      } else {
        return command_os.getLinux();
      }
    }
    throw new Exception("No proper command/command_os found in action json");
  }

  public static List<SuperActionPackage> getSuperAction(String actionId) throws Exception {
    String targetPath = String.format(PLUGIN_SUPER_ACTION_JSON, actionId);
    File targetFile = new File(targetPath);

    if (!targetFile.exists()) {
      return null;
    }

    String actionContent = FileUtils.readFileToString(targetFile);
    Gson gson = new Gson();
    Type type = new TypeToken<List<SuperActionPackage>>() {
    }.getType();

    return gson.fromJson(actionContent, type);
  }

  public static String getConfiguredJavaHome() throws Exception {
    File targetFile = new File(CONFIG_JAVAHOME_JSON);

    if (!targetFile.exists()) {
      return "";
    }

    String fileContent = FileUtils.readFileToString(targetFile, Charset.defaultCharset());
    if(StringUtils.isEmpty(fileContent)) {
      return "";
    }

    File javaHomeConfigured = new File(fileContent, "bin");
    if(javaHomeConfigured.exists()) {
      return javaHomeConfigured.getAbsolutePath() + "/";
    }

    return "";
  }

  public static String addAction(String actionId, String command, String workspace) throws Exception {
    String response = "OK";
    try {
      Action action = new Action(actionId, command, workspace);
      saveAction(action);
    } catch (Exception e) {
      e.printStackTrace();
      response = "ERROR";
    }
    return response;
  }

  public static File exportAction(String actionId) throws Exception {
    //Zip and returns file object 
    try {
      String targetPath = String.format(PLUGIN_ACTION_WS, actionId);
      File tempDir = new File(WorkspaceConstants.APP_TEMP_WORKSPACE, System.currentTimeMillis() + "");
      tempDir.mkdirs();

      File zipFile = new File(tempDir, actionId + ".zip");

      ZipFileService.zipDirectory(zipFile.getAbsolutePath(), targetPath);

      return zipFile;
    } catch (Exception e) {
      e.printStackTrace();
    }
    return null;
  }

  public static void saveAction(Action action) throws Exception {
    String targetPath = String.format(PLUGIN_ACTION_JSON, action.getActionId());
    writeToFile(action, targetPath);
  }

  public static void writeToFile(Object data, String filename) throws Exception {
    System.out.println("writing...  " + filename);
    File targetFile = new File(filename);
    targetFile.getParentFile().mkdirs();
    targetFile.createNewFile();
    Writer writer = null;
    try {
      writer = new FileWriter(targetFile);
      Gson gson = new GsonBuilder().setPrettyPrinting().create();
      gson.toJson(data, writer);
    } catch (Exception e) {
      e.printStackTrace();
    } finally {
      writer.close();
    }
  }

}
