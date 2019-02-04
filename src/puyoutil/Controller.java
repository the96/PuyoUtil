package puyoutil;

import com.github.sarxos.webcam.Webcam;
import javafx.collections.ListChangeListener;
import javafx.collections.MapChangeListener;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Rectangle2D;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.TextField;
import javafx.stage.Screen;
import javafx.stage.Stage;

import java.awt.*;
import java.io.IOException;
import java.util.ArrayList;

public class Controller implements UnsetScene{
    @FXML
    TextField fieldTime, fieldAlart;
    @FXML
    ChoiceBox<CaptureObject> captureSelect;
    @FXML
    CheckBox checkViewHour;
    @FXML
    RadioButton display, captureDevice;
    final ToggleGroup group = new ToggleGroup();
    private SetScene stage;
    private Preview previewController;
    private Stage previewStage;
    ArrayList<CaptureObject> displayList;
    ArrayList<CaptureObject> captureDeviceList;
    private static final int DISPLAY = 1;
    private static final int CAPTURE_DEVICE = 2;

    class CaptureObject {
        String name;
        String text;
        int type;
        Object object;
        CaptureObject (int index, String text, int type, Object object) {
            if (type == DISPLAY) {
                name = "display[" + index + "]";
            } else if(type == CAPTURE_DEVICE) {
                name = "device[" + index + "]";
            }
            this.text = text;
            this.type = type;
            this.object = object;
        }
        public String toString() {
            return name + ":" + text;
        }
    }

    public void init() {
        display.setToggleGroup(group);
        captureDevice.setToggleGroup(group);
        display.setSelected(true);
        displayList = new ArrayList<>();
        int index = 0;
        for (Screen screen: Screen.getScreens()) {
            Rectangle2D rec = screen.getBounds();
            String text = "(" + Math.floor(rec.getWidth()) + "x" + Math.floor(rec.getHeight()) + ")";
            CaptureObject object = new CaptureObject(index, text, DISPLAY ,screen);
            displayList.add(object);
        }
        captureDeviceList = new ArrayList<>();
        index = 0;
        for (Webcam webcam: Webcam.getWebcams()) {
            CaptureObject object = new CaptureObject(index, webcam.getName(), CAPTURE_DEVICE ,webcam);
            captureDeviceList.add(object);
        }
        setItemToSelectList(DISPLAY);
    }
    @FXML
    public void onSelectCapture(ActionEvent e) {
        if (e.getTarget() == display) {
            setItemToSelectList(DISPLAY);
        } else if (e.getTarget() == captureDevice) {
            setItemToSelectList(CAPTURE_DEVICE);
        }
    }

    public void setItemToSelectList(int radioCapture) {
        captureSelect.getItems().clear();
        switch (radioCapture) {
            case DISPLAY:
                captureSelect.getItems().setAll(displayList);
                captureSelect.setValue(displayList.get(0));
                break;
            case CAPTURE_DEVICE:
                captureSelect.getItems().setAll(captureDeviceList);
                captureSelect.setValue(captureDeviceList.get(0));
                break;
        }
    }

    @FXML
    public void onClickAtStart(ActionEvent e) throws IOException {
        CaptureObject captureObject= captureSelect.getValue();
        if (captureObject == null) return;
        if (previewController != null && previewController.isRunning()) {
            previewController.close();
            previewStage.close();
        }
        FXMLLoader loader = new FXMLLoader(getClass().getResource("timer.fxml"));
        Parent view = loader.load();
        stage.show(new Scene(view, Main.WIDTH, Main.HEIGHT), this);
        Timer timer = loader.getController();
        timer.setTime(Integer.parseInt(fieldTime.getText()));
        timer.setAlert(Integer.parseInt(fieldAlart.getText()));
        timer.setCheckViewHour(checkViewHour.isSelected());
        timer.setInterface(stage);
        timer.setCapture(this.getCapture(captureObject));
        timer.init();
        view.setOnMouseClicked(event -> timer.onClick());
    }

    private Capture getCapture(CaptureObject object) {
        switch (object.type) {
            case DISPLAY:
                try {
                    Screen screen = (Screen) object.object;
                    return new DisplayCapture(getRectangleFromRectangle2D(screen.getBounds()));
                } catch (AWTException e) {
                    e.printStackTrace();
                } catch (ClassCastException e) {
                    e.printStackTrace();
                }
            break;
            case CAPTURE_DEVICE:
                try {
                    Webcam webcam = (Webcam) object.object;
                    DeviceCapture dc = new DeviceCapture(webcam);
                    dc.init(new Dimension(1980, 1080));
                    return dc;
                } catch (ClassCastException e) {
                    e.printStackTrace();
                }
                break;
        }
        return null;
    }

    private Rectangle getRectangleFromRectangle2D(Rectangle2D rec) {
        return new Rectangle((int) rec.getMinX(), (int) rec.getMinY(), (int) rec.getWidth(), (int) rec.getHeight());
    }

    @FXML
    public void soundTest(ActionEvent e) {
        Main.bellstar.play();
    }

    @FXML
    public void checkCapture(ActionEvent e) throws IOException {
        CaptureObject captureObject = captureSelect.getValue();
        if (captureObject == null) return;
        previewStage = new Stage();
        FXMLLoader loader = new FXMLLoader(getClass().getResource("preview.fxml"));
        Parent root = loader.load();
        previewStage.setTitle("PuyoUtil");
        previewStage.setScene(new Scene(root, 400, 225));
        previewStage.show();
        previewController = loader.getController();
        previewController.init(this.getCapture(captureObject));
        previewStage.setOnCloseRequest(event -> previewController.close());
    }

    public void setInterface(SetScene stage) {
        this.stage = stage;
    }
}