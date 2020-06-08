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
package org.openbowl.scorer.remote;

import com.google.gson.JsonSyntaxException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.prefs.Preferences;
import javafx.application.Platform;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import org.openbowl.common.SystemHandler;
import org.openbowl.common.SystemStatus;
import org.openbowl.scorer.MainApp;

/**
 *
 * @author Open Bowl <http://www.openbowlscoring.org/>
 */
public class ScorerSystemHandler extends SystemHandler {

    private final MainApp app;
    private Timer timer;
    private final Preferences prefs;
    private final BooleanProperty oddRunningProperty, evenRunningProperty;

    public ScorerSystemHandler(MainApp app, BooleanProperty oddRunning, BooleanProperty evenRunning) {
        prefs = Preferences.userNodeForPackage(this.getClass());
        this.app = app;
        timer = new Timer();
        oddRunningProperty = new SimpleBooleanProperty();
        oddRunningProperty.bind(oddRunning);
        evenRunningProperty = new SimpleBooleanProperty();
        evenRunningProperty.bind(evenRunning);
    }

    @Override
    protected void onQuit() {
        timer.schedule(new onQuitTask(), 1000);
    }

    @Override
    protected Map<String, Object> onGet(Map<String, String> parms) {
        Map<String, Object> map = new HashMap<>();
        switch (parms.getOrDefault("get", "none")) {
            case "status":
                map.put("status", getSystemStatus());
                break;
            default:
                map.put(SUCCESS, false);
                map.put(ERROR_MSG, "missing or unsupported request");
                break;
        }
        return map;
    }

    @Override
    protected Map<String, Object> onPost(Map<String, String> parms, String body) {
        Map<String, Object> map = new HashMap<>();
        Map<String, Object> requestBody = new HashMap<>();
        try {
            requestBody = gson.fromJson(body, Map.class);
        } catch (JsonSyntaxException e) {
            map.put(SUCCESS, false);
            map.put(ERROR_MSG, e.getMessage());
            return map;
        }
        try {
            switch (parms.getOrDefault("set", "none")) {
                case "quitProgram":
                    onQuit();
                    map.put(SUCCESS, true);
                    break;
                default:
                    map.put(SUCCESS, false);
                    map.put(ERROR_MSG, "missing or unsupported request");
                    break;
            }
        } catch (ClassCastException e) {
            map.put(SUCCESS, false);
            map.put(ERROR_MSG, "missing or unsupported request");
        }
        return map;
    }

    private class onQuitTask extends TimerTask {

        @Override
        public void run() {

            timer.cancel();
            timer = new Timer();
            Platform.runLater(new Runnable() {
                @Override
                public void run() {
                    app.onQuit();
                }
            });
            //timer.schedule(new pinCounterDelayTask(), pinCounterDelay);
        }

    }

    @Override
    protected ArrayList<SystemStatus> getSystemStatus() {
        ArrayList<SystemStatus> status = super.getSystemStatus();
        System.out.println("System Status: Session Running Check");
        if (oddRunningProperty.get()) {
            status.add(SystemStatus.ODD_GAME_RUNNING);
        } else {
            status.add(SystemStatus.ODD_GAME_IDLE);
        }
        if (evenRunningProperty.get()) {
            status.add(SystemStatus.EVEN_GAME_RUNNING);
        } else {
            status.add(SystemStatus.EVEN_GAME_IDLE);
        }
        return status;
    }

}
