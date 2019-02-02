package puyoutil;

import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.media.AudioClip;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import org.opencv.core.Core;

import java.io.File;

public class Main extends Application implements SetScene {
    public static final int WIDTH = 400;
    public static final int HEIGHT = 300;
    public static final int BASE_WIDTH = 1920;
    public static final int BASE_HEIGHT = 1080;
    public static final String READY_IMG_PATH = "ready_mini.png";
    public static final String GO_IMG_PATH = "go_mini.png";
    public static final boolean BUILD_JAR = false;
    public static final double THRESH = 0.7;
    public static final int FRAME_RATE = 60;
    public static final int MS_BETWEEN_FRAME = (int) Math.floor(1000 / FRAME_RATE);
    public static final boolean MATCHING_RANGE_OPTIMIZE_OPTION = true;
    public static final boolean MATCHING_RESIZE_OPTION = true;
    public static final double MATCHING_RESIZE_MAGNIFICATION = 0.5;
    public static final boolean FINE_OPTION = true;
    static AudioClip bellstar;
    Stage stage;

    @Override
    public void start(Stage primaryStage) throws Exception{
        bellstar = new AudioClip(new File("bellstar.mp3").toURI().toString());
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
        if (BUILD_JAR) {
            System.loadLibrary("./opencv_java401");
        } else {
            System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
        }
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