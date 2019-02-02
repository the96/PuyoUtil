package puyoutil;

import javafx.embed.swing.SwingFXUtils;
import javafx.fxml.FXML;
import javafx.geometry.Rectangle2D;
import javafx.scene.image.ImageView;

import java.awt.*;

public class Preview {
    private boolean nowpreview;
    private Capture capture;
    @FXML
    ImageView view;

    public void init(Rectangle2D rectangle) {
        nowpreview = true;
        try {
            capture = new Capture(new Rectangle((int)rectangle.getMinX(), (int)rectangle.getMinY(), (int)rectangle.getWidth(), (int)rectangle.getHeight()));
            new Thread(() -> {
                while(nowpreview) {
                    view.setImage(SwingFXUtils.toFXImage(capture.takePicture(), null));
                    try {
                        Thread.sleep(Main.MS_BETWEEN_FRAME);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }).start();
        } catch (AWTException e) {
            e.printStackTrace();
        }
    }

    public void close() {
        nowpreview = false;
    }

    public boolean isRunning() {
        return nowpreview;
    }
}
