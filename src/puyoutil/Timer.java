package puyoutil;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Label;

import javafx.geometry.Rectangle2D;

import java.awt.*;

public class Timer implements UnsetScene {
    private int sec;
    private long timer, alart, alartbase, timerbase;
    private long baseMilliSec, prevMilliSec, nowMilliSec;
    private Time time, count;
    private boolean checkViewHour;
    private boolean threadRunning;
    private int matchingStatus;
    private Rectangle2D captureRectangle;
    private Capture capture;
    private TemplateMatching readyMatching, goMatching;
    private SetScene stage;
    private Thread matchingThread;
    private static final int INIT = -1;
    private static final int NOT_READY = 0;
    private static final int READY = 1;
    private static final int GO = 2;
    @FXML
    Label countView, timeView;
    void init() {
        time = new Time(sec);
        timer = sec;
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
        readyMatching = new TemplateMatching(Main.READY_IMG_PATH, (int)captureRectangle.getWidth(), (int)captureRectangle.getHeight());
        goMatching = new TemplateMatching(Main.GO_IMG_PATH, (int)captureRectangle.getWidth(), (int)captureRectangle.getHeight());
        timeView.setText(Time.formattingTime(time, checkViewHour));
        matchingThread = new Thread(() -> {
            while (threadRunning) {
                // readyがマッチングした後goをマッチング
                if (matchingStatus == NOT_READY || matchingStatus == INIT) {
                    if (readyMatching.find(TemplateMatching.BufferedImageToMat(capture.takePicture()))) {
                        matchingStatus = READY;
                        baseMilliSec = System.currentTimeMillis();
                        count.setTime(0);
                    }
                } else {
                    if (goMatching.find(TemplateMatching.BufferedImageToMat(capture.takePicture()))) {
                        matchingStatus = GO;
                        baseMilliSec = System.currentTimeMillis();
                        alartbase = baseMilliSec;
                        timerbase = baseMilliSec;
                    } else {
                        long pastMilliSec = nowMilliSec - baseMilliSec;
                        count.setTime(pastMilliSec);
                        if (pastMilliSec > 1500) {
                            matchingStatus = NOT_READY;
                        }
                    }
                }
                nowMilliSec = System.currentTimeMillis();
                if (matchingStatus == NOT_READY) {
                    long pastMilliSec = nowMilliSec - baseMilliSec;
                    count.setTime(pastMilliSec);
                    if (nowMilliSec - timerbase >= timer * 1000) {
                        timerbase = nowMilliSec;
                        new Thread(()->{
                            for (int i = 0; i < 3; i++) {
                                Main.bellstar.play();
                                try {
                                    Thread.sleep(500);
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                }
                            }
                        }).start();
                    }
                    if (nowMilliSec - alartbase >= alart * 1000) {
                        alartbase = nowMilliSec;
                        Main.bellstar.play();
                    }
                }
                Platform.runLater(() -> countView.setText(Time.formattingTime(count, checkViewHour)));
                long sleepTime;
                if (matchingStatus == READY || Main.FINE_OPTION ) {
                    sleepTime = Main.MS_BETWEEN_FRAME - (nowMilliSec - prevMilliSec);
                } else {
                    sleepTime = Main.MS_BETWEEN_FRAME + 16 - (nowMilliSec - prevMilliSec);
                }
                prevMilliSec = nowMilliSec;
                try {
                    if (sleepTime > 0)
                        Thread.sleep(sleepTime);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });
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

    void onClick() {
        if (!threadRunning) {
            threadRunning = true;
            matchingStatus = INIT;
            baseMilliSec = System.currentTimeMillis();
            alartbase = baseMilliSec;
            timerbase = baseMilliSec;
            matchingThread.start();
        }
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
