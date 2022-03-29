package com.sherlock.webterminal.commandexec;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.net.URL;
import java.nio.file.Files;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import com.sherlock.webterminal.commandexec.custom.CustomExecutionManager;
import com.sherlock.webterminal.commandexec.data.Action;
import com.sherlock.webterminal.commandexec.data.ExecutionResponse;
import com.sherlock.webterminal.commandexec.executor.TerminalExecutorRepository;
import com.sherlock.webterminal.commandexec.executor.WebListenersHandler;
import com.sherlock.webterminal.core.workspace.ActionsManager;
import com.sherlock.webterminal.core.workspace.WorkspaceConstants;
import com.sherlock.webterminal.emails.EmailMessenger;
import com.sherlock.webterminal.teamsapi.TeamsMessageData;
import com.sherlock.webterminal.teamsapi.TeamsMessengerHandler;
import com.sherlock.webterminal.utils.TimeFormatUtil;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.springframework.web.socket.WebSocketSession;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.sherlock.webterminal.commandexec.exception.WebListenterException;
import com.sherlock.webterminal.commandexec.superaction.SuperActionManager;
import com.sherlock.webterminal.commandexec.superaction.data.SuperActionPackage;

public class CommandExecutionManager implements Runnable {

  String actionId;
  String command;
  String workspace;
  String script_inputs;
  String runnerKey;

  public CommandExecutionManager(String actionId, String command, String workspace, String script_inputs,
      String runnerKey) {
    super();
    this.actionId = actionId;
    this.command = command;
    this.workspace = workspace;
    this.script_inputs = script_inputs;
    this.runnerKey = runnerKey;
  }

  @Override
  public void run() {
    try {

      executeAction(actionId, command, workspace, script_inputs, runnerKey);

    } catch (WebListenterException e) {
      e.printStackTrace();
    }

    TerminalExecutorRepository.removeProcess(runnerKey);
  }

  public static void main(String[] args) throws Exception {
    executeCommand("jps");
  }

  public static String executeCommand(String actionId) throws Exception {
    try {
      executeCommand(ActionsManager.getAction(actionId));
      return "OK";
    } catch (Exception e) {
      return "ERROR" + e.getMessage();
    }
  }

  public static ExecutionResponse executeCommand(Action action) throws Exception {

    String actionId = action.getActionId();
    String command = action.getCommand();
    String workspace = action.getWorkspace();
    return executeCommand(actionId, command, workspace, "", null);

  }

  public static ExecutionResponse executeCommand(String actionId, String command, String workspace,
      String script_inputs, WebSocketSession session) throws Exception {

    String runnerKey = UUID.randomUUID().toString();

    return executeAction(actionId, command, workspace, script_inputs, runnerKey);

  }

