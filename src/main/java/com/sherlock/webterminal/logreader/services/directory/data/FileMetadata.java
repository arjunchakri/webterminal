package com.sherlock.webterminal.logreader.services.directory.data;

public class FileMetadata {

  private String name;
  private long lastModified;
  private long size;
  private boolean isDir;

  public FileMetadata(String name, long lastModified, long size, boolean isDir) {
    super();
    this.name = name;
    this.lastModified = lastModified;
    this.size = size;
    this.isDir = isDir;
  }

  public boolean isDir() {
    return isDir;
  }

  public void setDir(boolean isDir) {
    this.isDir = isDir;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public long getLastModified() {
    return lastModified;
  }

  public void setLastModified(long lastModified) {
    this.lastModified = lastModified;
  }

  public long getSize() {
    return size;
  }

  public void setSize(long size) {
    this.size = size;
  }

  public FileMetadata(String name, long lastModified, long size) {
    super();
    this.name = name;
    this.lastModified = lastModified;
    this.size = size;
    this.isDir = false;
  }

}
