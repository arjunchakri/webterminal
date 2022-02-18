package com.sherlock.webterminal.spring.core;

import java.io.File;

import javax.servlet.http.HttpServletRequest;

import org.springframework.stereotype.Controller;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.HandlerMapping;
import org.springframework.web.servlet.ModelAndView;

import com.sherlock.webterminal.logreader.services.readerkey.ReaderKeyRepository;

@Controller
public class WebController {

  @RequestMapping("/")
  public ModelAndView showMainPage() {
    ModelAndView modelAndView = new ModelAndView("index");
    String newReaderKey = ReaderKeyRepository.generateReaderKey();
    System.out.println("newReaderKey=" + newReaderKey);
    modelAndView.addObject("readerKey", newReaderKey);
    return modelAndView;
  }

  @RequestMapping("/remotecontrol")
  public ModelAndView remotecontrol() {
    ModelAndView modelAndView = new ModelAndView("remotecontrol");
    return modelAndView;
  }

  @RequestMapping("/apache")
  public ModelAndView apache() {
    ModelAndView modelAndView = new ModelAndView("apache");
    return modelAndView;
  }

  @RequestMapping("/screencapture")
  public ModelAndView screencapture() {
    ModelAndView modelAndView = new ModelAndView("screencapture");
    return modelAndView;
  }

  @RequestMapping("/dev")
  public ModelAndView showdev() {
    ModelAndView modelAndView = new ModelAndView("indexdev");
    String newReaderKey = ReaderKeyRepository.generateReaderKey();
    System.out.println("newReaderKey=" + newReaderKey);
    modelAndView.addObject("readerKey", newReaderKey);
    return modelAndView;
  }

  @RequestMapping("/formula")
  public ModelAndView showformulacomponent(HttpServletRequest request) {
    ModelAndView modelAndView = new ModelAndView("formula");

    String newReaderKey = ReaderKeyRepository.generateReaderKey();
    System.out.println("newReaderKey=" + newReaderKey);
    modelAndView.addObject("readerKey", newReaderKey);

    return modelAndView;
  }

  @RequestMapping("/fileedit")
  public ModelAndView showfileseditPage(@RequestParam(value = "path", required = true) String path,
      HttpServletRequest request) {
    ModelAndView modelAndView = new ModelAndView("fileedit");

    String targetPath = System.getProperty("user.home");

    try {
      targetPath = path;

      if (targetPath == null || targetPath.equalsIgnoreCase("root") || targetPath.isEmpty()) {
        targetPath = System.getProperty("user.home");
      } else {
        File targetFile = new File(targetPath);
        if (!targetFile.exists() || !targetFile.canRead()) {
          throw new Exception("Issue with the directory " + targetPath);
        }
      }
    } catch (Exception e) {
      e.printStackTrace();
      System.out.println("Falling back to root");
      targetPath = System.getProperty("user.home");
    }

    String newReaderKey = ReaderKeyRepository.generateReaderKey();
    System.out.println("newReaderKey=" + newReaderKey);
    modelAndView.addObject("readerKey", newReaderKey);
    modelAndView.addObject("path", targetPath);

    return modelAndView;
  }

  @RequestMapping("/files")
  public ModelAndView showFilesPage(@RequestParam(value = "path") String path, HttpServletRequest request) {

    ModelAndView modelAndView = new ModelAndView("files");
    String targetPath = System.getProperty("user.home");
    try {
      targetPath = path;

      if (targetPath == null || targetPath.equalsIgnoreCase("root") || targetPath.isEmpty()) {
        targetPath = System.getProperty("user.home");
      } else {
        File targetFile = new File(targetPath);
        if (!targetFile.exists() || !targetFile.isDirectory() || !targetFile.canRead()) {
          throw new Exception("Issue with the directory " + targetPath);
        }
      }
    } catch (Exception e) {
      e.printStackTrace();
      System.out.println("Falling back to root");
      targetPath = System.getProperty("user.home");
    }

    System.out.println("Folder view of " + targetPath);

    String newReaderKey = ReaderKeyRepository.generateReaderKey();
    System.out.println("newReaderKey=" + newReaderKey);
    modelAndView.addObject("readerKey", newReaderKey);
    modelAndView.addObject("folderpath", targetPath);

    return modelAndView;
  }

  /**
   * Extract path from a controller mapping. /controllerUrl/** => return matched **
   * @param request incoming request.
   * @return extracted path
   */
  public static String extractPathFromPattern(final HttpServletRequest request) {

    String path = (String) request.getAttribute(HandlerMapping.PATH_WITHIN_HANDLER_MAPPING_ATTRIBUTE);
    String bestMatchPattern = (String) request.getAttribute(HandlerMapping.BEST_MATCHING_PATTERN_ATTRIBUTE);

    AntPathMatcher apm = new AntPathMatcher();
    String finalPath = apm.extractPathWithinPattern(bestMatchPattern, path);

    return finalPath;

  }

  @RequestMapping("/log")
  public ModelAndView showHTTPPage() {
    ModelAndView modelAndView = new ModelAndView("log");
    String newReaderKey = ReaderKeyRepository.generateReaderKey();
    System.out.println("newReaderKey=" + newReaderKey);
    modelAndView.addObject("readerKey", newReaderKey);
    return modelAndView;
  }
  //"C:\Users\anunugonda\Documents\GitHub\pw-hackathon-webterminal\staticassets\core-6.7.1 JPMUS 671upgrade.html"

}
