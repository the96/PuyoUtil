package puyoutil;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.CheckBox;
import javafx.scene.control.TextField;

import java.io.IOException;

public class Controller implements UnsetScene{
    @FXML
    TextField fieldTime, fieldAlart;
    @FXML
    CheckBox checkViewHour;
    SetScene stage;
    @FXML
    public void onClickAtStart(ActionEvent e) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("timer.fxml"));
        Parent view = loader.load();
        stage.show(new Scene(view, 400, 200), this);
        Timer timer = loader.getController();
        timer.setTime(Integer.parseInt(fieldTime.getText()));
        timer.setAlert(Integer.parseInt(fieldAlart.getText()));
        timer.setCheckViewHour(checkViewHour.isSelected());
        timer.setInterface(stage);
        timer.init();
    }

    public void setInterface(SetScene stage) {
        this.stage = stage;
    }
}