  public static ExecutionResponse executeAction(String actionId, String command, String workspace, String script_inputs,
      String runnerKey) throws WebListenterException {
    ExecutionResponse executionResponse = null;
    WebListenersHandler
        .sendMessage("<span style='color:grey'>[ Attempting to execute action: " + actionId + " ]</span>", runnerKey);
    try {
      List<SuperActionPackage> superAction = ActionsManager.getSuperAction(actionId);
      int execResponse = 0;
      long execInit = System.currentTimeMillis();
      TeamsMessageData teamsNotifier = buildTeamsNotifier(script_inputs, runnerKey);

      if (superAction != null) {
        //check if a superaction 
        SuperActionManager.triggerSuperAction(actionId, script_inputs, runnerKey);

      } else {

        System.out.println("Attempting to execute action: " + actionId);

        File contextDirectory = new File(workspace);
        String pluginSpace = String.format(ActionsManager.PLUGIN_ACTION_WS, actionId);

        if (workspace.isEmpty()) {
          String currentTime = new SimpleDateFormat("yyyyMMddHHmmss.SSS").format(new Date());
          String workspaceFolderName = currentTime + "-" + actionId;
          workspaceFolderName = workspaceFolderName.replaceAll("\\s+", "_");
          contextDirectory = new File(WorkspaceConstants.APP_TEMP_WORKSPACE, workspaceFolderName);
          contextDirectory.mkdirs();
          WebListenersHandler
              .sendMessage("<span style='color:grey'>[ Workspace configured as . Creating a temp directory - "
                  + contextDirectory + " ] </span>", runnerKey);
          System.out.println("Workspace seems to be empty. Creating a temp directory - " + contextDirectory);
          replicateDirContents(pluginSpace, contextDirectory);
        }
        processRequirementsFromResourcesFile(pluginSpace, contextDirectory.getAbsolutePath(), runnerKey);

//      processRequirementsFromResources(actionId, contextDirectory.getAbsolutePath(), session);
        //Copy all files from pluginspace to contextdir

        customActionPre(pluginSpace, contextDirectory.getAbsolutePath(), script_inputs, runnerKey);

        System.out.println("Running command > " + command + " in directory > " + contextDirectory.getAbsolutePath());

        WebListenersHandler.sendMessage("<span style='color:grey'>[ Running command > " + command + " > "
            + contextDirectory.getAbsolutePath() + " ] </span>", runnerKey);
        teamsNotifier = buildTeamsNotifier(pluginSpace, contextDirectory, runnerKey);

        executionResponse = execute(command, contextDirectory, teamsNotifier, runnerKey);
        execResponse = executionResponse.getReturnCode();
      }

      long execEnd = System.currentTimeMillis();
      String executionTime = TimeFormatUtil.formatDuration(execEnd - execInit);
      String configuredEmails = detectConfiguredEmails(script_inputs);
      if (configuredEmails != null) {
        EmailMessenger.sendEmail(actionId, command, script_inputs, executionTime, execResponse + "", configuredEmails);
      }

      if (teamsNotifier != null) {
        String buildEmailContent =
            EmailMessenger.buildEmailContent(actionId, command, script_inputs, executionTime, execResponse + "");
        TeamsMessengerHandler.handle(teamsNotifier, buildEmailContent);
      }

      WebListenersHandler.sendMessage(
          "<span style='color:green'>[ Command execution completed - took " + executionTime + "] </span>", runnerKey);

    } catch (Exception e) {
      e.printStackTrace();
      System.out.println("Failed to execute action with actionId: " + actionId);
      WebListenersHandler.sendMessage(
          "<span style='color:red'>[ FAILED to execute action with actionId: " + actionId + " ] </span>", runnerKey);
    }

    return executionResponse;
  }

  public static void processRequirementsFromResourcesFile(String pluginSpace, String executionWorkspace,
      String runnerKey) throws WebListenterException {
    try {

      File resourcesFile = new File(pluginSpace, "requirements.list");

      if (!resourcesFile.exists()) {
        return;
      }

      if (!resourcesFile.canRead()) {
        return;
      }

      WebListenersHandler.sendMessage("<span style='color:grey'>[ Resources for action, downloading resources ]</span>",
          runnerKey);
      List<String> requirementsFileLines = Files.readAllLines(resourcesFile.toPath());

      for (String eachRequirement : requirementsFileLines) {
        eachRequirement = eachRequirement.trim();
        if (!eachRequirement.isEmpty()) {
          try {
            System.out.println(" -- Downloading " + eachRequirement);
            WebListenersHandler
                .sendMessage("<span style='color:grey'>[ -- Downloading " + eachRequirement + " ]</span>", runnerKey);
            File downloadFile = new File(executionWorkspace, FilenameUtils.getName(eachRequirement));
            FileUtils.copyURLToFile(new URL(eachRequirement), downloadFile);
            downloadFile.setExecutable(true);
            downloadFile.setReadable(true);
            downloadFile.setWritable(true);
          } catch (Exception e) {
            System.out.println();
            WebListenersHandler.sendMessage(
                "<span style='color:red'>[ Failed to download resource" + eachRequirement + " ]</span>", runnerKey);
            throw new Exception("Failed to download resource " + eachRequirement);

          }
        }
      }

    } catch (Exception e) {
      e.printStackTrace();
      System.out.println("Failed to process requirements. ");
      WebListenersHandler.sendMessage("<span style='color:red'>[ Failed to process requirements.  ] </span>",
          runnerKey);
    }
  }

  private static void replicateDirContents(String pluginSpace, File contextDirectory) throws Exception {
    try {
      FileUtils.copyDirectory(new File(pluginSpace), contextDirectory);
    } catch (Exception e) {
      e.printStackTrace();
      throw new Exception("Error while copying directory for pre-setup -> " + e.getMessage());
    }
  }

