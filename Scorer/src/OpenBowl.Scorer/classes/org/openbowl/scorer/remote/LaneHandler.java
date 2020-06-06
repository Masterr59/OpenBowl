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
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.openbowl.common.CommonHandler;
import org.openbowl.scorer.Lane;

/**
 *
 * @author Open Bowl <http://www.openbowlscoring.org/>
 */
public class LaneHandler extends CommonHandler {

    private final Lane lane;

    public LaneHandler(Lane lane) {
        super();
        this.lane = lane;
    }

    @Override
    protected Map<String, Object> onGet(Map<String, String> parms) {
        Map<String, Object> map = new HashMap<>();
        map.put("lane", lane.getName());
        switch (parms.getOrDefault("get", "none")) {
            case "pinSetterPower":
                map.put(SUCCESS, true);
                map.put("Power", lane.getPinSetter().getPowerState());
                break;
            case "pinSetterConfig":
                map.put(SUCCESS, true);
                map.put("CurentConfig", lane.getPinSetter().getConfiguration());
                break;
            case "pinCounterConfig":
                map.put(SUCCESS, true);
                map.put("CurentConfig", lane.getPinCounter().getConfiguration());
                break;
            case "sweepDetectConfig":
                map.put(SUCCESS, true);
                map.put("CurentConfig", lane.getSweep().getConfiguration());
                break;
            case "foulDetectConfig":
                map.put(SUCCESS, true);
                map.put("CurentConfig", lane.getFoul().getConfiguration());
                break;
            case "ballDetectConfig":
                map.put(SUCCESS, true);
                map.put("CurentConfig", lane.getBall().getConfiguration());
                break;
            case "displayConnectorConfig":
                map.put(SUCCESS, true);
                map.put("CurentConfig", lane.getDisplay().getConfiguration());
                break;
            case "laneConfig":
                map.put(SUCCESS, true);
                map.put("CurentConfig", lane.getConfiguration());
                break;
            case "lastImage":
                map.put(SUCCESS, true);
                map.put("file", getLastImage());
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
        map.put("Lane", lane.getName());
        Map<String, Object> requestBody = new HashMap<>();
        try {
            requestBody = gson.fromJson(body, Map.class);
        } catch (JsonSyntaxException e) {
            map.put(SUCCESS, false);
            map.put(ERROR_MSG, e.getMessage());
            return map;
        }
        try {
            String results;
            switch (parms.getOrDefault("set", "none")) {
                case "pinSetterPower":
                    boolean powerState = (boolean) requestBody.get("state");
                    lane.getPinSetter().setPower(powerState);
                    map.put(SUCCESS, true);
                    map.put("Power", lane.getPinSetter().getPowerState());
                    break;
                case "pinSetterConfig":
                    results = lane.getPinSetter().setConfiguration(requestBody);
                    if (results.isBlank()) {
                        map.put(SUCCESS, true);
                        map.put("CurentConfig", lane.getPinSetter().getConfiguration());
                    } else {
                        map.put(SUCCESS, false);
                        map.put(ERROR_MSG, results);
                    }
                    break;
                case "pinSetterCycle":
                    if (lane.getPinSetter().getPowerState()) {
                        lane.getPinSetter().cycle();
                        map.put(SUCCESS, true);
                    } else {
                        map.put(SUCCESS, false);
                        map.put(ERROR_MSG, "Pinsetter is off");
                    }
                    break;
                case "pinSetterCycleNoScore":
                    if (lane.getPinSetter().getPowerState()) {
                        lane.cycleNoScore();
                        map.put(SUCCESS, true);
                    } else {
                        map.put(SUCCESS, false);
                        map.put(ERROR_MSG, "Pinsetter is off");
                    }
                    break;
                case "pinCounterConfig":
                    results = lane.getPinCounter().setConfiguration(requestBody);
                    if (results.isBlank()) {
                        map.put(SUCCESS, true);
                        map.put("CurentConfig", lane.getPinCounter().getConfiguration());
                    } else {
                        map.put(SUCCESS, false);
                        map.put(ERROR_MSG, results);
                    }
                    break;
                case "sweepDetectConfig":
                    results = lane.getSweep().setConfiguration(requestBody);
                    if (results.isBlank()) {
                        map.put(SUCCESS, true);
                        map.put("CurentConfig", lane.getSweep().getConfiguration());
                    } else {
                        map.put(SUCCESS, false);
                        map.put(ERROR_MSG, results);
                    }
                    break;
                case "foulDetectConfig":
                    results = lane.getFoul().setConfiguration(requestBody);
                    if (results.isBlank()) {
                        map.put(SUCCESS, true);
                        map.put("CurentConfig", lane.getFoul().getConfiguration());
                    } else {
                        map.put(SUCCESS, false);
                        map.put(ERROR_MSG, results);
                    }
                    break;
                case "ballDetectConfig":
                    results = lane.getBall().setConfiguration(requestBody);
                    if (results.isBlank()) {
                        map.put(SUCCESS, true);
                        map.put("CurentConfig", lane.getBall().getConfiguration());
                    } else {
                        map.put(SUCCESS, false);
                        map.put(ERROR_MSG, results);
                    }
                    break;
                case "displayConnectorConfig":
                    results = lane.getDisplay().setConfiguration(requestBody);
                    if (results.isBlank()) {
                        map.put(SUCCESS, true);
                        map.put("CurentConfig", lane.getDisplay().getConfiguration());
                    } else {
                        map.put(SUCCESS, false);
                        map.put(ERROR_MSG, results);
                    }
                    break;
                case "laneConfig":
                    results = lane.setConfiguration(requestBody);
                    if (results.isBlank()) {
                        map.put(SUCCESS, true);
                        map.put("CurentConfig", lane.getConfiguration());
                    } else {
                        map.put(SUCCESS, false);
                        map.put(ERROR_MSG, results);
                    }
                    break;
                default:
                    map.put(SUCCESS, false);
                    map.put(ERROR_MSG, "missing or unsupported request");
                    break;

            }
        } catch (ClassCastException e) {
            map.put(SUCCESS, false);
            map.put(ERROR_MSG, e.toString());
        }
        return map;
    }

    private byte[] getLastImage() {
        File f = new File("currentPinImage.png");
        if (f.exists() && f.canRead()) {
            try {
                return Files.readAllBytes(f.toPath());
            } catch (IOException ex) {
                System.out.println("Error opening file " + ex.toString());
            }
        }
        return new String("Error").getBytes();
    }
}
