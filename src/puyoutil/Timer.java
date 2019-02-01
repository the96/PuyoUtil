package puyoutil;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Label;

import javafx.geometry.Rectangle2D;

import java.awt.*;

public class Timer implements UnsetScene {
    private int sec, alart;
    private long baseMilliSec, prevMilliSec, nowMilliSec;
    private Time time, count;
    private boolean checkViewHour;
    private boolean threadRunning;
    private boolean lastResult;
    private Rectangle2D captureRectangle;
    private Capture capture;
    private TemplateMatching matching;
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
        matching = new TemplateMatching(Main.READY_IMG_PATH, (int)captureRectangle.getWidth(), (int)captureRectangle.getHeight());
        timeView.setText(Time.formattingTime(time, checkViewHour));
        threadRunning = true;
        lastResult = false;
        baseMilliSec = System.currentTimeMillis();
        new Thread(() -> {
            while (threadRunning) {
                lastResult = matching.find(TemplateMatching.BufferedImageToMat(capture.takePicture()));
                nowMilliSec = System.currentTimeMillis();
                count.setTime(nowMilliSec - baseMilliSec);
                if (lastResult) {
                    baseMilliSec = nowMilliSec;
                }
                Platform.runLater(() -> countView.setText(Time.formattingTime(count, checkViewHour)));
                long sleepTime = Main.MS_BETWEEN_FRAME - (nowMilliSec - prevMilliSec);
                prevMilliSec = nowMilliSec;
                try {
                    if (sleepTime > 0)
                        Thread.sleep(sleepTime);
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
    private int h, m, s, deci_sec;
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
    void setTime (long ms) {
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
