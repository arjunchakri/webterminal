package com.sherlock.webterminal.filemanager;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardWatchEventKinds;
import java.nio.file.WatchEvent;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import com.sherlock.webterminal.logreader.services.directory.data.DirectoryContents;
import com.sherlock.webterminal.logreader.services.directory.data.DirectoryInputBean;
import com.sherlock.webterminal.logreader.services.directory.data.FileMetadata;
import com.sherlock.webterminal.spring.core.socket.LogSocketMessenger;
import com.sherlock.webterminal.spring.core.socket.folderlistener.data.DirElement;
import org.apache.commons.io.FileUtils;
import org.springframework.web.socket.WebSocketSession;

import com.google.gson.Gson;

public class FileManager {

  public static void startFolderListener(String folderPath, String saveContents, WebSocketSession session) {

    File targetFile = new File(folderPath);

    if (targetFile.isDirectory()) {
      fireChangedEvent(folderPath, session);

      try {
        startFileWatcher(folderPath, session);
      } catch (Exception e) {
        // TODO Auto-generated catch block
        e.printStackTrace();
      }
    } else {
      //FILE

      if (saveContents == null) {
        readFileToEditor(targetFile, session);
      } else {
        writeFileContents(targetFile, saveContents, session);
      }

    }

  }

  public static String backupAndDelete(String targetFilePath) throws IOException {

    File targetFile = new File(targetFilePath);

    File copyFile =
        new File(targetFile.getParentFile(), targetFile.getName() + "." + System.currentTimeMillis() + ".bak");

    FileUtils.copyFile(targetFile, copyFile);
    FileUtils.deleteQuietly(targetFile);

    return "Backed up current file to " + copyFile.getAbsolutePath();
  }

  private static void writeFileContents(File targetFile, String saveContents, WebSocketSession session) {
    try {
      targetFile.createNewFile();
    } catch (IOException e1) {
      // TODO Auto-generated catch block
      e1.printStackTrace();
    }
    PrintWriter out = null;
    try {
      out = new PrintWriter(new FileOutputStream(targetFile, true));
      out.println(saveContents);
    } catch (FileNotFoundException e) {
      e.printStackTrace();
    } finally {
      out.close();
    }
  }

  private static void readFileToEditor(File folderPath, WebSocketSession session) {
    // TODO Change entire API
    BufferedReader br = null;
    try {
      br = new BufferedReader(new FileReader(folderPath));
      String line;
      while ((line = br.readLine()) != null) {
        LogSocketMessenger.sendMessage(line, session);
      }
    } catch (Exception e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    } finally {
      try {
        br.close();
      } catch (IOException e) {
        e.printStackTrace();
      }
    }
    LogSocketMessenger.sendMessage("ENDOFFILEWEBTERMINAL", session);

  }

