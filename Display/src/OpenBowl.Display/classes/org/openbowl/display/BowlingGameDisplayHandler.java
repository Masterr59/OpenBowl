/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.openbowl.display;

import com.google.gson.JsonSyntaxException;
import java.util.HashMap;
import java.util.Map;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import org.openbowl.common.BowlingGame;
import org.openbowl.common.CommonHandler;

/**
 *
 * @author Open Bowl <http://www.openbowlscoring.org/>
 */
public class BowlingGameDisplayHandler extends CommonHandler {

    private final int lane;
    private final BowlingGameDisplay game;
    private BooleanProperty saverProperty;
    private BooleanProperty powerProperty;

    public BowlingGameDisplayHandler(int lane, BowlingGameDisplay g) {
        super();
        this.lane = lane;
        this.game = g;
        this.saverProperty = new SimpleBooleanProperty(false);
        this.powerProperty = new SimpleBooleanProperty(true);
    }

    @Override
    protected Map<String, Object> onGet(Map<String, String> parms) {
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

    @Override
    protected Map<String, Object> onPost(Map<String, String> parms, String body) {
        Map<String, Object> map = new HashMap<>();
        map.put("Lane", lane);
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
                case "currentPlayer":
                    try {
                        int newCurrentPlayer = (new Double((double) requestBody.get("player"))).intValue();
                        System.out.printf("Lane %d - Setting current player to %d\n", lane, newCurrentPlayer);
                        if (newCurrentPlayer < game.getNumPlayers()) {
                            game.setCurentPlayer(newCurrentPlayer);
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
                    try {
                        int playerNumber = Integer.parseInt(parms.get("player"));
                        if (playerNumber < game.getNumPlayers()) {
                            BowlingGame newScore = gson.fromJson(body, BowlingGame.class);
                            System.out.printf("Lane %d - Updating player %d, %s\n", lane, playerNumber, newScore.toString());
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
                        System.out.printf("Lane %d - Adding Player: %s\n", lane, newPlayer.toString());
                        map.put(SUCCESS, true);
                        map.put("player", game.getNumPlayers() - 1);

                    } catch (Exception e) {
                        map.put(SUCCESS, false);
                        map.put(ERROR_MSG, e.toString());
                    }
                    break;
                case "newGame":
                    try {
                        System.out.printf("lane %d - Resetting game display", lane);
                        game.reset();
                        map.put(SUCCESS, true);

                    } catch (Exception e) {
                        map.put(SUCCESS, false);
                        map.put(ERROR_MSG, e.toString());
                    }
                    break;
                case "screenPower":
                    try {
                        Map<String, Object> postData = gson.fromJson(body, Map.class);
                        if (postData.containsKey("state") && postData.get("state") instanceof Boolean) {
                            boolean state = ((Boolean) postData.get("state")).booleanValue();
                            System.out.printf("lane %d - screen power :" + state, lane);
                            this.powerProperty.set(state);
                            map.put(SUCCESS, true);
                        } else {
                            System.out.printf("lane %d - screen power parm error", lane);
                            map.put(SUCCESS, false);
                        }
                    } catch (Exception e) {
                        map.put(SUCCESS, false);
                        map.put(ERROR_MSG, e.toString());
                    }
                    break;
                case "screenSaver":
                    try {
                        Map<String, Object> postData = gson.fromJson(body, Map.class);
                        if (postData.containsKey("state") && postData.get("state") instanceof Boolean) {
                            boolean state = ((Boolean) postData.get("state")).booleanValue();
                            System.out.printf("lane %d - screen saver :" + state, lane);
                            this.saverProperty.set(state);
                            map.put(SUCCESS, true);
                        } else {
                            System.out.printf("lane %d - screen saver parm error", lane);
                            map.put(SUCCESS, false);
                        }
                    } catch (Exception e) {
                        map.put(SUCCESS, false);
                        map.put(ERROR_MSG, e.toString());
                    }
                    break;
                default:
                    System.out.printf("lane %d - Unknown API Call %s\n", lane, parms.getOrDefault("set", "none"));
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
