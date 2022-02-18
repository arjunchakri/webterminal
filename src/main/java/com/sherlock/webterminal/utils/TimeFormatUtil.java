
package com.sherlock.webterminal.utils;

public class TimeFormatUtil {

  private static final int SECOND = 1000;
  private static final int MINUTE = 60 * SECOND;
  private static final int HOUR = 60 * MINUTE;
  private static final int DAY = 24 * HOUR;

  public static String formatDuration(long millis) {

    try {
      StringBuffer timeBuilder = new StringBuffer("");
      if (millis > DAY) {
        timeBuilder.append(millis / DAY).append(" days ");
        millis %= DAY;
      }
      if (millis > HOUR) {
        timeBuilder.append(millis / HOUR).append(" hr ");
        millis %= HOUR;
      }
      if (millis > MINUTE) {
        timeBuilder.append(millis / MINUTE).append(" min ");
        millis %= MINUTE;
      }
      if (millis > SECOND) {
        timeBuilder.append(millis / SECOND).append(" sec ");
        millis %= SECOND;
      }
      timeBuilder.append(millis + " ms");
      return timeBuilder.toString();
    } catch (Exception e) {
      return (millis + " ms");
    }
  }

  public static void main(String[] args) {
    System.out.println(formatDuration(3900000));
    System.out.println(formatDuration(45909));
    System.out.println(formatDuration(64985));

  }

}
