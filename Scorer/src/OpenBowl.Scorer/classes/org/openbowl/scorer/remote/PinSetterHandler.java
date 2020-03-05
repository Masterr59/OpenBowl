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

import java.util.HashMap;
import java.util.Map;
import org.openbowl.scorer.PinSetter;
import com.google.gson.JsonSyntaxException;
import org.openbowl.common.CommonHandler;

/**
 *
 * @author Open Bowl <http://www.openbowlscoring.org/>
 */
public class PinSetterHandler extends CommonHandler {

    private final PinSetter pinSetter;
    private final int lane;

    public PinSetterHandler(PinSetter pinSetter, int lane) {
        super();
        this.pinSetter = pinSetter;
        this.lane = lane;

    }

    @Override
    protected Map<String, Object> onGet(Map<String, String> parms) {
        Map<String, Object> map = new HashMap<>();
        map.put("Lane", lane);
        switch (parms.getOrDefault("get", "none")) {
            case "power":
                map.put(SUCCESS, true);
                map.put("Power", pinSetter.getPowerState());
                break;
            case "config":
                map.put(SUCCESS, true);
                map.put("CurentConfig", pinSetter.getConfiguration());
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
        map.put("Lane", lane);
        //System.out.println(body);
        Map<String, Object> requestBody = new HashMap<>();
        try {
            requestBody = gson.fromJson(body, Map.class);
        } catch (JsonSyntaxException e) {
            map.put(SUCCESS, false);
            map.put(ERROR_MSG, e.getMessage());
            return map;
        }
        //System.out.println(gson.toJson(requestBody));
        try {
            switch (parms.getOrDefault("set", "none")) {
                case "power":
                    boolean powerState = (boolean) requestBody.get("state");
                    pinSetter.setPower(powerState);
                    map.put(SUCCESS, true);
                    map.put("Power", pinSetter.getPowerState());
                    break;
                case "config":
                    String results = pinSetter.setConfiguration(requestBody);
                    if (results.isBlank()) {
                        map.put(SUCCESS, true);
                        map.put("CurentConfig", pinSetter.getConfiguration());
                    } else {
                        map.put(SUCCESS, false);
                        map.put(ERROR_MSG, results);
                    }
                    break;
                case "cycle":
                    if (pinSetter.getPowerState()) {
                        pinSetter.cycle();
                        map.put(SUCCESS, true);
                    } else {
                        map.put(SUCCESS, false);
                        map.put(ERROR_MSG, "Pinsetter is off");
                    }
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
}
