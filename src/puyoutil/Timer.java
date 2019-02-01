package puyoutil;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Label;

import javafx.geometry.Rectangle2D;

import java.awt.*;

public class Timer implements UnsetScene {
    private int sec, alart;
    private long prevMilliSec;
    private Time time, count;
    private boolean checkViewHour;
    private boolean threadRunning;
    private Rectangle2D captureRectangle;
    private Capture capture;
    private SetScene stage;
    @FXML
    Label countView, timeView;
    void init() {
        time = new Time(sec);
        count = new Time(0);
        try {
            capture = new Capture(new Rectangle(
                                            (int) captureRectangle.getMinX(),
                                            (int) captureRectangle.getMinY(),
                                            (int) captureRectangle.getWidth(),
                                            (int) captureRectangle.getHeight()));
        } catch (AWTException e) {
            e.printStackTrace();
            return;
        }
        capture.takeAndSavePicture();
        timeView.setText(Time.formattingTime(time, checkViewHour));
        prevMilliSec = System.currentTimeMillis();
        threadRunning = true;
        new Thread(() -> {
            while (threadRunning) {
                long nowMillSec = System.currentTimeMillis();
                count = new Time(nowMillSec - prevMilliSec);
                Platform.runLater(() -> countView.setText(Time.formattingTime(count, checkViewHour)));
                try {
                    Thread.sleep(10);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    void setTime(int sec) {
        this.sec = sec;
    }

    void setAlert(int alert) {
        this.alart = alert;
    }

    void setCheckViewHour(boolean checkViewHour) {
        this.checkViewHour = checkViewHour;
    }

    void setCaptureRectangle(Rectangle2D captureRectangle) {
        this.captureRectangle = captureRectangle;
    }

    void setInterface(SetScene stage) {
        this.stage = stage;
        this.stage.setCloseOperation(event -> threadRunning = false);
    }

    @Override
    public void unset() {
        threadRunning = false;
        this.stage.setCloseOperation(event -> {});
    }
}

class Time {
    int h, m, s, deci_sec;
    Time (int s) {
        this.h = (int) Math.floor(s / 3600);
        this.m = (int) Math.floor((s % 3600) / 60);
        this.s = s % 60;
        this.deci_sec = 0;
    }
    Time (long ms) {
        int sec = (int) Math.floor(ms / 1000);
        this.h = (int) Math.floor(sec / 3600);
        this.m = (int) Math.floor((sec % 3600) / 60);
        this.s = sec % 60;
        this.deci_sec = (int) Math.floor(ms % 1000 / 10);
    }
    public String toString() {
        return formattingTime(this, true);
    }

    static String formattingTime(Time time, boolean checkViewHour) {
        return (checkViewHour ? time.h + ":" : "") + String.format("%02d", time.m) + ":" + String.format("%02d", time.s) + ":" + String.format("%02d", time.deci_sec);
    }
}
