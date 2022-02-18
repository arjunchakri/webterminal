
import java.io.File;
import java.text.CharacterIterator;
import java.text.StringCharacterIterator;

public class Formula {

  public static String execute()  throws Exception {
	String contextPath = System.getProperty("user.dir");

    File contextDirectory = new File(contextPath);
    
	return calculateAvailableSpace(contextDirectory);
  }

  public static String getFrequency() {
   return "10";
  }

  public static void main(String[] args) throws Exception {

    String contextPath = System.getProperty("user.dir");

    File contextDirectory = new File(contextPath);

    System.out.println(calculateAvailableSpace(contextDirectory));
  }

  private static String calculateAvailableSpace(File targetLocation) throws Exception {
    long usableSpace = targetLocation.getUsableSpace();
    long totalSpace = targetLocation.getTotalSpace();

    return humanReadableByteCountBin(usableSpace) + " / " + humanReadableByteCountBin(totalSpace);
  }

//  private static String byteCountToDisplaySize(long memory) {
//    try {
//      return FileUtils.byteCountToDisplaySize(memory);
//    } catch (Exception e) {
//      return memory + " bytes";
//    }
//
//  }

  public static String humanReadableByteCountBin(long bytes) {
    long absB = bytes == Long.MIN_VALUE ? Long.MAX_VALUE : Math.abs(bytes);
    if (absB < 1024) {
      return bytes + " B";
    }
    long value = absB;
    CharacterIterator ci = new StringCharacterIterator("KMGTPE");
    for (int i = 40; i >= 0 && absB > 0xfffccccccccccccL >> i; i -= 10) {
      value >>= 10;
      ci.next();
    }
    value *= Long.signum(bytes);
    return String.format("%.3f %ciB", value / 1024.0, ci.current());
  }

}
