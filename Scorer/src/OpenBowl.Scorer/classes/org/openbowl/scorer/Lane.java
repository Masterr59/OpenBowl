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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.prefs.Preferences;
import javafx.application.Platform;
import javafx.scene.Node;
import org.openbowl.common.BowlingPins;
import static org.openbowl.scorer.BasicPinSetter.POWER_PIN_SETTING_NAME;

/**
 *
 * A collection of devices that comprise a bowling lane This class will fire the
 * LaneEvents.SLOW_BALL and LaneEvents.BOWL_EVENT when the conditions are met.
 *
 * @author Open Bowl <http://www.openbowlscoring.org/>
 */
public class Lane extends Node {

    private final double SPEED_CONVERSION_RATE = 681.818;
    public final String BALL_SWEEP_DISTANCE_SETTING = "FoulSweepDistance";
    public final String SLOW_BALL_THRESHOLD_SETTING = "SlowBallThreshold";
    public final String FOUL_BALL_THRESHOLD_SETTING = "FoulBallThreshold";
    public final String PIN_COUNTER_DELAY_SETTING = "PinCounterDelay";

    public final double FOUL_SWEEP_DISTANCE_DEFAULT = 12 * 7;
    public final long SLOW_BALL_THRESHOLD_DEFAULT = 2000;
    public final long FOUL_BALL_THRESHOLD_DEFAULT = 5000;
    public final long PIN_COUNTER_DELAY_DEFAULT = 2000;

    private PinSetter pinSetter;
    private PinCounter pinCounter;
    private Detector sweep, ball, foul;
    private DisplayConnector display;
    private String name;
    private double foulSweepDistance;
    private long slowBallThreshold, foulBallThreshold, pinCounterDelay,
            lastBallDetected, lastSweepDetected, lastFoulDetected;

    private boolean lastBallFoul;
    private double lastBallSpeed;
    private ArrayList<BowlingPins> lastBallPins;
    private final Preferences prefs;
    private Timer timer;
    boolean scoreOnSweep;

    public Lane(String name) {
        this.name = name;
        lastBallDetected = 0;
        lastSweepDetected = 0;
        lastFoulDetected = 0;

        prefs = Preferences.userNodeForPackage(this.getClass());

        foulSweepDistance = prefs.getDouble(name + BALL_SWEEP_DISTANCE_SETTING, FOUL_SWEEP_DISTANCE_DEFAULT);
        slowBallThreshold = prefs.getLong(name + SLOW_BALL_THRESHOLD_SETTING, SLOW_BALL_THRESHOLD_DEFAULT);
        foulBallThreshold = prefs.getLong(name + FOUL_BALL_THRESHOLD_SETTING, FOUL_BALL_THRESHOLD_DEFAULT);
        pinCounterDelay = prefs.getLong(name + PIN_COUNTER_DELAY_SETTING, PIN_COUNTER_DELAY_DEFAULT);
        scoreOnSweep = true;
        timer = new Timer();

    }

    public void configureDialog() {
        try {
            LaneOptionsDialogController dialog = new LaneOptionsDialogController(this, name);
            dialog.setTitle(name);
            dialog.showAndWait();

        } catch (IOException e) {
            System.out.println("Error showing dialog " + e.toString());
            e.printStackTrace();
        }
        foulSweepDistance = prefs.getDouble(name + BALL_SWEEP_DISTANCE_SETTING, FOUL_SWEEP_DISTANCE_DEFAULT);
        slowBallThreshold = prefs.getLong(name + SLOW_BALL_THRESHOLD_SETTING, SLOW_BALL_THRESHOLD_DEFAULT);
        foulBallThreshold = prefs.getLong(name + FOUL_BALL_THRESHOLD_SETTING, FOUL_BALL_THRESHOLD_DEFAULT);
        pinCounterDelay = prefs.getLong(name + PIN_COUNTER_DELAY_SETTING, PIN_COUNTER_DELAY_DEFAULT);

    }

