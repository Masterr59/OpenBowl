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

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import java.io.IOException;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.openbowl.scorer.PinSetter;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import java.io.StringWriter;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Iterator;
import org.openbowl.common.AuthorizedUser;
import org.openbowl.common.WebFunctions;

/**
 *
 * @author Open Bowl <http://www.openbowlscoring.org/>
 */
public class PinSetterHandler implements HttpHandler {

    private final String SUCCESS = "success";
    private final String ERROR_MSG = "error message";
    private final PinSetter pinSetter;
    private final int lane;
    private final Gson gson;
    private final List<AuthorizedUser> users;

    public PinSetterHandler(PinSetter pinSetter, int lane) {
        this.pinSetter = pinSetter;
        this.lane = lane;
        gson = new Gson();
        users = new ArrayList();
        Calendar exp = Calendar.getInstance();
        exp.add(Calendar.YEAR, 10);
        users.add(new AuthorizedUser("yZ9Ut95MG3xdf5gc6WgT", exp));
    }

    public void addAuthorizedUser(AuthorizedUser u) {
        users.add(u);
    }

    @Override
    public void handle(HttpExchange he) throws IOException {
        Map<String, List<String>> headers = he.getRequestHeaders();
        String method = he.getRequestMethod();
        if (isAuthorized(headers)) {
            Map<String, String> parms = WebFunctions.queryToMap(he.getRequestURI().getQuery());
            he.getResponseHeaders().set("Content-Type", "application/json");
            OutputStream os = he.getResponseBody();
            Map<String, Object> response = new HashMap<>();
            switch (method) {
                case WebFunctions.GET_METHOD: {
                    response.putAll(onGet(parms));
                    break;
                }
                case WebFunctions.POST_METHOD: {
                    String body = new String(he.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);
                    response.putAll(onPost(parms, body));
                    break;
                }
                default: {
                    response.put(SUCCESS, false);
                    response.put(ERROR_MSG, "unsupported method");
                    break;
                }

            }

            String jsonResponse = gson.toJson(response);
            he.sendResponseHeaders(200, jsonResponse.length());
            os.write(jsonResponse.getBytes());
            os.close();

        } else {
            //sends 403 Forbidden
            he.sendResponseHeaders(403, 0);
            he.getResponseBody().close();
        }
    }

    private boolean isAuthorized(Map<String, List<String>> headers) {
        Iterator<AuthorizedUser> itr = users.listIterator();
        while (itr.hasNext()) {
            AuthorizedUser u = itr.next();
            if (u.isAuthorized(headers)) {
                return true;
            }
            if (u.isExpired()) {
                itr.remove();
            }
        }
        return false;
    }

    private Map<String, Object> onGet(Map<String, String> parms) {
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

    private Map<String, Object> onPost(Map<String, String> parms, String body) {
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
                    if(pinSetter.getPowerState()){
                        pinSetter.cycle();
                        map.put(SUCCESS, true);
                    }
                    else{
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
