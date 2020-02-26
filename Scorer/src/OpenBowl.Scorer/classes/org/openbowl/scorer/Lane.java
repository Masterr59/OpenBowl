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

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;
import java.util.prefs.Preferences;
import javafx.scene.Node;
import org.openbowl.common.BowlingPins;

/**
 *
 * @author Open Bowl <http://www.openbowlscoring.org/>
 */
public class Lane extends Node {

    private final double SPEED_CONVERSION_RATE = 681.818;
    private final String FOUL_SWEEP_DISTANCE_SETTING = "FoulSweepDistance";
    private final String SLOW_BALL_THRESHOLD_SETTING = "SlowBallThreshold";
    private final String FOUL_BALL_THRESHOLD_SETTING = "FoulBallThreshold";
    private final String PIN_COUNTER_DELAY_SETTING = "PinCounterDelay";

    private final double FOUL_SWEEP_DISTANCE_DEFAULT = 12 * 7;
    private final long SLOW_BALL_THRESHOLD_DEFAULT = 2000;
    private final long FOUL_BALL_THRESHOLD_DEFAULT = 5000;
    private final long PIN_COUNTER_DELAY_DEFAULT = 2000;

    private PinSetter pinSetter;
    private PinCounter pinCounter;
    private Detector sweep, ball, foul;
    private String name;
    private double foulSweepDistance;
    private long slowBallThreshold, foulBallThreshold, pinCounterDelay,
            lastBallDetected, lastSweepDetected, lastFoulDetected;

    private boolean lastBallFoul;
    private double lastBallSpeed;
    private ArrayList<BowlingPins> lastBallPins;
    private final Preferences prefs;
    private Timer timer;

    public Lane(String name) {
        this.name = name;
        lastBallDetected = 0;
        lastSweepDetected = 0;
        lastFoulDetected = 0;

        prefs = Preferences.userNodeForPackage(this.getClass());

        foulSweepDistance = prefs.getDouble(FOUL_SWEEP_DISTANCE_SETTING, FOUL_SWEEP_DISTANCE_DEFAULT);
        slowBallThreshold = prefs.getLong(SLOW_BALL_THRESHOLD_SETTING, SLOW_BALL_THRESHOLD_DEFAULT);
        foulBallThreshold = prefs.getLong(FOUL_BALL_THRESHOLD_SETTING, FOUL_BALL_THRESHOLD_DEFAULT);
        pinCounterDelay = prefs.getLong(PIN_COUNTER_DELAY_SETTING, PIN_COUNTER_DELAY_DEFAULT);

        timer = new Timer();

        ball.addEventHandler(DetectedEvent.DETECTION, not_used -> onBallDetected());
        sweep.addEventHandler(DetectedEvent.DETECTION, not_used -> onSweepDetected());
        foul.addEventHandler(DetectedEvent.DETECTION, not_used -> onFoulDetected());
    }

    public PinSetter getPinSetter() {
        return pinSetter;
    }

    public void setPinSetter(PinSetter pinSetter) {
        this.pinSetter = pinSetter;
    }

    public Detector getSweep() {
        return sweep;
    }

    public void setSweep(Detector sweep) {
        this.sweep = sweep;
    }

    public Detector getBall() {
        return ball;
    }

    public void setBall(Detector ball) {
        this.ball = ball;
    }

    public Detector getFoul() {
        return foul;
    }

    public void setFoul(Detector foul) {
        this.foul = foul;
    }

    private void onBallDetected() {
        lastBallDetected = System.currentTimeMillis();
        timer.schedule(new slowBallDetectTask(), slowBallThreshold);
    }

    private void onSweepDetected() {
        lastSweepDetected = System.currentTimeMillis();
        long timeTaken = lastSweepDetected - lastBallDetected;
        //feet / millis
        lastBallSpeed = foulSweepDistance / (double) timeTaken * SPEED_CONVERSION_RATE;
        lastBallFoul = ((lastSweepDetected - lastFoulDetected) < foulBallThreshold);
        timer.schedule(new pinCounterDelayTask(), pinCounterDelay);
    }

    private void onFoulDetected() {
        lastFoulDetected = System.currentTimeMillis();
    }

    public boolean isLastBallFoul() {
        return lastBallFoul;
    }

    public double getLastBallSpeed() {
        return lastBallSpeed;
    }

    public ArrayList<BowlingPins> getLastBallPins() {
        return lastBallPins;
    }

    private class pinCounterDelayTask extends TimerTask {

        @Override
        public void run() {
            lastBallPins = pinCounter.countPins();
            fireEvent(new LaneEvents(LaneEvents.BOWL_EVENT));
            timer.cancel();
            timer.purge();
        }

    }

    private class slowBallDetectTask extends TimerTask {

        @Override
        public void run() {
            fireEvent(new LaneEvents(LaneEvents.SLOW_BALL));
            timer.cancel();
            pinSetter.cycle();
            timer.schedule(new pinCounterDelayTask(), pinCounterDelay);
        }

    }

}
