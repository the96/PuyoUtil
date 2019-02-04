package puyoutil;

import javafx.embed.swing.SwingFXUtils;
import javafx.fxml.FXML;
import javafx.scene.image.ImageView;

public class Preview {
    private boolean isNowPreview;
    private Capture capture;
    @FXML
    ImageView view;

    public void init(Capture capture) {
        isNowPreview = true;
        this.capture = capture;
        new Thread(() -> {
            while(isNowPreview) {
                view.setImage(SwingFXUtils.toFXImage(this.capture.takePicture(), null));
                try {
                    Thread.sleep(Main.MS_BETWEEN_FRAME);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    public void close() {
        isNowPreview = false;
    }

    public boolean isRunning() {
        return isNowPreview;
    }
}
