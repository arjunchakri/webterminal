package com.sherlock.webterminal.logreader.services;

import com.sherlock.webterminal.logreader.services.directory.data.DirectoryInputBean;

import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.List;

public class RandomAccessFileAPI {

  public static void main(String[] args) throws Exception {
    readLastLines("C:\\Users\\anunugonda\\Documents\\InstallationTiming\\core-6.7.1\\core-6.7.1.log");
  }

  public static List<String> readLastLines(String fileName) throws Exception {
    List<String> linesCollection = new ArrayList<String>();

    RandomAccessFile raf = new RandomAccessFile(fileName, "r");
    ArrayList<Long> arrayList = new ArrayList<Long>();
    String cur_line = "";
    while ((cur_line = raf.readLine()) != null) {
      arrayList.add(raf.getFilePointer());
    }

    raf.seek(arrayList.get(31));
    System.out.println(raf.readLine());
    raf.close();

    return linesCollection;
  }

}
