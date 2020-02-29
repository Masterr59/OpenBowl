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
import java.util.HashMap;
import java.util.Map;
import javafx.stage.Modality;

/**
 *
 * @author Open Bowl <http://www.openbowlscoring.org/>
 */
public class FakePinSetter implements PinSetter {

    private boolean power = false;
    private String name;
    FakePinSetterDialogController dialog;
    FakeBowlerDialogController bowler;
    Detector sweep;
    int ball;

    public FakePinSetter(String name) {
        ball = 0;
        this.name = name;
        try {
            dialog = new FakePinSetterDialogController(name, this);
            dialog.initModality(Modality.NONE);
            dialog.show();
        } catch (IOException e) {
            System.out.println("Error showing dialog " + e.toString());
        }
    }

    public void setBall(int ball) {
        this.ball = ball;
    }

    public int getBall() {
        return ball;
    }

    public PinCounter getPinCounter() {
        return dialog;
    }

    public FakePinSetterDialogController getDialog() {
        return dialog;
    }

    public void setBowler(FakeBowlerDialogController bowler) {
        this.bowler = bowler;
    }

    @Override
    public void configureDialog() {
        log("show configuration dialog");
    }

    @Override
    public String setConfiguration(Map<String, Object> configuration) {
        log("set configuration");
        return "";
    }

    @Override
    public Map<String, Object> getConfiguration() {
        Map<String, Object> map = new HashMap<>();
        map.put("Type", this.getClass().getName());
        log("get configuration");
        return map;
    }

    @Override
    public void setPower(boolean state) {
        log("set power state: " + state);
        dialog.setPower(state);
        power = state;
    }

    @Override
    public boolean getPowerState() {
        log("get power state: " + power);
        return power;
    }

    public void setSweep(Detector sweep) {
        this.sweep = sweep;
    }

    @Override
    public void cycle() {
        if (power) {
            ball++;
            ball = ball % 2;
            dialog.setBall(ball);
            if (ball == 0) {
                //dialog.resetPins();
            }
            //bowler.onCycle();
            log("cycle");
            sweep.fireDetectedEvent();
        } else {
            log("cycled when powered off");
        }
    }

    @Override
    public String setup() {
        log("setup");
        return "";
    }

    @Override
    public void teardown() {
        log("teardown");

    }

    @Override
    public void log(String s) {
        PinSetter.super.log(name + " " + s);
    }

}
