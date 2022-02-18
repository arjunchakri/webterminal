package com.sherlock.webterminal.emails.data;

public class EmailConfig {

  private String smtp_host;
  private String smtp_port;
  private String smtp_username;
  private String smtp_password;
  private String from_address;

  public String getSmtp_host() {
    return smtp_host;
  }

  public void setSmtp_host(String smtp_host) {
    this.smtp_host = smtp_host;
  }

  public String getSmtp_port() {
    return smtp_port;
  }

  public void setSmtp_port(String smtp_port) {
    this.smtp_port = smtp_port;
  }

  public String getSmtp_username() {
    return smtp_username;
  }

  public void setSmtp_username(String smtp_username) {
    this.smtp_username = smtp_username;
  }

  public String getSmtp_password() {
    return smtp_password;
  }

  public void setSmtp_password(String smtp_password) {
    this.smtp_password = smtp_password;
  }

  public String getFrom_address() {
    return from_address;
  }

  public void setFrom_address(String from_address) {
    this.from_address = from_address;
  }

}
