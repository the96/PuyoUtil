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
    private Capture capture;
    private TemplateMatching readyMatching, goMatching;
    private SetScene stage;
    private Thread matchingThread;
    private static final int INIT = -1;
    private static final int NOT_READY = 0;
    private static final int READY = 1;
    private static final int GO = 2;
    private static final int PRE_NOTICE_SEC = 10;
    @FXML
    Label countView, timeView;
    void init() {
        time = new Time(sec);
        timer = sec;
        readyMatching = new TemplateMatching(Main.READY_IMG_PATH, capture.getWidth(), capture.getHeight());
        goMatching = new TemplateMatching(Main.GO_IMG_PATH, capture.getWidth(), capture.getHeight());
        timeView.setText(Time.formattingTime(time, checkViewHour));
        matchingThread = new Thread(() -> {
            while (threadRunning) {
                nowMilliSec = System.currentTimeMillis();
                // 上から順に状態が遷移
                switch (matchingStatus) {
                    case NOT_READY:
                        long pastMilliSec = nowMilliSec - baseMilliSec;
                        count.setTime(pastMilliSec);
                        Platform.runLater(() -> countView.setText(Time.formattingTime(count, checkViewHour)));
                        if (nowMilliSec - timerbase >= (timer + PRE_NOTICE_SEC) * 1000) { // 予定時刻10秒前からの処理
                            timerbase = nowMilliSec;
                            new Thread(new Runnable() {
                                @Override
                                public void run() {
                                    try {
                                        loopPlay(2);
                                        Thread.sleep(PRE_NOTICE_SEC * 1000);
                                        loopPlay(4);
                                    } catch (InterruptedException e) {
                                        e.printStackTrace();
                                    }
                                }
                                void loopPlay(int cnt) {
                                    for (int i = 0; i < cnt; i++) {
                                        Main.bellstar.play();
                                        try {
                                            Thread.sleep(500);
                                        } catch (InterruptedException e) {
                                            e.printStackTrace();
                                        }
                                    }
                                }
                            }).start();
                        } else if (nowMilliSec - alartbase >= alart * 1000) { // 定期的な通知
                            alartbase = nowMilliSec;
                            Main.bellstar.play();
                        }
                        // break through
                    case INIT:
                        if (readyMatching.find(TemplateMatching.BufferedImageToMat(capture.takePicture()))) {
                            matchingStatus = READY;
                        }
                        break;
                    case READY:
                        if (goMatching.find(TemplateMatching.BufferedImageToMat(capture.takePicture()))) {
                            matchingStatus = GO;
                            count.setTime(0);
                            Platform.runLater(() -> countView.setText(Time.formattingTime(count, checkViewHour)));
                        }
                        break;
                    case GO:
                        if (goMatching.find(TemplateMatching.BufferedImageToMat(capture.takePicture()))) {
                            baseMilliSec = nowMilliSec;
                            alartbase = baseMilliSec;
                            timerbase = baseMilliSec;
                            // GO状態のまま1秒以上検出されなかったらNOT_READYへ状態遷移
                        } else if (nowMilliSec - baseMilliSec >= 1000){
                            matchingStatus = NOT_READY;
                        }
                        break;
                }
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

    void setCapture(Capture capture) {
        this.capture = capture;
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
