package puyoutil;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Capture {
    private Robot robot;
    private Rectangle area;

    Capture(Rectangle rectangle) throws AWTException{
        this.robot = new Robot();
        this.area = rectangle;
    }

    public BufferedImage takePicture() {
        return robot.createScreenCapture(area);
    }

    public void takeAndSavePicture() {
        try {
            String filename = new SimpleDateFormat("yyyy-mm-dd-hh-mm-ss").format(new Date()) + ".png";
            ImageIO.write(takePicture(), "png", new File(filename));
            System.out.println("save file.\r\npath:" + filename);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}