  private static String detectConfiguredEmails(String customVars) {
    try {
      Gson gson = new Gson();
      Type type = new TypeToken<Map<String, String>>() {
      }.getType();
      Map<String, String> inputMap = gson.fromJson(customVars, type);
      if (inputMap == null) {
        return null;
      }
      if (!inputMap.isEmpty()) {
        String email = inputMap.get("WEBTERMINAL_EMAIL_NOTIFICATION");
        if (email == null || email.isEmpty()) {
          return null;
        }

        return email;
      }

    } catch (Exception e) {
      e.printStackTrace();
      return null;
    }
    return null;

  }

  private static TeamsMessageData buildTeamsNotifier(String customVars, String runnerKey) throws Exception {
    try {

      Gson gson = new Gson();
      Type type = new TypeToken<Map<String, String>>() {
      }.getType();
      Map<String, String> inputMap = gson.fromJson(customVars, type);
      if (inputMap == null) {
        return null;
      }
      if (!inputMap.isEmpty()) {
        String webhookURL = inputMap.get("WEBTERMINAL_TEAMS_NOTIFICATIONS_WEBHOOK_URL");
        if (webhookURL == null || webhookURL.isEmpty()) {
          return null;
        }

        WebListenersHandler
            .sendMessage("<span style='color:grey'>[ Loaded teams notification pattern with EMPTY pattern, url = "
                + webhookURL + " ] </span>", runnerKey);

        return new TeamsMessageData(webhookURL, new ArrayList<String>());
      }

    } catch (Exception e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
      return null;
    }
    return null;
  }

  private static TeamsMessageData buildTeamsNotifier(String pluginSpace, File contextDirectory, String runnerKey)
      throws Exception {
    try {
      File terminalPropFile = new File(contextDirectory, "webterminalplugin.properties");
      if (!terminalPropFile.exists()) {
        return null;
      }

      Properties webterminalProperties = new Properties();

      webterminalProperties.load(new FileInputStream(terminalPropFile));

      String teamsWebhook = webterminalProperties.getProperty("WEBTERMINAL_TEAMS_NOTIFICATIONS_WEBHOOK_URL");
      if (teamsWebhook == null || teamsWebhook.isEmpty()) {
        return null;
      }

      File mileStonesFile = new File(pluginSpace, "milestones.list");
      if (!mileStonesFile.exists()) {
        WebListenersHandler
            .sendMessage("<span style='color:grey'>[ Loaded teams notification pattern with EMPTY pattern, url = "
                + teamsWebhook + " ] </span>", runnerKey);
        return new TeamsMessageData(teamsWebhook, new ArrayList<String>());
      }

      List<String> patternLines = FileUtils.readLines(mileStonesFile);

      if (patternLines.isEmpty()) {
        return null;
      }

      List<String> inputPatterns = new ArrayList<String>();
      for (String eachPattern : patternLines) {
        if (!eachPattern.isEmpty()) {
          inputPatterns.add(eachPattern);
        }
      }

      WebListenersHandler.sendMessage("<span style='color:grey'>[ Loaded teams notification pattern with "
          + inputPatterns.toString() + ", url = " + teamsWebhook + " ] </span>", runnerKey);
      return new TeamsMessageData(teamsWebhook, inputPatterns);
    } catch (Exception e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
      return null;
    }

  }

