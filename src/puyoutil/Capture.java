package puyoutil;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public abstract class Capture {
    abstract BufferedImage takePicture();
    void takeAndSavePicture() {
        try {
        String filename = new SimpleDateFormat("yyyy-mm-dd-hh-mm-ss").format(new Date()) + ".png";
        ImageIO.write(takePicture(), "png", new File(filename));
        System.out.println("save file.\r\npath:" + filename);
        } catch (IOException e) {
        e.printStackTrace();
        }
    }
    void close() {}
    abstract int getWidth();
    abstract int getHeight();
}