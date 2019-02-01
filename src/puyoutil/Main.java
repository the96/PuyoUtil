package puyoutil;

import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import org.opencv.core.Core;

public class Main extends Application implements SetScene {
    public static final int WIDTH = 400;
    public static final int HEIGHT = 255;
    Stage stage;

    @Override
    public void start(Stage primaryStage) throws Exception{
        stage = primaryStage;
        FXMLLoader loader = new FXMLLoader(getClass().getResource("setting.fxml"));
        Parent root = loader.load();
        primaryStage.setTitle("PuyoUtil");
        primaryStage.setScene(new Scene(root, WIDTH, HEIGHT));
        primaryStage.show();
        Controller controller = loader.getController();
        controller.setInterface(this);
        controller.init();
    }

    public static void main(String[] args) {
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
        launch(args);
    }

    @Override
    public void show(Scene newScene, UnsetScene scene) {
        scene.unset();
        this.stage.setScene(newScene);
    }

    @Override
    public void setCloseOperation(EventHandler<WindowEvent> e) {
        this.stage.setOnCloseRequest(e);
    }
}

interface SetScene {
    void show(Scene newScene, UnsetScene scene);
    void setCloseOperation(EventHandler<WindowEvent> e);
}

interface UnsetScene {
    default void unset(){}
}