  private static void customActionPre(String pluginSpace, String absolutePath, String explicitVariables,
      String runnerKey) throws Exception {
    try {
      String executableName = "Pre.java";

      File javaExecutable = new File(pluginSpace, executableName);
      File executableLibs = new File(pluginSpace, "libs");

      if (!javaExecutable.exists()) {
        return;
      }

      if (!javaExecutable.canRead()) {
        return;
      }

      WebListenersHandler.sendMessage("<span style='color:grey'>[ Executable found ]</span>", runnerKey);

      File finalExecutable = new File(absolutePath, javaExecutable.getName());
      File finalExecutableLibs = new File(absolutePath, "libs");

      FileUtils.copyFile(javaExecutable, finalExecutable);
      if (executableLibs.exists() && executableLibs.isDirectory()) {
        FileUtils.copyDirectory(executableLibs, finalExecutableLibs);
      }

      File parentFile = finalExecutable.getParentFile();

      Gson gson = new Gson();
      Type type = new TypeToken<Map<String, String>>() {
      }.getType();
      Map<String, String> inputMap = gson.fromJson(explicitVariables, type);
      if (!inputMap.isEmpty()) {
        WebListenersHandler
            .sendMessage("<span style='color:grey'>[ Loading properties to file " + inputMap + " ]</span>", runnerKey);

        Properties pluginProperties = new Properties();
//        inputMap.forEach((key, value) -> pluginProperties.put(key, value));
        for (Map.Entry<String,String> entry : inputMap.entrySet()) {
          pluginProperties.put(entry.getKey(), entry.getValue());
        }
        File pluginPropertiesFile = new File(parentFile, "webterminalplugin.properties");
        pluginPropertiesFile.createNewFile();
        FileOutputStream installerPropertiesStream = new FileOutputStream(pluginPropertiesFile);
        pluginProperties.store(installerPropertiesStream, "pluginPropertiesFile");
        installerPropertiesStream.close();

        WebListenersHandler.sendMessage("<span style='color:grey'>[ Loading successful ]</span>", runnerKey);

      }

      CustomExecutionManager.compileFile(parentFile, executableName, runnerKey);

    } catch (Exception e) {
      e.printStackTrace();
      System.out.println("Failed to process requirements. ");
      WebListenersHandler.sendMessage("<span style='color:red'>[ Failed to process requirements.  ] </span>",
          runnerKey);
      throw e;
    }

  }

  public static ExecutionResponse execute(String commandExec, File contextDirectory, TeamsMessageData teamsNotifier,
      String runnerKey) throws Exception {
    List<String> consoleLogs = new ArrayList<String>();

    boolean notificationEnabled = false;
    if (teamsNotifier != null) {
      notificationEnabled = true;
    }

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
//      System.out.println(line);
      consoleLogs.add(line);
      WebListenersHandler.sendMessage(line, runnerKey);
      if (notificationEnabled) {
        TeamsMessengerHandler.handle(teamsNotifier, line);
      }
    }
    BufferedReader errorBufferedStream = new BufferedReader(new InputStreamReader(p.getErrorStream()));
    while ((line = errorBufferedStream.readLine()) != null) {
      System.out.println("ERROR>> " + line);
      consoleLogs.add(line);
      WebListenersHandler.sendMessage("<span style='color:red'>" + line + "</span>", runnerKey);

    }

    System.out.println("Execution done");

    p.waitFor(300, TimeUnit.SECONDS);

    int processCode = 0;
    try {
      processCode = p.exitValue();
    } catch (Exception e) {
      e.printStackTrace();
    }
    WebListenersHandler.sendMessage("<span style='color:grey'>[ Process return code: " + processCode + " ]</span>",
        runnerKey);

    return new ExecutionResponse(processCode, consoleLogs);
  }

  //
//public static void processRequirementsFromResources(String actionId, String executionWorkspace, WebSocketSession session) {
//  try {
//
//    Action action = ActionsManager.getAction(actionId);
//    String resources = action.getResources();
//    if (resources == null) {
//      return;
//    }
//
//    if (resources.isEmpty()) {
//      return;
//    }
//
//    resources = resources.trim();
//    LogSocketMessenger.sendMessage("<span style='color:grey'>[ Resources for action, downloading resources ]</span>",
//        session);
//    String[] requirementsFileLines = resources.split(",");
//
//    for (String eachRequirement : requirementsFileLines) {
//      eachRequirement = eachRequirement.trim();
//      if (!eachRequirement.isEmpty()) {
//        try {
//          LogSocketMessenger.sendMessage("<span style='color:grey'>[ -- Downloading " + eachRequirement + " ]</span>",
//              session);
//          File downloadFile = new File(executionWorkspace, FilenameUtils.getName(eachRequirement));
//          FileUtils.copyURLToFile(new URL(eachRequirement), downloadFile);
//        } catch (Exception e) {
//          System.out.println();
//          LogSocketMessenger.sendMessage(
//              "<span style='color:red'>[ Failed to download resource" + eachRequirement + " ]</span>", session);
//        }
//      }
//    }
//
//  } catch (Exception e) {
//    e.printStackTrace();
//    System.out.println("Failed to process requirements. ");
//    LogSocketMessenger.sendMessage("<span style='color:red'>[ Failed to process requirements.  ] </span>", session);
//  }
//}

}
