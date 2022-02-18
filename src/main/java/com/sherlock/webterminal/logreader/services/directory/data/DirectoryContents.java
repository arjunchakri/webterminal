package com.sherlock.webterminal.logreader.services.directory.data;

import java.util.List;

public class DirectoryContents {

  private List<FileMetadata> files;
  private String displayName;
  private String path;

  public DirectoryContents() {
  }

  public List<FileMetadata> getFiles() {
    return files;
  }

  public void setFiles(List<FileMetadata> files) {
    this.files = files;
  }

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

  public DirectoryContents(List<FileMetadata> files, String displayName, String path) {
    super();
    this.files = files;
    this.displayName = displayName;
    this.path = path;
  }

}
