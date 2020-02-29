/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.openbowl.display;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import java.io.IOException;
import java.util.List;
import org.openbowl.common.AuthorizedUser;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import org.openbowl.common.BowlingGame;
import org.openbowl.common.WebFunctions;

/**
 *
 * @author Open Bowl <http://www.openbowlscoring.org/>
 */
public class BowlingGameDisplayHandler implements HttpHandler {

    private final String SUCCESS = "success";
    private final String ERROR_MSG = "error message";
    private final int lane;
    private final Gson gson;
    private final List<AuthorizedUser> users;
    private final BowlingGameDisplay game;

    public BowlingGameDisplayHandler(int lane, BowlingGameDisplay g) {
        this.lane = lane;
        gson = new Gson();
        this.game = g;
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
        //System.out.print("Handle");
        Map<String, List<String>> headers = he.getRequestHeaders();
        String method = he.getRequestMethod();
        if (isAuthorized(headers)) {
            //System.out.println("Authorized");
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
            //System.out.println("Response: " + jsonResponse);
            he.sendResponseHeaders(200, jsonResponse.length());
            os.write(jsonResponse.getBytes());
            os.close();

        } else {
            //sends 403 Forbidden
            he.sendResponseHeaders(403, 0);
            he.getResponseBody().close();
        }
    }

    private Map<String, Object> onGet(Map<String, String> parms) {
        Map<String, Object> map = new HashMap<>();
        map.put("Lane", lane);
        switch (parms.getOrDefault("get", "none")) {
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
                case "currentPlayer":
                    try {
                        int newCurrentPlater = (int) requestBody.get("player");
                        if (newCurrentPlater < game.getNumPlayers()) {
                            game.setCurentPlayer(newCurrentPlater);
                            map.put(SUCCESS, true);
                            map.put("currentPlayer", game.getCurentPlayer());
                        } else {
                            map.put(SUCCESS, false);
                            map.put(ERROR_MSG, "Player number > number of players.");
                        }
                    } catch (Exception e) {
                        map.put(SUCCESS, false);
                        map.put(ERROR_MSG, e.toString());
                    }
                    break;
                case "playerScore":
                    //System.out.println("PlayerScore");
                    try {
                        int playerNumber = Integer.parseInt(parms.get("player"));
                        if (playerNumber < game.getNumPlayers()) {
                            System.out.println(body);
                            BowlingGame newScore = gson.fromJson(body, BowlingGame.class);
                            game.updatePlater(playerNumber, newScore);
                            map.put(SUCCESS, true);
                            map.put("player", playerNumber);
                        } else {
                            map.put(SUCCESS, false);
                            map.put(ERROR_MSG, "Player number > number of players.");
                        }
                    } catch (Exception e) {
                        map.put(SUCCESS, false);
                        map.put(ERROR_MSG, e.toString());
                    }
                    break;
                case "newPlayer":
                    
                    try {
                        BowlingGame newPlayer = gson.fromJson(body, BowlingGame.class);
                        game.addPlayer(newPlayer);
                        map.put(SUCCESS, true);
                        map.put("player", game.getNumPlayers() - 1);

                    } catch (Exception e) {
                        map.put(SUCCESS, false);
                        map.put(ERROR_MSG, e.toString());
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
}
