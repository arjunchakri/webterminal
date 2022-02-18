package com.sherlock.webterminal.logreader.services.tail;

import java.io.File;

import com.sherlock.webterminal.spring.core.socket.LogSocketMessenger;
import org.apache.commons.io.input.Tailer;
import org.apache.commons.io.input.TailerListener;

public class CustomTailerService {
 
  public static void main(String[] args) {
    
  }
  
  public static void tailFile(String filePath) {
    Tailer tailer = new Tailer(new File(filePath), new TailerListener() {
      
      @Override
      public void init(Tailer tailer) {
        // TODO Auto-generated method stub
        
      }
      
      @Override
      public void handle(Exception ex) {
        // TODO Auto-generated method stub
      }
      
      @Override
      public void handle(String line) {
        System.out.println(line);
        LogSocketMessenger.sendMessage(line);
      }
      
      @Override
      public void fileRotated() {
        // TODO Auto-generated method stub
        
      }
      
      @Override
      public void fileNotFound() {
        // TODO Auto-generated method stub
        
      }
    });
    tailer.run();
  }
  
}
