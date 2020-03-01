/*
 * Copyright (C) 2020 Open Bowl <http://www.openbowlscoring.org/>
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.openbowl.scorer;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.animation.AnimationTimer;
import javafx.beans.binding.Bindings;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.Slider;

/**
 *
 * @author Open Bowl <http://www.openbowlscoring.org/>
 */
public class FakeBowlerDialogController extends Dialog<Void> implements Initializable {

    @FXML
    private Button bowl;
    @FXML
    private Button foulButton;
    @FXML
    private CheckBox sweepTrigger;
    @FXML
    private CheckBox foulTrigger;
    @FXML
    private Slider ballSpeed;
    @FXML
    private ProgressBar ballProgress;
    @FXML
    private ProgressBar cycleProgress;
    @FXML
    private Label speedLabel;

    private FakePinSetter pinsetter;
    private FakePinSetterDialogController dialog;
    private FakeDetector sweep, ball, foul;
    private String name;
    private final String desc = "Fake Bowler - ";
    private AnimationTimer ballTimer, cycleTimer;
    private long ballPreviousTime, cyclePreviousTime, ballStartTime, cycleStartTime,
            ballSweepTime, cycleEndTime, ballDetectTime;
    private final long TIMER_MSEC = 10;
    private int cycleDelay = 2000;

    public FakeBowlerDialogController(String n) throws IOException {

        this.name = n;
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/openbowl/scorer/FakeBowlerDialog.fxml"));
        loader.setController(this);
        Parent root = loader.load();
        getDialogPane().setContent(root);

        setTitle("Fake " + n);
        bowl.setOnAction(notUsed -> onBowl());
        foulButton.setOnAction(notUsed -> foul.fireDetectedEvent());
        pinsetter = new FakePinSetter(n);
        pinsetter.setBowler(this);
        dialog = pinsetter.getDialog();
        sweep = new FakeDetector(n + "SweepDetector");
        pinsetter.setSweep(sweep);
        ball = new FakeDetector(n + "BallDetector");
        foul = new FakeDetector(n + "FoulDetector");
        ballProgress.setProgress(0);
        cycleProgress.setProgress(0);
        speedLabel.textProperty().bind(Bindings.format("Ball Speed: %.1f mph", ballSpeed.valueProperty()));

        ballTimer = new AnimationTimer() {
            @Override
            public void handle(long now) {
                onBallTimer(now);
            }

        };

        cycleTimer = new AnimationTimer() {
            @Override
            public void handle(long now) {
                onCycleTimer(now);
            }

        };
    }

    public void onCycle() {
        long now = System.currentTimeMillis();
        if (cycleEndTime < now) {
            sweep.fireDetectedEvent();
            startCycleTimer();
        }
    }

    public void startBallTimer() {
        ballPreviousTime = System.currentTimeMillis();
        ballStartTime = ballPreviousTime;

        double fpms = ballSpeed.getValue() * 1.4667;
        log("Balls Speed " + String.format("%.2f", fpms) + " fps");
        ballSweepTime = ballStartTime + (long) (67.0 / fpms * 1000.0);
        ballDetectTime = ballStartTime + (long) (60.0 / fpms * 1000.0);
        log("Time till ball Detect: " + (ballDetectTime - ballStartTime));
        log("Time till ball sweep: " + (ballSweepTime - ballStartTime));
        if (foulTrigger.isSelected()) {
            foul.fireDetectedEvent();
        }
        ballTimer.start();
    }

    public void stopBallTimer() {
        ballPreviousTime = System.currentTimeMillis();
        ballTimer.stop();
    }

    private void onBallTimer(long now) {
        now = System.currentTimeMillis();
        long elapsed = (now - ballPreviousTime);
        if (elapsed > TIMER_MSEC) {
            long totalTime = ballSweepTime - ballStartTime;
            long timeElapsed = now - ballStartTime;
            double progress = (double) timeElapsed / (double) totalTime;
            //System.out.println(progress);
            ballProgress.setProgress(progress);
            // do some of the work and quit when done
            if (now > ballDetectTime) {
                ball.fireDetectedEvent();
                ballDetectTime = ballSweepTime * 2;
            }
            if (now > ballSweepTime) {

                stopBallTimer();
                if (sweepTrigger.isSelected()) {
                    startCycleTimer();
                }
            }

            ballPreviousTime = now;
        }

    }

    public void startCycleTimer() {
        pinsetter.cycle();
    }
    
    public void startCycleTimerGUI() {
        //pinsetter.cycle();
        cyclePreviousTime = System.currentTimeMillis();
        cycleStartTime = cyclePreviousTime;
        cycleEndTime = cycleStartTime + cycleDelay;
        cycleTimer.start();
    }

    public void stopCycleTimer() {
        cyclePreviousTime = System.currentTimeMillis();
        cycleTimer.stop();
        if (pinsetter.getBall() == 2) {
            dialog.setBall(0);
            pinsetter.setBall(0);
        }
        ballProgress.setProgress(0);
        cycleProgress.setProgress(0);
    }

    private void onCycleTimer(long now) {
        now = System.currentTimeMillis();
        long elapsed = (now - cyclePreviousTime);
        if (elapsed > TIMER_MSEC) {
            // do some of the work and quit when done
            long totalTime = cycleEndTime - cycleStartTime;
            long timeElapsed = now - cycleStartTime;
            double progress = (double) timeElapsed / (double) totalTime;
            cycleProgress.setProgress(progress);
            if (now > cycleEndTime) {

                stopCycleTimer();
            }

            cyclePreviousTime = now;
        }

    }

    public FakePinSetter getPinsetter() {
        return pinsetter;
    }

    public FakeDetector getSweep() {
        return sweep;
    }

    public FakeDetector getFoul() {
        return foul;
    }

    public FakeDetector getBall() {
        return ball;
    }

    public PinCounter getCounter() {
        return pinsetter.getPinCounter();
    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {

    }

    private void onBowl() {
        startBallTimer();
    }

    private void log(String s) {
        System.out.println(desc + name + " " + s);
    }
}
