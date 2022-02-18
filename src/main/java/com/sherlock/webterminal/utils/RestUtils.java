package com.sherlock.webterminal.utils;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.X509Certificate;
import java.util.NoSuchElementException;
import java.util.Scanner;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

public class RestUtils {

  public static String getResponse(String url, String requestMethod, String payload) throws Exception {
    try {
      url = url.replace(" ", "%20");
      System.out.println("Rest query: " + url);
      HttpURLConnection con = getConnection(url, requestMethod, payload);

      if (con.getResponseCode() == 200) {

        InputStream response = null;
        response = con.getInputStream();
        String json = null;
        Scanner scanner = null;
        if (response != null) {
          try {
            scanner = new Scanner(response);
            json = scanner.useDelimiter("\\A").next();

          } finally {
            if (scanner != null) {
              scanner.close();
            }
          }
          return json;
        }
      }

    } catch (MalformedURLException e) {
      e.printStackTrace();
    } catch (IOException e) {
      e.printStackTrace();
    } catch (NoSuchElementException e) {
      System.out.println("NoSuchElementException");
    }
    return null;
  }

  public static synchronized HttpURLConnection getConnection(String projectsURL, String requestMethod, String payload)
      throws MalformedURLException, IOException {
    URL url;
    url = new URL(projectsURL);
    HttpURLConnection con = (HttpURLConnection) url.openConnection();
    con.setRequestMethod(requestMethod);
    con.setRequestProperty("Accept", "application/json");
    con.setRequestProperty("content-type", "application/json");

//    String encoded = Base64.getEncoder()
//        .encodeToString((JENKINS_AUTH_USERNAME + ":" + JENKINS_AUTH_PASSWORD).getBytes(StandardCharsets.UTF_8)); // Java 8
//    con.setRequestProperty("Authorization", "Basic " + encoded);
    if (payload != null) {
      con.setDoOutput(true);

      OutputStreamWriter writer = new OutputStreamWriter(con.getOutputStream(), "UTF-8");
      writer.write(payload);
      writer.close();

    }
    return con;
  }

  public static void trustHTTPS() {
    try {
      // Create a trust manager that does not validate certificate chains
      TrustManager[] trustAllCerts = new TrustManager[] { new X509TrustManager() {
        public java.security.cert.X509Certificate[] getAcceptedIssuers() {
          return null;
        }

        public void checkClientTrusted(X509Certificate[] certs, String authType) {
        }

        public void checkServerTrusted(X509Certificate[] certs, String authType) {
        }
      } };

      // Install the all-trusting trust manager
      SSLContext sc = SSLContext.getInstance("SSL");
      sc.init(null, trustAllCerts, new java.security.SecureRandom());
      HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());

      // Create all-trusting host name verifier
      HostnameVerifier allHostsValid = new HostnameVerifier() {
        public boolean verify(String hostname, SSLSession session) {
          return true;
        }
      };

      // Install the all-trusting host verifier
      HttpsURLConnection.setDefaultHostnameVerifier(allHostsValid);
    } catch (NoSuchAlgorithmException e) {
      e.printStackTrace();
    } catch (KeyManagementException e) {
      e.printStackTrace();
    }
  }
}