    public String setConfiguration(Map<String, Object> newConfig) {
        String results = "";
        try {
            String type = (String) newConfig.get("Type");
            double ballSweep = (double) newConfig.get(BALL_SWEEP_DISTANCE_SETTING);
            long slowBall = (new Double((double) newConfig.get(SLOW_BALL_THRESHOLD_SETTING))).longValue();
            long foulBall = (new Double((double) newConfig.get(FOUL_BALL_THRESHOLD_SETTING))).longValue();
            long pinCycle = (new Double((double) newConfig.get(PIN_COUNTER_DELAY_SETTING))).longValue();

            if (type.equals(this.getClass().getName())) {
                foulSweepDistance = ballSweep;
                slowBallThreshold = slowBall;
                foulBallThreshold = foulBall;
                pinCounterDelay = pinCycle;
                prefs.putDouble(name + BALL_SWEEP_DISTANCE_SETTING, ballSweep);
                prefs.putLong(name + SLOW_BALL_THRESHOLD_SETTING, new Double(slowBall).longValue());
                prefs.putLong(name + FOUL_BALL_THRESHOLD_SETTING, new Double(foulBall).longValue());
                prefs.putLong(name + PIN_COUNTER_DELAY_SETTING, new Double(pinCycle).longValue());
            } else {
                results += "Incorrect device type";
            }
        } catch (ClassCastException e) {
            results += e.getMessage();
        } catch (NullPointerException e) {
            results += "NullPointException: " + e.getMessage();
        }

        return results;

    }

    public Map<String, Object> getConfiguration() {
        Map<String, Object> ret = new HashMap<>();
        ret.put("Type", this.getClass().getName());
        ret.put(BALL_SWEEP_DISTANCE_SETTING, foulSweepDistance);
        ret.put(SLOW_BALL_THRESHOLD_SETTING, slowBallThreshold);
        ret.put(FOUL_BALL_THRESHOLD_SETTING, foulBallThreshold);
        ret.put(PIN_COUNTER_DELAY_SETTING, pinCounterDelay);
        return ret;
    }

    public PinSetter getPinSetter() {
        return pinSetter;
    }

    public void setPinSetter(PinSetter pinSetter) {
        this.pinSetter = pinSetter;
    }

    public PinCounter getPinCounter() {
        return pinCounter;
    }

    public void setPinCounter(PinCounter pinCounter) {
        this.pinCounter = pinCounter;
    }

    public Detector getSweep() {
        return sweep;
    }

    public void setSweep(Detector sweep) {
        this.sweep = sweep;
        sweep.addEventHandler(DetectedEvent.DETECTION, not_used -> onSweepDetected());
    }

    public Detector getBall() {
        return ball;
    }

    public void setBall(Detector ball) {
        this.ball = ball;
        ball.addEventHandler(DetectedEvent.DETECTION, not_used -> onBallDetected());
    }

    public Detector getFoul() {
        return foul;
    }

    public void setFoul(Detector foul) {
        this.foul = foul;
        foul.addEventHandler(DetectedEvent.DETECTION, not_used -> onFoulDetected());
    }

    private void onBallDetected() {
        lastBallDetected = System.currentTimeMillis();
        timer.schedule(new slowBallDetectTask(), slowBallThreshold);
    }

    private void onSweepDetected() {
        if (scoreOnSweep) {
            timer.cancel();
            timer = new Timer();
            lastSweepDetected = System.currentTimeMillis();
            lastBallSpeed = foulSweepDistance / (double) lastSweepDetected * SPEED_CONVERSION_RATE;
            lastBallFoul = ((lastSweepDetected - lastFoulDetected) < foulBallThreshold);
            timer.schedule(new pinCounterDelayTask(), pinCounterDelay);
        } else {
            System.out.println("Not scoring this cycle");
            scoreOnSweep = true;
        }
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

    public DisplayConnector getDisplay() {
        return display;
    }

    public void setDisplay(DisplayConnector display) {
        this.display = display;
    }

    public String getName() {
        return name;
    }

    private class pinCounterDelayTask extends TimerTask {

        @Override
        public void run() {
            lastBallPins = pinCounter.countPins();
            fireEvent(new LaneEvents(LaneEvents.BOWL_EVENT));
            timer.cancel();
            timer = new Timer();
            //timer.purge();
        }

    }

    private class slowBallDetectTask extends TimerTask {

        @Override
        public void run() {
            System.out.println("Slow ball detected");
            fireEvent(new LaneEvents(LaneEvents.SLOW_BALL));
            timer.cancel();
            timer = new Timer();
            Platform.runLater(new Runnable() {
                @Override
                public void run() {
                    pinSetter.cycle();
                }
            });
            //timer.schedule(new pinCounterDelayTask(), pinCounterDelay);
        }

    }

    public void cycleNoScore() {
        scoreOnSweep = false;
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                pinSetter.cycle();
            }
        });
    }

    public void shutdown() {
        timer.cancel();
        timer = null;
        pinSetter.setPower(false);
        pinSetter.teardown();
        ball.teardown();
        foul.teardown();
        sweep.teardown();
    }
}
