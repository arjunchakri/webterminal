package com.sherlock.webterminal.logdownload;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

public class DownloadFileRepository {

  private static Map<String, File> downloadFileRepository = new HashMap<String, File>();

  public static void addFile(File downloadResource, String downloadKey) throws Exception {
    downloadFileRepository.put(downloadKey, downloadResource);
  }

  public static File getFile(String downloadKey) throws Exception {
    return downloadFileRepository.get(downloadKey);
  }

}
