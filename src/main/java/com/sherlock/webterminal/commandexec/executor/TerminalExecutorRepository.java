package com.sherlock.webterminal.commandexec.executor;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;

import org.springframework.web.socket.WebSocketSession;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.sherlock.webterminal.commandexec.CommandExecutionManager;

public class TerminalExecutorRepository {

  private static Map<String, WebTerminalProcess> webterminalProcesses =
      new ConcurrentHashMap<String, WebTerminalProcess>();

  public static void addProcess(String payload, WebSocketSession session) {

    //READING INPUTS FROM WEBPAGE
    Gson gson = new Gson();
    Type type = new TypeToken<Map<String, String>>() {
    }.getType();
    Map<String, String> inputMap = gson.fromJson(payload, type);
    String actionId = inputMap.get("actionId");
    String command = inputMap.get("command");
    String workspace = inputMap.get("workspace");
    String script_inputs = inputMap.get("script_inputs");
    String runner_key = inputMap.get("runner_key");

    if (runner_key == null) {
      // NEW PROCESS

      String runnerKey = UUID.randomUUID().toString();

      WebTerminalProcess terminalProcess = new WebTerminalProcess();
      terminalProcess.setProcessService(Executors.newSingleThreadExecutor());
      terminalProcess.setProcessName(actionId);
      terminalProcess.setSocketSessions(new ArrayList<WebSocketSession>(Arrays.asList(session)));
      webterminalProcesses.put(runnerKey, terminalProcess);

      WebListenersHandler.addListener(runnerKey, session);

      //FIRING THE THREAD
      terminalProcess
          .setExecutionManager(new CommandExecutionManager(actionId, command, workspace, script_inputs, runnerKey));
      terminalProcess.getProcessService().execute(terminalProcess.getExecutionManager());

    } else {
      // ADD LISTENER GUY TO LISTENERS
      WebListenersHandler.addListener(runner_key, session);

    }

  }

  public static void removeProcess(String runnerKey) {

    WebTerminalProcess internalProcess = webterminalProcesses.get(runnerKey);
    if (internalProcess == null) {
      return;
    }

    internalProcess.clear();

    webterminalProcesses.remove(runnerKey);

  }

  public static Map<String, String> listProcesses() {

    Map<String, String> processesOutput = new HashMap<String, String>();
    Set<String> processIDs = webterminalProcesses.keySet();

    for (String eachProcessID : processIDs) {
      WebTerminalProcess webTerminalProcess = webterminalProcesses.get(eachProcessID);
      processesOutput.put(eachProcessID, webTerminalProcess.getProcessName());
    }

    return processesOutput;

  }

  public static void setProcess(String runnerKey, Process p) {
    WebTerminalProcess internalProcess = webterminalProcesses.get(runnerKey);
    if (internalProcess == null) {
      //thow exception
    }
    internalProcess.setProcess(p);
  }

}
