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

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.openbowl.common.BowlingGame;
import org.openbowl.common.BowlingSplash;
import org.openbowl.common.WebFunctions;

/**
 *
 * @author Open Bowl <http://www.openbowlscoring.org/>
 */
public class DisplayConnector {

    private final String address;
    private final String endpoint;
    private final Gson gson;
    private final String authToken;

    public DisplayConnector(String address, String endpoint, String authToken) {
        this.address = address;
        this.endpoint = endpoint;
        this.authToken = authToken;
        gson = new Gson();
    }

    public Map<String, Object> setCurentPlayer(int player) {
        String parms = "?set=currentPlayer";
        Map<String, Integer> map = new HashMap<>();
        map.put("player", player);
        String response = "{}";
        try {
            response = WebFunctions.doHttpPostRequest(address, "gamedisplay/" + endpoint + "/" + parms, gson.toJson(map), authToken);
        } catch (IOException | InterruptedException ex) {
            Logger.getLogger(DisplayConnector.class.getName()).log(Level.SEVERE, null, ex);
        }
        return processResponse(response);
    }

    public Map<String, Object> setScore(BowlingGame g, int player) {
        String parms = "?set=playerScore&player=" + Integer.toString(player);
        String response = "{}";
        try {
            //System.out.println("send to display player: " + player + " data: " + gson.toJson(g));
            //System.out.println(address + "gamedisplay/" + endpoint + parms);
            response = WebFunctions.doHttpPostRequest(address, "gamedisplay/" + endpoint + "/" + parms, gson.toJson(g), authToken);
        } catch (IOException | InterruptedException ex) {
            Logger.getLogger(DisplayConnector.class.getName()).log(Level.SEVERE, null, ex);
        }
        //System.out.println("no error");
        //System.out.println(response);
        return processResponse(response);
    }

    public Map<String, Object> newPlayer(BowlingGame g) {
        String parms = "?set=newPlayer";
        String response = "{}";
        try {
            response = WebFunctions.doHttpPostRequest(address, "gamedisplay/" + endpoint + "/" + parms, gson.toJson(g), authToken);
        } catch (IOException | InterruptedException ex) {
            Logger.getLogger(DisplayConnector.class.getName()).log(Level.SEVERE, null, ex);
        }
        return processResponse(response);
    }

    public Map<String, Object> showSplash(BowlingSplash type) {
        String parms = "?set=splash";
        Map<String, String> map = new HashMap<>();
        map.put("type", type.toString());
        String response = "{}";
        try {
            response = WebFunctions.doHttpPostRequest(address, "splash/" + endpoint + "/" + parms, gson.toJson(map), authToken);
        } catch (IOException | InterruptedException ex) {
            Logger.getLogger(DisplayConnector.class.getName()).log(Level.SEVERE, null, ex);
        }
        return processResponse(response);
    }

    public Map<String, Object> showMessageCard(String type, int duration) {
        String parms = "?set=card";
        Map<String, Object> map = new HashMap<>();
        map.put("type", type);
        map.put("duration", duration);
        String response = "{}";
        try {
            response = WebFunctions.doHttpPostRequest(address, "message/" + endpoint + "/" + parms, gson.toJson(map), authToken);
        } catch (IOException | InterruptedException ex) {
            Logger.getLogger(DisplayConnector.class.getName()).log(Level.SEVERE, null, ex);
        }
        return processResponse(response);
    }

    private Map<String, Object> processResponse(String response) {
        Map<String, Object> ret = new HashMap<>();
        if (response.contains("{") && response.contains("}")) {
            try {
                ret = gson.fromJson(response, Map.class);
            } catch (JsonSyntaxException ex) {
                System.out.println(response);
                System.out.println(ex.toString());
            }
        }
        return ret;
    }
}
