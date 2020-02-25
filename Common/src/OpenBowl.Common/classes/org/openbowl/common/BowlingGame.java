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
    private int tapValue;
    private int handycap;
    private ArrayList<BowlingFrame> frames;
    private BowlingFrame currentFrame;
    private int gameScore;

    public BowlingGame(String name, int id) {
        this.playerName = name;
        this.playerID = id;
        this.gameScore = 0;
        this.tapValue = 0;
        this.handycap = 0;
        this.frames = new ArrayList<>();
        this.currentFrame = new BowlingFrame();
        this.frames.add(currentFrame);
    }

    public void addBall(ArrayList<BowlingPins> pins, boolean foul) {
        switch (currentFrame.getCurrentBall()) {
            case NONE:
                this.currentFrame.addBall(pins, foul);
                break;
            case ONE:
                this.currentFrame.addBall(pins, foul);
                if (this.frames.size() < 10) {
                    this.currentFrame = new BowlingFrame();
                    this.frames.add(currentFrame);
                }
                break;
            case TWO:
                if (this.frames.size() == 10) {
                    this.currentFrame.addBall(pins, foul);
                }
                break;
        }
        scoreGame();
    }

    public ArrayList<BowlingFrame> getFrames() {
        return frames;
    }

    public void setFrames(ArrayList<BowlingFrame> frames) {
        this.frames = frames;
        scoreGame();
    }

    public void addFrame(BowlingFrame f) {
        this.frames.add(f);
        scoreGame();
    }

    public int getGameScore() {
        return gameScore;
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

    public int getTapValue() {
        return tapValue;
    }

    public void setTapValue(int tapValue) {
        this.tapValue = tapValue;
        scoreGame();
    }

    public void updateTo(BowlingGame game) {
        this.playerName = game.getPlayerName();
        this.playerID = game.getPlayerID();
        this.handycap = game.getHandycap();
        this.frames = game.getFrames();
        scoreGame();
    }

    private void scoreGame() {
        //to do
    }

}
