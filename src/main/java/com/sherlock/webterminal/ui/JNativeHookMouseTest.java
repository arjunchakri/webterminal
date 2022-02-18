//package com.sherlock.nothing.ui;
//
//import org.jnativehook.GlobalScreen;
//import org.jnativehook.NativeHookException;
//import org.jnativehook.mouse.NativeMouseEvent;
//import org.jnativehook.mouse.NativeMouseInputListener;
//
//public class JNativeHookMouseTest implements NativeMouseInputListener {
//  public void nativeMouseClicked(NativeMouseEvent e) {
//
//    System.out.println("Mouse Clicked: " + e.getID() + e.getClickCount() + " at (" + e.getX() + " , " + +e.getY() + ") "
//        + "button=" + e.getButton());
//  }
//
//  public void nativeMousePressed(NativeMouseEvent e) {
//    System.out.println("Mouse Pressed: " + e.getID() + e.getButton() + " at (" + e.getX() + " , " + +e.getY() + ") "
//        + "button=" + e.getButton());
//  }
//
//  public void nativeMouseReleased(NativeMouseEvent e) {
//    System.out.println("Mouse Released: " + e.getID() + e.getButton() + " at (" + e.getX() + " , " + +e.getY() + ") "
//        + "button=" + e.getButton());
//  }
//
//  public void nativeMouseMoved(NativeMouseEvent e) {
////    System.out.println("Mouse Moved: " + e.getX() + ", " + e.getY() + " at (" + e.getX() + " , " + +e.getY() + ") "
////        + "button=" + e.getButton());
//  }
//
//  public void nativeMouseDragged(NativeMouseEvent e) {
////    System.out.println("Mouse Dragged: " + e.getX() + ", " + e.getY() + " at (" + e.getX() + " , " + +e.getY() + ") "
////        + "button=" + e.getButton());
//  }
//
//  public static void main(String[] args) {
//    try {
//      GlobalScreen.registerNativeHook();
//    } catch (NativeHookException ex) {
//      System.err.println("There was a problem registering the native hook.");
//      System.err.println(ex.getMessage());
//
//      System.exit(1);
//    }
//
//    // Construct the example object.
//    JNativeHookMouseTest example = new JNativeHookMouseTest();
//
//    // Add the appropriate listeners.
//    GlobalScreen.addNativeMouseListener(example);
//    GlobalScreen.addNativeMouseMotionListener(example);
//  }
//}
