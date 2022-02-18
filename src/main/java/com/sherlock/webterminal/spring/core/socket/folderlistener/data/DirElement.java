package com.sherlock.webterminal.spring.core.socket.folderlistener.data;

public class DirElement {

  private String name;
  private String type;

  private long lastModified;
  private long size;

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getType() {
    return type;
  }

  public void setType(String type) {
    this.type = type;
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

  @Override
  public String toString() {
    return "DirElement [name=" + name + ", type=" + type + ", lastModified=" + lastModified + ", size=" + size + "]";
  }

  public DirElement(String name, String type, long lastModified, long size) {
    super();
    this.name = name;
    this.type = type;
    this.lastModified = lastModified;
    this.size = size;
  }

}
