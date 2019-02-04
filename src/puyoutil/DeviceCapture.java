package puyoutil;

import com.github.sarxos.webcam.Webcam;

import java.awt.*;
import java.awt.image.BufferedImage;

public class DeviceCapture extends Capture {
    Webcam webcam;

    DeviceCapture (Webcam webcam) {
        this.webcam = webcam;
    }

    void init(Dimension size) {
//        webcam.setViewSize(size);
        webcam.open(false);
    }

    @Override
    BufferedImage takePicture() {
        return webcam.getImage();
    }

    @Override
    void close() {
        webcam.close();
    }

    @Override
    int getWidth() {
        return webcam.getViewSize().width;
    }

    @Override
    int getHeight() {
        return webcam.getViewSize().height;
    }
}
