package com.sherlock.webterminal.formulas;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardWatchEventKinds;
import java.nio.file.WatchEvent;
import java.nio.file.WatchEvent.Kind;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.concurrent.ConcurrentHashMap;

import com.sherlock.webterminal.core.workspace.FormulaManager;
import com.sherlock.webterminal.formulas.data.CustomFormula;
import com.sherlock.webterminal.spring.core.socket.LogSocketMessenger;
import org.springframework.web.socket.WebSocketSession;

import com.google.gson.Gson;
import com.sherlock.webterminal.formulas.data.FormulaWebInput;

public class CustomFormulaExecutor {

  static volatile Map<String, WebSocketSession> listenerSessions = new ConcurrentHashMap<String, WebSocketSession>();
  static volatile Map<String, FormulaWebInput> cachedResults = new HashMap<String, FormulaWebInput>();

  public static List<Timer> formulaeTimers = new ArrayList<Timer>();

  static {
    try {
      setupFormulaeCrons();
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  public static void connectClient(WebSocketSession session) {

    String id = session.getId();
    listenerSessions.put(id, session);
    System.out.println("connectedClient -> " + id + " || numofclients=" + listenerSessions.size());

    for (String eachKey : cachedResults.keySet()) {
      notifyClients(Arrays.asList(session), cachedResults.get(eachKey));
    }

  }

  public static void disconnectClient(WebSocketSession session) {

    String id = session.getId();

    if (id == null) {
      return;
    }

    listenerSessions.remove(id);
    System.out.println("disconnect -> " + id + " || numofclients=" + listenerSessions.size());

  }

  public static void notifyClients(Collection<WebSocketSession> webSocketSessions, FormulaWebInput webInput) {
    for (WebSocketSession eachSession : webSocketSessions) {
      Gson gson = new Gson();

      LogSocketMessenger.sendMessage(gson.toJson(webInput), eachSession);

    }
  }

  public static void handleCronEvent(CustomFormula formula) {

    if (listenerSessions.size() <= 0) {
      //Do nothing if no listeners are there
      return;
    }

    try {
      String timeStamp = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS").format(Calendar.getInstance().getTime());

      System.out.println("  -----------> " + formula.getName() + " at " + timeStamp);

      invokeFormulaAsync(formula);

    } catch (Exception e) {
      e.printStackTrace();
    }

  }

  public static FormulaWebInput invokeFormula(CustomFormula formula) throws Exception {
    FormulaWebInput formulaOutput = FormulaManager.executeFormula(formula);
    String formulaName = formula.getName();

    cachedResults.put(formulaName, formulaOutput);

    Collection<WebSocketSession> webSocketSessions = listenerSessions.values();
    notifyClients(webSocketSessions, formulaOutput);
    //System.out.println(" output ------------------------> " + formulaOutput);

    return formulaOutput;
  }

  public static void setupFormulaeCrons() throws Exception {

    System.out.println("setupFormulaeCrons - setting up crons");

    List<CustomFormula> allFormulae = FormulaManager.getAllFormulae();

    cleanupCrons();

    for (CustomFormula customFormula : allFormulae) {

      try {
        if (customFormula.getFrequency() > 0) {
          CronService service = new CronService(customFormula);
          service.startCron(generateTimer());
        }

        invokeFormulaAsync(customFormula);
      } catch (Exception e) {
        e.printStackTrace();
      }

    }

    //startFileWatcher(FormulaManager.FORMULAE_WORKSPACE);

  }

  private static void invokeFormulaAsync(final CustomFormula customFormula) {
    new Thread(new Runnable() {

      @Override
      public void run() {
        try {
          invokeFormula(customFormula);
        } catch (Exception e) {
          e.printStackTrace();
        }
      }
    }).start();
  }

  private static Timer generateTimer() {
    Timer timer = new Timer();
    formulaeTimers.add(timer);
    return timer;
  }

  private static void cleanupCrons() {
    try {

      for (Timer formulaeTimer : formulaeTimers) {
        formulaeTimer.cancel();
      }

    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  public static void startFileWatcher(String directoryPath) {
    Path myDir = Paths.get(directoryPath);

    try {
      WatchService watcher = myDir.getFileSystem().newWatchService();

      Kind[] kinds = { StandardWatchEventKinds.ENTRY_CREATE, StandardWatchEventKinds.ENTRY_DELETE,
          StandardWatchEventKinds.ENTRY_MODIFY };

      myDir.register(watcher, kinds);

      WatchKey watckKey = watcher.take();

      List<WatchEvent<?>> events = watckKey.pollEvents();
      for (WatchEvent event : events) {

//        switch (event.kind()) {
//          case StandardWatchEventKinds.ENTRY_CREATE:
//          case StandardWatchEventKinds.ENTRY_DELETE:
//          case StandardWatchEventKinds.ENTRY_MODIFY:
//            System.out.println("Event - " + event.context().toString());
//            setupFormulaeCrons();
//
//          default:
//            break;
//        }

        if (event.kind() == StandardWatchEventKinds.ENTRY_CREATE) {
          System.out.println("Created: " + event.context().toString());
          setupFormulaeCrons();
        }
        if (event.kind() == StandardWatchEventKinds.ENTRY_DELETE) {
          System.out.println("Delete: " + event.context().toString());
          setupFormulaeCrons();
        }
        if (event.kind() == StandardWatchEventKinds.ENTRY_MODIFY) {
          System.out.println("Modify: " + event.context().toString());
          setupFormulaeCrons();
        }
      }

    } catch (Exception e) {
      System.out.println("Error: " + e.toString());
    }

    // TODO - severe - make this class nonstatic and remove recursion
    startFileWatcher(directoryPath);
  }

  public static void main(String[] args) throws Exception {

    listenerSessions.put("test", null);

    setupFormulaeCrons();

  }

}
