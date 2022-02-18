package com.sherlock.webterminal.logdownload.processor;

import java.io.File;

import com.sherlock.webterminal.core.workspace.ActionsManager;
import com.sherlock.webterminal.logdownload.LogDownloadRequestProcessor;
import org.apache.commons.io.FileUtils;

public class LogDownloadManager {

  public static File getZippedFile(File logFilepath) throws Exception {
    File tempDirWorkspace = new File(ActionsManager.DIR_TEMP_DIRECTORY);

    File tempDir = new File(tempDirWorkspace, System.currentTimeMillis() + "");

    File downloadFile = logFilepath;
    File downloadFileCopy = new File(tempDir, downloadFile.getName());

    File zippedFile = new File(tempDirWorkspace, downloadFile.getName() + "-" + System.currentTimeMillis() + ".zip");

    //COPY TO WS
    long t1 = System.currentTimeMillis();
    System.out.println("Copying required file...");

    LogDownloadRequestProcessor.sendDisplayMessage("Copying required file(s)...");

    
    if (downloadFile.isFile()) {
      FileUtils.copyFile(downloadFile, downloadFileCopy);

    } else if (downloadFile.isDirectory()) {
      FileUtils.copyDirectory(downloadFile, downloadFileCopy);
    }


    long t2 = System.currentTimeMillis();
    long deltaCopyTime = ((t2 - t1) / 1000);
    LogDownloadRequestProcessor.sendDisplayMessage("Time taken for copying: " + deltaCopyTime + " seconds.");

    System.out.println("Time taken for copying: " + deltaCopyTime + " seconds.");

    //ZIP FILE
    System.out.println("Zipping file on server...");
    LogDownloadRequestProcessor.sendDisplayMessage("Zipping file on server...");

    String directory = downloadFileCopy.getParent();
    ZipFileService.zipDirectory(zippedFile.getAbsolutePath(), directory);

    try {
      System.out.println("Deleting file..");
      FileUtils.forceDelete(downloadFileCopy);
    } catch (Exception e) {
      e.printStackTrace();
    }

    long t3 = System.currentTimeMillis();
    long deltaZippingTime = ((t3 - t2) / 1000);

    LogDownloadRequestProcessor.sendDisplayMessage("Time taken for zipping: " + deltaZippingTime + " seconds.");
    System.out.println("Time taken for copying: " + deltaZippingTime + " seconds.");

    return zippedFile;

  }

}
