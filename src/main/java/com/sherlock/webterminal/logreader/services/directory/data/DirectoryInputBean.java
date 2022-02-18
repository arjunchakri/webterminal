package com.sherlock.webterminal.logreader.services.directory.data;

public class DirectoryInputBean {
  private String displayName;
  private String path;

  public String getDisplayName() {
    return displayName;
  }

  public void setDisplayName(String displayName) {
    this.displayName = displayName;
  }

  public String getPath() {
    return path;
  }

  public void setPath(String path) {
    this.path = path;
  }

  public DirectoryInputBean(String displayName, String path) {
    super();
    this.displayName = displayName;
    this.path = path;
  }

  @Override
  public String toString() {
    return "DirectoryInputBean [displayName=" + displayName + ", path=" + path + "]";
  }

}
