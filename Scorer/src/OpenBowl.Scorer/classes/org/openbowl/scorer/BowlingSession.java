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

import java.util.ArrayList;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import org.openbowl.common.BowlingFrame;
import org.openbowl.common.BowlingGame;
import org.openbowl.common.BowlingPins;
import org.openbowl.common.BowlingSplash;

/**
 *
 * @author Open Bowl <http://www.openbowlscoring.org/>
 */
public abstract class BowlingSession implements Runnable {

    protected ArrayList<BowlingGame> players;
    protected Lane lane;
    protected DisplayConnector display;
    protected BooleanProperty isRunning;
    protected BowlingFrame.BallNumber currentBall;
    protected int currentPlayer;

    public BowlingSession(Lane lane) {
        this.lane = lane;
        this.display = lane.getDisplay();
        this.lane.addEventHandler(LaneEvents.SLOW_BALL, notUsed -> onSlowBall());
        this.lane.addEventHandler(LaneEvents.BOWL_EVENT, notUsed -> onBowlEvent());
        this.isRunning = new SimpleBooleanProperty(false);
        players = new ArrayList<>();
    }

    public ArrayList<BowlingGame> getPlayers() {
        return players;
    }

    public BooleanProperty getIsRunning() {
        return isRunning;
    }

    public void setPlayers(ArrayList<BowlingGame> players) {
        this.players = players;
    }

    public abstract void pauseSession();

    public abstract void resumeSession();

    public abstract void abortSession();

    private void onSlowBall() {
        display.showSplash(BowlingSplash.SlowBall);
    }

    /**
     *
     * Default action of a bowling event ie a bowler bowls a ball down the lane
     * The player gets two balls on frames 1-9 and an optional 3rd ball on 
     * frame 10
     */
    protected void onBowlEvent() {
        boolean foul = lane.isLastBallFoul();
        double speed = lane.getLastBallSpeed();
        ArrayList<BowlingPins> pins = lane.getLastBallPins();
        players.get(currentPlayer).addBall(pins, foul, speed);
        display.setScore(players.get(currentPlayer), currentPlayer);

        if (foul) {
            display.showSplash(BowlingSplash.Foul);
        }
        if (players.get(currentPlayer).getFrames().size() < 10) {
            switch (currentBall) {
                case NONE:
                    currentBall = BowlingFrame.BallNumber.ONE;
                    //strike
                    if (players.get(currentPlayer).isStrikeSpare(pins)) {
                        display.showSplash(BowlingSplash.Strike);
                        players.get(currentPlayer).addEmptyBall();
                        lane.cycleNoScore();
                        currentBall = BowlingFrame.BallNumber.NONE;
                        incrementPlayer();
                    }
                    break;
                case ONE:
                    currentBall = BowlingFrame.BallNumber.TWO;
                    //spare
                    if (players.get(currentPlayer).isStrikeSpare(pins)) {
                        display.showSplash(BowlingSplash.Spare);
                    }
                    break;
                case TWO:
                    currentBall = BowlingFrame.BallNumber.NONE;
                    lane.cycleNoScore();
                    break;
            }
        }
        //tenth frame
        if (players.get(currentPlayer).getFrames().size() >= 10) {
            BowlingFrame frameTen = players.get(currentPlayer).getFrames().get(9);
            boolean isBallOne = frameTen.getScoreType(BowlingFrame.BallNumber.ONE) != BowlingFrame.ScoreType.NONE;
            boolean isBallTwo = frameTen.getScoreType(BowlingFrame.BallNumber.TWO) != BowlingFrame.ScoreType.NONE;
            boolean isBallBonus = frameTen.getScoreType(BowlingFrame.BallNumber.BONUS) != BowlingFrame.ScoreType.NONE;
            boolean isBallOneStrike = players.get(currentPlayer).isStrikeSpare(frameTen.getBallPins(BowlingFrame.BallNumber.ONE));
            boolean isBallTwoStrike = players.get(currentPlayer).isStrikeSpare(frameTen.getBallPins(BowlingFrame.BallNumber.TWO));
            switch (currentBall) {
                case NONE:
                    //Strike on first ball
                    currentBall = BowlingFrame.BallNumber.ONE;
                    if (players.get(currentPlayer).isStrikeSpare(pins)) {
                        players.get(currentPlayer).addEmptyBall();
                        lane.cycleNoScore();
                    }
                    break;
                case ONE:
                    //Strike / spare on second ball
                    currentBall = BowlingFrame.BallNumber.TWO;
                    if (players.get(currentPlayer).isStrikeSpare(pins)) {
                        lane.cycleNoScore();
                    }
                    break;
                case TWO:
                    currentBall = BowlingFrame.BallNumber.NONE;
                    lane.cycleNoScore();
                    break;
            }
            boolean allowThird = false;

            if (isBallOne && isBallOneStrike) {
                allowThird = true;
            }
            if (isBallTwo && isBallTwoStrike) {
                allowThird = true;
            }

            int ballCount = isBallOne ? 1 : 0;
            ballCount += isBallTwo ? 1 : 0;
            ballCount += isBallBonus ? 1 : 0;
            if (ballCount == 3 || (ballCount == 2 && !allowThird)) {
                incrementPlayer();
                currentBall = BowlingFrame.BallNumber.NONE;
            }

        } else {
            if (currentBall == BowlingFrame.BallNumber.TWO) {
                currentBall = BowlingFrame.BallNumber.NONE;
                incrementPlayer();
            }
        }
    }
    
    /**
     *
     * Resends the scores for every player to the display
     */
    public void refreshDisplay() {
        for (int i = 0; i < players.size(); i++) {
            display.setScore(players.get(i), i);
        }
    }

    protected void incrementPlayer(){
        currentPlayer = (currentPlayer + 1) % players.size();
        display.setCurentPlayer(currentPlayer);
    }
}
