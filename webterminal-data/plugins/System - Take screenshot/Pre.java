
import java.awt.Rectangle;
import java.awt.Robot;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.imageio.ImageIO;

public class Pre {

  public static void main(String[] args) throws Exception {

    File screenshotsDir = new File(System.getProperty("java.io.tmpdir"), "webterminalscreenshots");
    screenshotsDir.mkdirs();

    String timestamp = new SimpleDateFormat("yyyyMMddHHmmss").format(new Date());
    File screenshotFile = new File(screenshotsDir, "screenshot-" + timestamp + ".png");

    BufferedImage image = new Robot().createScreenCapture(new Rectangle(Toolkit.getDefaultToolkit().getScreenSize()));
    ImageIO.write(image, "png", screenshotFile);

    System.out.println("Screenshot created at " + screenshotFile.getAbsolutePath());

  }

}