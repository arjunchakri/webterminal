package com.sherlock.webterminal.logdownload.processor;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.zip.GZIPOutputStream;

import net.lingala.zip4j.core.ZipFile;
import net.lingala.zip4j.model.ZipParameters;

public class ZipFileService {

  public static void zipDirectory(String zipFileName, String directory) throws Exception {
    ZipFile zipFile = new ZipFile(zipFileName);
    ZipParameters parameters = new ZipParameters();
    parameters.setCompressionLevel(1);
    parameters.setIncludeRootFolder(false);
    zipFile.createZipFileFromFolder(directory, parameters, false, 0L);
  }

  public static void compressGzipFile(String file, String gzipFile) {
    try {
      FileInputStream fis = new FileInputStream(file);
      FileOutputStream fos = new FileOutputStream(gzipFile);
      GZIPOutputStream gzipOS = new GZIPOutputStream(fos);
      byte[] buffer = new byte[1024];
      int len;
      while ((len = fis.read(buffer)) != -1) {
        gzipOS.write(buffer, 0, len);
      }
      //close resources
      gzipOS.close();
      fos.close();
      fis.close();
    } catch (IOException e) {
      e.printStackTrace();
    }

  }
}