  public static void startFileWatcher(String directoryPath, WebSocketSession session) {
    Path myDir = Paths.get(directoryPath);

    try {
      WatchService watcher = myDir.getFileSystem().newWatchService();
      myDir.register(watcher, StandardWatchEventKinds.ENTRY_CREATE, StandardWatchEventKinds.ENTRY_DELETE,
          StandardWatchEventKinds.ENTRY_MODIFY);

      WatchKey watckKey = watcher.take();

      List<WatchEvent<?>> events = watckKey.pollEvents();
      for (WatchEvent event : events) {
        if (event.kind() == StandardWatchEventKinds.ENTRY_CREATE) {
          System.out.println("Created: " + event.context().toString());
          fireChangedEvent(directoryPath, session);

        }
        if (event.kind() == StandardWatchEventKinds.ENTRY_DELETE) {
          System.out.println("Delete: " + event.context().toString());
          fireChangedEvent(directoryPath, session);

        }
        if (event.kind() == StandardWatchEventKinds.ENTRY_MODIFY) {
          System.out.println("Modify: " + event.context().toString());
          fireChangedEvent(directoryPath, session);

        }
      }

    } catch (Exception e) {
      System.out.println("Error: " + e.toString());
    }

    // TODO - severe - make this class nonstatic and remove recursion
    startFileWatcher(directoryPath, session);
  }

//  public static void startListening(String directoryPath, WebSocketSession session) throws Exception {
//    File directory = new File(directoryPath);
//    FileAlterationObserver observer = new FileAlterationObserver(directory);
//    observer.addListener(new FileAlterationListener() {
//
//      @Override
//      public void onStop(FileAlterationObserver observer) {
//        // TODO Auto-generated method stub
//
//      }
//
//      @Override
//      public void onStart(FileAlterationObserver observer) {
//        // TODO Auto-generated method stub
//
//      }
//
//      @Override
//      public void onFileDelete(File file) {
//        fireChangedEvent(directoryPath, session);
//
//      }
//
//      @Override
//      public void onFileCreate(File file) {
//        fireChangedEvent(directoryPath, session);
//
//      }
//
//      @Override
//      public void onFileChange(File file) {
//        fireChangedEvent(directoryPath, session);
//
//      }
//
//      @Override
//      public void onDirectoryDelete(File directory) {
//        fireChangedEvent(directoryPath, session);
//
//      }
//
//      @Override
//      public void onDirectoryCreate(File directory) {
//        fireChangedEvent(directoryPath, session);
//
//      }
//
//      @Override
//      public void onDirectoryChange(File directory) {
//        fireChangedEvent(directoryPath, session);
//      }
//    });
//
//    FileAlterationMonitor monitor = new FileAlterationMonitor(3000);
//    monitor.addObserver(observer);
//    monitor.start();
//  }

  public static void fireChangedEvent(String directoryPath, WebSocketSession session) {
    System.out.println("fireChangedEvent");

    List<DirElement> dirContents = getDirContents(directoryPath);
    Gson gson = new Gson();
    LogSocketMessenger.sendMessage(gson.toJson(dirContents), session);
  }

  public static List<DirElement> getDirContents(String directoryPath) {
    List<DirElement> dirElements = new ArrayList<DirElement>();

    File directoryFile = new File(directoryPath);
    File[] listFiles = directoryFile.listFiles();
    for (File eachFile : listFiles) {
      try {
        System.out.println(eachFile.getName());
        System.out.println(eachFile.lastModified());
        String fileType = "";
        if (eachFile.isFile()) {
          fileType = "file";
        } else {
          fileType = "folder";

        }

        dirElements.add(new DirElement(eachFile.getName(), fileType, eachFile.lastModified(), eachFile.length()));
      } catch (Exception e) {
        System.out.println("Exception for " + eachFile.getAbsolutePath());
        e.printStackTrace();
      }
    }

    return dirElements;

  }

  public static List<DirectoryContents> getFilesList(List<DirectoryInputBean> configuredDirectories) throws Exception {
    List<DirectoryContents> resultantContents = new ArrayList<DirectoryContents>();
    for (DirectoryInputBean eachDirectory : configuredDirectories) {
      try {
        File directoryFile = new File(eachDirectory.getPath());
        if (!directoryFile.isDirectory()) {
          throw new IllegalArgumentException(eachDirectory.toString() + " is not a valid directory.");
        }

        List<FileMetadata> metadataList = new ArrayList<FileMetadata>();
        File[] listFiles = directoryFile.listFiles();
        for (File eachFile : listFiles) {
          if (eachFile.isFile() && eachFile.canRead()) {
            System.out.println(eachFile.getName());
            System.out.println(eachFile.lastModified());

            FileMetadata metadata = new FileMetadata(eachFile.getName(), eachFile.lastModified(), eachFile.length());
            metadataList.add(metadata);
          }
        }

        Collections.sort(metadataList, new Comparator<FileMetadata>() {
          @Override
          public int compare(FileMetadata o1, FileMetadata o2) {
            return (-1) * Long.compare(o1.getLastModified(), o2.getLastModified());
          }
        });

        DirectoryContents directory1 =
            new DirectoryContents(metadataList, eachDirectory.getDisplayName(), eachDirectory.getPath());
        resultantContents.add(directory1);
      } catch (Exception e) {
        e.printStackTrace();
      }

    }

    return resultantContents;
  }

}
