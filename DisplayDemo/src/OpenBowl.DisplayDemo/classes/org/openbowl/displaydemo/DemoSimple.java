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
package org.openbowl.displaydemo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import org.openbowl.common.BowlingGame;
import org.openbowl.common.BowlingPins;
import org.openbowl.common.BowlingSplash;
import org.openbowl.scorer.DisplayConnector;

/**
 *
 * @author Open Bowl <http://www.openbowlscoring.org/>
 */
public class DemoSimple {
    private final String FRED = "Fred F.";
    private final String BARNEY = "Barney R.";
    private final String SLATE = "Mr. Slate";
    private final String GAZOO = "GR8 Gazoo";

    private final ArrayList<BowlingPins> STD_PINS[];
    private final DisplayConnector display;
    private final Map<String, BowlingGame> game;

    public DemoSimple(String displayAddress) {
        STD_PINS = new ArrayList[11];
        for (int i = 0; i < 11; i++) {
            STD_PINS[i] = new ArrayList<>();
            for (int j = 0; j < i; j++) {
                STD_PINS[i].add(BowlingPins.values()[j]);
            }
        }

        this.game = new HashMap<>();

        this.game.put(FRED, new BowlingGame(FRED, 0, FRED));
        this.game.put(BARNEY, new BowlingGame(BARNEY, 0, BARNEY));
        this.game.put(SLATE, new BowlingGame(SLATE, 19, SLATE));
        this.game.put(GAZOO, new BowlingGame(GAZOO, 0, GAZOO));


        this.display = new DisplayConnector("odd", "none");

        Map<String, Object> displaySettings = new HashMap<>();
        displaySettings.put(this.display.ADDRESS_SETTING, displayAddress);
        displaySettings.put(this.display.ENDPOINT_SETTING, "odd");
        displaySettings.put("Type", this.display.getClass().getName());

        this.display.setConfiguration(displaySettings);

    }

    public void runDemo() {
        try {
            display.newGame();
            display.showMessageCard("Welcome", -1);
            Thread.sleep(3000);
            display.showMessageCard("NONE", -1);
            Thread.sleep(500);
            display.newPlayer(game.get(FRED));
            Thread.sleep(500);
            display.newPlayer(game.get(BARNEY));
            Thread.sleep(500);
            display.newPlayer(game.get(SLATE));
            Thread.sleep(500);
            display.newPlayer(game.get(GAZOO));
            Thread.sleep(2000);
            for(BowlingSplash sp : BowlingSplash.values()){
                display.showSplash(sp);
                long time = sp == BowlingSplash.PerfectGame ? 31000 : 5500;
                time = sp == BowlingSplash.Foul ? 7000 : time;
                Thread.sleep(time);
            }
            
            
            
        } catch (InterruptedException ex) {
            System.out.println("Thread Interupted");
        }
    }
    
}
