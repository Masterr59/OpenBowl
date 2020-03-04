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
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.prefs.Preferences;
import javafx.scene.control.TextInputDialog;
import org.openbowl.common.BowlingGame;
import org.openbowl.common.BowlingSplash;
import org.openbowl.common.WebFunctions;

/**
 *
 * @author Open Bowl <http://www.openbowlscoring.org/>
 */
public class DisplayConnector {

    public final String DISPLAY_ADDRESS_NAME = "DisplayAddress";
    public final String DISPLAY_ENDPOINT_NAME = "DisplayEndpoint";
    public final String DISPLAY_ADDRESS_VALUE = "127.0.0.1";
    public final String DISPLAY_ENDPOINT_VALUE = "odd";

    private final String name;
    private String address;
    private String endpoint;
    private final Gson gson;
    private final String authToken;
    private Preferences prefs;

    public DisplayConnector(String name, String authToken) {
        this.name = name;
        prefs = Preferences.userNodeForPackage(this.getClass());
        this.address = prefs.get(name + DISPLAY_ADDRESS_NAME, DISPLAY_ADDRESS_VALUE);
        this.endpoint = prefs.get(name + DISPLAY_ENDPOINT_NAME, DISPLAY_ENDPOINT_VALUE);
        this.authToken = authToken;
        gson = new Gson();
    }

    public void configureDialog() {
        try {
            DisplayConnectorDialogController dialog = new DisplayConnectorDialogController(name, this);
            dialog.setTitle(name);
            dialog.showAndWait();

        } catch (IOException e) {
            System.out.println("Error showing dialog " + e.toString());
            e.printStackTrace();
        }
        this.address = prefs.get(name + DISPLAY_ADDRESS_NAME, DISPLAY_ADDRESS_VALUE);
        this.endpoint = prefs.get(name + DISPLAY_ENDPOINT_NAME, DISPLAY_ENDPOINT_VALUE);
    }

    /**
     * 
     * Tells the display who the current player is
     *
     * @param player The new current player
     * @return The response from the display
     */
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

    /**
     *
     * Sets the score for a given player
     * 
     * @param g The game score card
     * @param player The player to update
     * @return The response from the display
     */
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

    /**
     *
     * Adds a new player on the display
     * 
     * @param g The players scorecard
     * @return The response from the display
     */
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

    /**
     *
     * Shows a splash / excitor video
     * 
     * @param type The type of splash to show
     * @return the response from the display
     */
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

    /**
     *
     * Shows a message card on the display for a given length of time
     * 
     * @param type The type of card to show
     * @param duration How long to show it
     * @return The response from the display
     */
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
