package com.sherlock.webterminal.installer;

import java.io.File;

import org.eclipse.jgit.api.Git;

public class WebTerminalInstallerRunner {

  public static void main(String[] args) {
    try {

      String baseDir = System.getProperty("user.dir");
      File targetSpace = new File(baseDir, InstallerProperties.APP_SPACE_FOLDER);

      if (args.length != 0) {
        //Assuming its the basedir
        baseDir = args[0];
        if (baseDir.equals(".")) {
          System.out.println("Setting current dir...");
          baseDir = "";
        }
        targetSpace = new File(baseDir);
      }

      System.out.println("Starting installer.. Attempting to download webterminal at " + targetSpace.getAbsolutePath());

      String gitRepo = InstallerProperties.GIT_REPO;
      String gitBranch = InstallerProperties.GIT_BRANCH;

      Git git = null;
      if (targetSpace.isDirectory() && targetSpace.exists()) {
        System.out.println("Attempting to pull latest changes... ");

        //existing folder. Load workspace and try a git pull
        git = Git.open(targetSpace);
        git.pull().call();
      } else {
        System.out.println("Attempting to setup the application... ");

        // fresh checkout.
        git = Git.cloneRepository().setURI(gitRepo).setBranch(gitBranch).setDirectory(targetSpace).call();
      }
    } catch (Exception e) {
      e.printStackTrace();
    }

  }

}
