package com.sherlock.webterminal.logreader.services.readerkey;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class ReaderKeyRepository {

  public static volatile List<String> readerKeyCollection = new ArrayList<String>();
  
  public static String generateReaderKey() {
    String readerKey = UUID.randomUUID().toString();
    readerKeyCollection.add(readerKey);
    return readerKey;
  }
  
}
