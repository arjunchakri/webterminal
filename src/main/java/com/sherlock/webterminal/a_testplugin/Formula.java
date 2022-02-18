package com.sherlock.webterminal.a_testplugin;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import javax.management.remote.JMXConnector;
import javax.management.remote.JMXConnectorFactory;
import javax.management.remote.JMXServiceURL;
import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

public class Formula {

  public static String getFrequency() {
    return "10";
  }

  public static void main(String[] args) {
    try {
      System.out.println(execute());
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  public static String execute() throws Exception {

    // COMPUTE SOMETHING

    return "Value to be rendered";

  }

}
