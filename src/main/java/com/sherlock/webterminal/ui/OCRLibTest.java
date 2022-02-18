//package com.sherlock.nothing.ui;
//
//import java.awt.Rectangle;
//import java.awt.Robot;
//import java.awt.Toolkit;
//import java.awt.image.BufferedImage;
//import java.util.List;
//
//import net.sourceforge.tess4j.ITessAPI.TessPageIteratorLevel;
//import net.sourceforge.tess4j.Tesseract;
//
//public class OCRLibTest {
//
//  private final static String datapath = "tessdata";
//  private final static String language = "eng";
//
//  public static void main(String[] args) throws Exception {
//
//    Tesseract tesseract = new Tesseract();
//
//    tesseract.setDatapath("C:\\Users\\anunugonda\\Downloads");
//    BufferedImage imageSc = new Robot().createScreenCapture(new Rectangle(Toolkit.getDefaultToolkit().getScreenSize()));
//
//    int level = TessPageIteratorLevel.RIL_WORD;
//
//    String doOCR = tesseract.doOCR(imageSc);
//    System.out.println(doOCR);
//
//    List<Rectangle> result = tesseract.getSegmentedRegions(imageSc, level);
//      for (int i = 0; i < result.size(); i++) {
//        Rectangle rect = result.get(i);
//  
//  //      System.out.println(String.format("Box[%d]: x=%d, y=%d, w=%d, h=%d", i, rect.x, rect.y, rect.width, rect.height));
//  
//  //      System.out.println(tesseract.doOCR(imageSc, rect));
//  
//      }
//
//  }
//
//}
