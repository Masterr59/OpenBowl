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
import org.openbowl.common.BowlingFrame.BallNumber;
import org.openbowl.common.BowlingFrame.ScoreType;

/**
 *
 * @author Open Bowl <http://www.openbowlscoring.org/>
 */
public class BowlingGame 
{

    private String playerName;
    private int playerID;
    private int tapValue;
    private int handicap;
    private ArrayList<BowlingFrame> frames;
    private BowlingFrame currentFrame;
    private int gameScore;

    /**
     *
     * @param name The players name on the score card
     * @param id The ID number of the player
     */
        this.playerName = name;
        this.playerID = id;
        this.gameScore = 0;
        this.tapValue = 10;
        this.handicap = 0;
        this.frames = new ArrayList<>();
        this.currentFrame = new BowlingFrame();
        this.frames.add(currentFrame);
    }

    /**
     *
     * Resets / clears a game of all scoring 
     */
    public void reset(){
        this.gameScore = 0;
        this.frames = new ArrayList<>();
        this.currentFrame = new BowlingFrame();
        this.frames.add(currentFrame);
    }
    
    /**
     *
     * Adds a ball to the game
     * 
     * @param pins An Array of pins remaining
     * @param foul If this ball is foul
     * @param speed The speed of the ball in fps
     */
    public void addBall(ArrayList<BowlingPins> pins, boolean foul, double speed) {
        switch (currentFrame.getCurrentBall()) {
            case NONE:
                this.currentFrame.addBall(pins, foul, speed);
                break;
            case ONE:
                this.currentFrame.addBall(pins, foul, speed);
                if (this.frames.size() < 10) {
                    this.currentFrame = new BowlingFrame();
                    this.frames.add(currentFrame);
                }
                break;
            case TWO:
                if (this.frames.size() == 10) {
                    this.currentFrame.addBall(pins, foul, speed);
                    this.currentFrame = new BowlingFrame();
                    this.frames.add(currentFrame);
                }
                break;
        }
        scoreGame();
    }

    /**
     *
     * Adds an empty ball to the frame that is NOT scored
     * 
     */
    public void addEmptyBall() {
        switch (currentFrame.getCurrentBall()) {
            case NONE:
                this.currentFrame.setBall(new ArrayList<BowlingPins>(), false, BallNumber.ONE, ScoreType.NONE, 0.0);
                break;
            case ONE:
                this.currentFrame.setBall(new ArrayList<BowlingPins>(), false, BallNumber.TWO, ScoreType.NONE, 0.0);
                if (this.frames.size() < 10) {
                    this.currentFrame = new BowlingFrame();
                    this.frames.add(currentFrame);
                }
                break;
            case TWO:
                if (this.frames.size() == 10) {
                    this.currentFrame.setBall(new ArrayList<BowlingPins>(), false, BallNumber.BONUS, ScoreType.NONE, 0.0);
                    this.currentFrame = new BowlingFrame();
                    this.frames.add(currentFrame);
                }
                break;
        }
        
        scoreGame();
    }
    
    public void addFrame(BowlingFrame f) {
        this.frames.add(f);
        scoreGame();
    }

    public ArrayList<BowlingFrame> getFrames() {
        return frames;
    }
    
/* ************************************************************ */    
    
    /*           *
     *  SETTERS  *
     *           */
    
    public void setHandicap(int handicap)        {  this.handicap = handicap;      }
    
    public void setFrames(ArrayList<BowlingFrame> frames) 
    {
        this.frames = frames;
        scoreGame();
    }
    
    /**
     *
     * @return The number of pins that are required to be down to count
     * as a strike or spare
     */

    /**
     *
     * @param tapValue The number of pins that are required to be down to count
     * as a strike or spare
     */
        this.tapValue = tapValue;
        scoreGame();
    }
    
    /**
     *
     * Checks if the array of pins would count as a strike or spare based on
     * the tap value
     * 
     * @param pins The array of pins remaining
     * @return If it counts as a strike or spare
     */
    public int getGameScore()      {  return gameScore;   }
    public int getHandicap()       {  return handicap;    }
    public int getTapValue()       {  return tapValue;    }

/* ************************************************************ */

    public boolean isStrikeSpare(ArrayList<BowlingPins> pins) {
        return (10 - pins.size() - tapValue) >= 0;
    }

    /**
     *
     * Checks if the array of pins is a noTap strike / spare
     * 
     * @param pins The array of pins remaining
     * @return
     */
    public boolean isNoTap(ArrayList<BowlingPins> pins) {
        return !pins.isEmpty() && isStrikeSpare(pins);
    }

    /**
     *
     * Copies the incoming game to the current game
     * 
     * @param game The game that is being copied from
     */
    public void updateTo(BowlingGame game) {
        this.playerName = game.getPlayerName();
        this.playerID = game.getPlayerID();
        this.handicap = game.getHandicap();
        this.frames = game.getFrames();
        scoreGame();
    }

    private void scoreGame() {
        //to do

    }

    @Override
    public String toString() {
        String ret = playerName;
        ret += " ";
        for (BowlingFrame f : frames) {

            ret += "|";
            ret += (f.getScoreType(BowlingFrame.BallNumber.ONE) != ScoreType.NONE) ? f.getBallPins(BowlingFrame.BallNumber.ONE).size() : " ";
            ret += "|";
            ret += (f.getScoreType(BowlingFrame.BallNumber.TWO) != ScoreType.NONE) ? f.getBallPins(BowlingFrame.BallNumber.TWO).size() : " ";
            ret += "|";
            ret += (f.getScoreType(BowlingFrame.BallNumber.BONUS) != ScoreType.NONE) ? f.getBallPins(BowlingFrame.BallNumber.BONUS).size() : " ";
        }
        return ret;
    }

}
