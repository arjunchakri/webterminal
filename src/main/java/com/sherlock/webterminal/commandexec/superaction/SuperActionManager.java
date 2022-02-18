package com.sherlock.webterminal.commandexec.superaction;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.gson.Gson;
import com.sherlock.webterminal.commandexec.CommandExecutionManager;
import com.sherlock.webterminal.commandexec.data.Action;
import com.sherlock.webterminal.commandexec.superaction.data.SuperActionInput;
import com.sherlock.webterminal.commandexec.superaction.data.SuperActionPackage;
import com.sherlock.webterminal.core.workspace.ActionsManager;

public class SuperActionManager {

  public static void main(String[] args) throws Exception {

    String superActionID = "AAsuperaction";

    triggerSuperAction(superActionID, "", null);

//    SuperActionPackage actionPackage =
//        new SuperActionPackage("Installer - Bundle installation", "", "PACKAGE=HB6.7.0.0,TEAMS_WEBHOOK_URL=");
//
//    SuperActionInput initializeSuperAction = initializeSuperAction(actionPackage);

//    CommandExecutionManager.executeCommand(actionId, command, action.getWorkspace(), contextVariablesJson, null);

//    Action action = ActionsManager.getAction(actionId);
//    
//    action.getCommand();

  }

  public static void triggerSuperAction(String superActionID, String script_inputs, String runnerKey) throws Exception {
    List<SuperActionInput> superActionInputs = new ArrayList<SuperActionInput>();
    List<SuperActionPackage> superActions = ActionsManager.getSuperAction(superActionID);

    for (SuperActionPackage superActionPackage : superActions) {
      System.out.println(" ---- Initiazing super actions " + superActionPackage.getActionId());
      SuperActionInput initializeSuperAction = initializeSuperAction(superActionPackage);
      superActionInputs.add(initializeSuperAction);
    }

    System.out.println(" Executing actions squentially ");

    for (SuperActionInput eachActionInput : superActionInputs) {
      CommandExecutionManager.executeAction(eachActionInput.getActionId(), eachActionInput.getCommand(),
          eachActionInput.getWorkspace(), eachActionInput.getScript_inputs(), runnerKey);
    }
  }

  public static List<SuperActionInput> initializeSuperAction(List<SuperActionPackage> actionPackages) throws Exception {
    List<SuperActionInput> actionInputs = new ArrayList<SuperActionInput>();
    for (SuperActionPackage superActionPackage : actionPackages) {
      actionInputs.add(initializeSuperAction(superActionPackage));
    }
    return actionInputs;
  }

  public static SuperActionInput initializeSuperAction(SuperActionPackage actionPackage) throws Exception {
    String actionId = actionPackage.getActionId();
    String commandArgs = actionPackage.getCommandVars();//"bundle=abc,key22=value22";
    String contextArgs = actionPackage.getContextVars();

    Action action = ActionsManager.getAction(actionId);

    if (action == null) {
      throw new Exception("Action with " + actionId + " does not exist.");
    }

    String command = action.getCommand(); //"cmd $(bundle) $(key22) @(test23)";
    if (!commandArgs.isEmpty()) {
      List<String> commandArgsList = Arrays.asList(commandArgs.split(","));
      for (String eachCommandArg : commandArgsList) {
        String[] cmdPieces = eachCommandArg.trim().split("=", -1);
        String cmdArgKey = cmdPieces[0].trim();
        String cmdArgValue = cmdPieces[1].trim();

        String cmdArgFullPattern = "$(" + cmdArgKey + ")";
        if (command.contains(cmdArgFullPattern)) {
          command = command.replace(cmdArgFullPattern, cmdArgValue);
        }
//        else {
//          throw new Exception("The command argument " + cmdArgFullPattern + " is not found in command " + command);
//        }
      }
    }

    Map<String, String> contextVariables = new HashMap<String, String>();

    if (!contextArgs.isEmpty()) {

      List<String> contextArgsList = Arrays.asList(contextArgs.split(","));

      for (String eachContextArg : contextArgsList) {
        String[] pieces = eachContextArg.trim().split("=", -1);
        String argKey = pieces[0].trim();
        String argValue = pieces[1].trim();

        String cmdContextFullPattern = "@(" + argKey + ")";
        if (command.contains(cmdContextFullPattern)) {
          command = command.replace(cmdContextFullPattern, "");
          contextVariables.put(argKey, argValue);
        }
//        else {
//          throw new Exception("The command argument " + cmdContextFullPattern + " is not found in command " + command);
//        }
      }
    }
    System.out.println(contextVariables);

    System.out.println(command);

    String contextVariablesJson = new Gson().toJson(contextVariables);

    return new SuperActionInput(actionId, command, action.getWorkspace(), contextVariablesJson);
  }

}
