package com.sherlock.webterminal.logreader.services.directory;

import java.io.File;
import java.lang.reflect.Type;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import com.sherlock.webterminal.core.workspace.ActionsManager;
import com.sherlock.webterminal.logreader.configuration.TempConstants;
import com.sherlock.webterminal.logreader.services.directory.data.DirectoryContents;
import com.sherlock.webterminal.logreader.services.directory.data.DirectoryInputBean;
import com.sherlock.webterminal.logreader.services.directory.data.FileMetadata;
import org.apache.commons.io.FileUtils;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

public class FileNamesProvider {
  public static void main(String[] args) throws Exception {
    getFilesList(TempConstants.DIRS_TO_READ);
  }

  public static String getCurrentDir() {

    String currentDir = System.getProperty("user.home");

    File cachedDirFile = new File(ActionsManager.CURRENT_DIR_CONFIGURATION);

    try {
      if (cachedDirFile.exists()) {
        String cachedDir = FileUtils.readFileToString(cachedDirFile, Charset.defaultCharset());
        if (new File(cachedDir).exists()) {
          currentDir = cachedDir;
        }

      }

    } catch (Exception e) {
      e.printStackTrace();
      FileUtils.deleteQuietly(cachedDirFile);
      currentDir = System.getProperty("user.home");
    }

    return currentDir.replace("\\", "/");
  }

  public static void saveDirToCache(String path) {
    try {
      File cachedDirFile = new File(ActionsManager.CURRENT_DIR_CONFIGURATION);

      FileUtils.writeStringToFile(cachedDirFile, path, Charset.defaultCharset());
    } catch (Exception e) {
      e.printStackTrace();

    }
  }

  /**
   * 
   * path -> null, displayname -> null => gets current list
   * path not null, displayname not null => adds to current list
   * 
   * @param path
   * @param displayName
   * @return
   * @throws Exception
   */
  public static List<DirectoryInputBean> getFolderNames(String path, String displayName) throws Exception {

    String configurationFilePath = ActionsManager.DIR_CONFIGURATION_JSON;
    File configurationFile = new File(configurationFilePath);
    List<DirectoryInputBean> dirInputs = new ArrayList<DirectoryInputBean>();
    Gson gson = new Gson();

    if (configurationFile.exists()) {

      String configuredDirStr = FileUtils.readFileToString(configurationFile, Charset.defaultCharset());
      Type type = new TypeToken<List<DirectoryInputBean>>() {
      }.getType();
      dirInputs = gson.fromJson(configuredDirStr, type);
    } else {
      boolean isWindows = System.getProperty("os.name").toLowerCase().contains("windows");
      dirInputs.addAll((isWindows) ? (TempConstants.DIRS_TO_READ) : (TempConstants.DIRS_TO_READ_LINUX));
    }

    if (path != null && displayName != null) {
      dirInputs.add(new DirectoryInputBean(displayName, path));
      ActionsManager.writeToFile(dirInputs, ActionsManager.DIR_CONFIGURATION_JSON);
    }

    return (dirInputs);
  }

  public static void clearFavDirs() {
    try {
      ActionsManager.writeToFile(new ArrayList<DirectoryInputBean>(), ActionsManager.DIR_CONFIGURATION_JSON);
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  public static List<DirectoryContents> getFilesList() throws Exception {
    List<DirectoryContents> resultantContents = new ArrayList<DirectoryContents>();

    String configurationFilePath = ActionsManager.DIR_CONFIGURATION_JSON;
    File configurationFile = new File(configurationFilePath);
    if (configurationFile.exists()) {

      String configuredDirStr = FileUtils.readFileToString(configurationFile, Charset.defaultCharset());
      Gson gson = new Gson();
      Type type = new TypeToken<List<DirectoryInputBean>>() {
      }.getType();
      List<DirectoryInputBean> dirInputs = gson.fromJson(configuredDirStr, type);
      resultantContents = getFilesList(dirInputs);
    } else {

      boolean isWindows = System.getProperty("os.name").toLowerCase().contains("windows");
      resultantContents =
          (isWindows) ? getFilesList(TempConstants.DIRS_TO_READ) : getFilesList(TempConstants.DIRS_TO_READ_LINUX);
    }

    return (resultantContents);
  }

  public static DirectoryContents getFolderContents(String path) throws Exception {
    File directoryFile = new File(path);
    if (!directoryFile.isDirectory()) {
      throw new IllegalArgumentException(directoryFile.toString() + " is not a valid directory.");
    }

    List<FileMetadata> filesList = new ArrayList<FileMetadata>();
    List<FileMetadata> dirList = new ArrayList<FileMetadata>();

    File[] listFiles = directoryFile.listFiles();
    for (File eachFile : listFiles) {
      if (eachFile.isFile() && eachFile.canRead()) {
        System.out.println(eachFile.getName());
        System.out.println(eachFile.lastModified());

        FileMetadata metadata = new FileMetadata(eachFile.getName(), eachFile.lastModified(), eachFile.length());
        filesList.add(metadata);
      } else if (eachFile.isDirectory()) {
        FileMetadata dirMetadata =
            new FileMetadata(eachFile.getName(), eachFile.lastModified(), eachFile.length(), true);
        dirList.add(dirMetadata);
      }
    }

    Collections.sort(filesList, new Comparator<FileMetadata>() {
      @Override
      public int compare(FileMetadata o1, FileMetadata o2) {
        return (-1) * Long.compare(o1.getLastModified(), o2.getLastModified());
      }
    });

    Collections.sort(dirList, new Comparator<FileMetadata>() {
      @Override
      public int compare(FileMetadata o1, FileMetadata o2) {
        return o1.getName().toLowerCase().compareTo(o2.getName().toLowerCase());
      }
    });

    List<FileMetadata> allContents = new ArrayList<FileMetadata>();
    allContents.addAll(dirList);
    allContents.addAll(filesList);

    return new DirectoryContents(allContents, directoryFile.getName(), directoryFile.getPath().replace("\\", "/"));

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
