//package com.sherlock.nothing.ui;
//
//import static java.util.logging.Level.OFF;
//import static java.util.logging.Logger.getLogger;
//import static org.jnativehook.GlobalScreen.addNativeKeyListener;
//import static org.jnativehook.GlobalScreen.registerNativeHook;
//import static org.jnativehook.GlobalScreen.removeNativeKeyListener;
//import static org.jnativehook.keyboard.NativeKeyEvent.getKeyText;
//
//import java.util.concurrent.CountDownLatch;
//import java.util.logging.Logger;
//
//import org.jnativehook.GlobalScreen;
//import org.jnativehook.NativeHookException;
//import org.jnativehook.keyboard.NativeKeyEvent;
//import org.jnativehook.keyboard.NativeKeyListener;
//
//public class JNativeHookKeyboardTest implements NativeKeyListener {
//  // Terminate after 100 keystrokes.
//  private final CountDownLatch mLatch = new CountDownLatch(100);
//
//  @Override
//  public void nativeKeyPressed(final NativeKeyEvent e) {
//    System.out.println("Pressed: " + getKeyText(e.getKeyCode()));
//    mLatch.countDown();
//  }
//
//  @Override
//  public void nativeKeyReleased(final NativeKeyEvent e) {
//    System.out.println("Released: " + getKeyText(e.getKeyCode()));
//  }
//
//  @Override
//  public void nativeKeyTyped(final NativeKeyEvent e) {
//    System.out.println("Typed: " + e);
//  }
//
//  private void start() throws InterruptedException {
//    System.out.println("Awaiting keyboard events.");
//    mLatch.await();
//    System.out.println("All keyboard events have fired, exiting.");
//  }
//
//  public static void main(final String[] args) throws NativeHookException, InterruptedException {
//    disableNativeHookLogger();
//    registerNativeHook();
//
//    JNativeHookKeyboardTest harness = new JNativeHookKeyboardTest();
//    addNativeKeyListener(harness);
//
//    System.out.println("Listener attached.");
//    harness.start();
//
//    System.out.println("Listener detached.");
//    removeNativeKeyListener(harness);
//  }
//
//  private static void disableNativeHookLogger() {
//    Logger logger = getLogger(GlobalScreen.class.getPackage().getName());
//    logger.setLevel(OFF);
//    logger.setUseParentHandlers(false);
//  }
//
//}
