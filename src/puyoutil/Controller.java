package puyoutil;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Rectangle2D;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Screen;

import java.io.IOException;
import java.util.ArrayList;

public class Controller implements UnsetScene{
    @FXML
    TextField fieldTime, fieldAlart;
    @FXML
    ChoiceBox<ScreenItem> captureSelect;
    @FXML
    CheckBox checkViewHour;
    ArrayList<ScreenItem> screenItemList;
    SetScene stage;

    public void init() {
        screenItemList = new ArrayList<>();
        int i = 0;
        ScreenItem defVal = null;
        for (Screen screen: Screen.getScreens()) {
            ScreenItem item = new ScreenItem(screen, i++);
            screenItemList.add(item);
            if (defVal == null) defVal = item;
        }
        screenItemList.add(new ScreenItem(i));
        captureSelect.getItems().setAll(screenItemList);
        captureSelect.setValue(defVal);
    }

    @FXML
    public void onClickAtStart(ActionEvent e) throws IOException {
        Screen screen = captureSelect.getValue().screen;
        if (screen == null) {
            Alert alert   = new Alert( Alert.AlertType.NONE , "" , ButtonType.OK);
            alert.setTitle( "ごめんなさい" );
            alert.getDialogPane().setHeaderText( "手動選択は現在実装されていません" );
            alert.getDialogPane().setContentText( "キャプチャするディスプレイを一枚選択してください" );
            alert.showAndWait();
            return;
        }
        FXMLLoader loader = new FXMLLoader(getClass().getResource("timer.fxml"));
        Parent view = loader.load();
        stage.show(new Scene(view, Main.WIDTH, Main.HEIGHT), this);
        Timer timer = loader.getController();
        timer.setTime(Integer.parseInt(fieldTime.getText()));
        timer.setAlert(Integer.parseInt(fieldAlart.getText()));
        timer.setCheckViewHour(checkViewHour.isSelected());
        timer.setInterface(stage);
        timer.setCaptureRectangle(screen.getBounds());
        timer.init();
    }

    @FXML
    public void soundTest(ActionEvent e) {
        Main.bellstar.play();
    }

    public void setInterface(SetScene stage) {
        this.stage = stage;
    }
}

class ScreenItem {
    Screen screen;
    int index;
    ScreenItem(Screen screen, int index) {
        this.screen = screen;
        this.index = index;
    }
    ScreenItem(int index) {
        this.screen = null;
        this.index = index;
    }
    public String toString() {
        if (this.screen != null) {
            Rectangle2D rec = screen.getBounds();
            return "screen[" + index + "](" + Math.floor(rec.getWidth()) + "x" + Math.floor(rec.getHeight()) + ")";
        } else {
            return "手動で選択";
        }
    }
}