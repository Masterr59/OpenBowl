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
package org.openbowl.common;

import java.util.ArrayList;

/**
 *
 * @author Open Bowl <http://www.openbowlscoring.org/>
 */
public class BowlingGame {

    private String playerName;
    private int playerID;
    private int handycap;
    private ArrayList<BowlingFrame> frames;
    private int gameScore;

    public BowlingGame(String name, int id) {
        this.playerName = name;
        this.playerID = id;
        this.gameScore = 0;
        this.handycap = 0;
        this.frames = new ArrayList<>();
    }

    public ArrayList<BowlingFrame> getFrames() {
        return frames;
    }

    public void setFrames(ArrayList<BowlingFrame> frames) {
        this.frames = frames;
    }

    public void addFrame(BowlingFrame f) {
        this.frames.add(f);
    }

    public int getGameScore() {
        return gameScore;
    }

    public void setGameScore(int gameScore) {
        this.gameScore = gameScore;
    }

    public int getHandycap() {
        return handycap;
    }

    public void setHandycap(int handycap) {
        this.handycap = handycap;
    }

    public String getPlayerName() {
        return playerName;
    }

    public int getPlayerID() {
        return playerID;
    }

    public void setPlayerName(String playerName) {
        this.playerName = playerName;
    }

    public void setPlayerID(int playerID) {
        this.playerID = playerID;
    }

    public void updateTo(BowlingGame game) {
        this.playerName = game.getPlayerName();
        this.playerID = game.getPlayerID();
        this.handycap = game.getHandycap();
        this.frames = game.getFrames();
    }

}
