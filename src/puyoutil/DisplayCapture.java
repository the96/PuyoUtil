package puyoutil;

import org.opencv.core.Rect;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class DisplayCapture extends Capture {
    private Robot robot;
    private Rectangle area;

    DisplayCapture(Rectangle rectangle) throws AWTException{
        this.robot = new Robot();
        this.area = rectangle;
    }

    BufferedImage takePicture() {
        return robot.createScreenCapture(area);
    }

    @Override
    int getWidth() {
        return (int) area.getWidth();
    }

    @Override
    int getHeight() {
        return (int) area.getHeight();
    }
}