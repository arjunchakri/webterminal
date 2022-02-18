
import java.io.File;

public class Pre {

  public static void main(String[] args) {

    try {
      String fileName = args[0];
      System.out.println("deleting -> " + fileName);
      boolean delete = new File(fileName).delete();
      System.out.println("deleted ->" + delete);

    } catch (Exception e) {
      e.printStackTrace();
    }
  }

}