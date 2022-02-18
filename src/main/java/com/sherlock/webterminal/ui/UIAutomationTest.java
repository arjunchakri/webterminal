package com.sherlock.webterminal.ui;

import java.awt.AWTEvent;
import java.awt.MouseInfo;
import java.awt.Toolkit;
import java.awt.event.AWTEventListener;

import javax.swing.JFrame;

public class UIAutomationTest {

  public static void main(String[] args) {
    Toolkit.getDefaultToolkit().addAWTEventListener(new Listener(),
        AWTEvent.MOUSE_EVENT_MASK | AWTEvent.FOCUS_EVENT_MASK);
    JFrame frame = new JFrame();
    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    frame.setVisible(true);
    frame.setAlwaysOnTop(true);
  }

  private static class Listener implements AWTEventListener {
    public void eventDispatched(AWTEvent event) {
      System.out.print(MouseInfo.getPointerInfo().getLocation() + " | ");
      System.out.println(event);
    }
  }

}
