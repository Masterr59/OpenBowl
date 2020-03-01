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
import javafx.application.Platform;
import org.openbowl.common.BowlingFrame;
import org.openbowl.common.BowlingFrame.BallNumber;
import org.openbowl.common.BowlingFrame.ScoreType;
import org.openbowl.common.BowlingGame;
import org.openbowl.common.BowlingPins;
import org.openbowl.common.BowlingSplash;

/**
 *
 * @author Open Bowl <http://www.openbowlscoring.org/>
 */
public class NumberedSession extends BowlingSession {

    private boolean run;
    private int GamesRemaining;
    private BallNumber currentBall;
    private int currentPlayer;
    private DisplayConnector display;
    private final static Object frameInterupt = new Object();

    public NumberedSession(Lane lane, DisplayConnector display, int numGames) {
        super(lane, display);
        GamesRemaining = numGames;
        currentBall = BallNumber.NONE;
        this.display = display;
        currentPlayer = 0;
        run = true;
    }

    public int addPlayer(BowlingGame g) {
        if (GamesRemaining > 0) {
            this.players.add(g);
            display.newPlayer(g);
            GamesRemaining--;
        }
        return GamesRemaining;
    }

    @Override
    public void pauseSession() {
        display.showMessageCard("stopBowling", -1);
        lane.getPinSetter().setPower(false);
    }

    @Override
    public void abortSession() {
        run = false;
        synchronized (frameInterupt) {
            frameInterupt.notifyAll();
        }
    }

    @Override
    protected void onBowlEvent() {
        boolean foul = lane.isLastBallFoul();
        double speed = lane.getLastBallSpeed();
        ArrayList<BowlingPins> pins = lane.getLastBallPins();
        //System.out.println(players.get(currentPlayer).getPlayerName() + " Ball: " + currentBall);
        //System.out.println(pins);
        players.get(currentPlayer).addBall(pins, foul, speed);
        display.setScore(players.get(currentPlayer), currentPlayer);

        if (foul) {
            display.showSplash(BowlingSplash.Foul);
        }
        if (players.get(currentPlayer).getFrames().size() < 10) {
            switch (currentBall) {
                case NONE:
                    currentBall = BallNumber.ONE;
                    //strike
                    if (players.get(currentPlayer).isStrikeSpare(pins)) {
                        display.showSplash(BowlingSplash.Strike);
                        players.get(currentPlayer).addEmptyBall();
                        lane.cycleNoScore();
                        currentBall = BallNumber.NONE;
                        currentPlayer = (currentPlayer + 1) % players.size();
                    }
                    break;
                case ONE:
                    currentBall = BallNumber.TWO;
                    //spare
                    if (players.get(currentPlayer).isStrikeSpare(pins)) {
                        display.showSplash(BowlingSplash.Spare);
                    }
                    break;
                case TWO:
                    currentBall = BallNumber.NONE;
                    lane.cycleNoScore();
                    break;
            }
        }
        //tenth frame
        if (players.get(currentPlayer).getFrames().size() >= 10) {
            BowlingFrame frameTen = players.get(currentPlayer).getFrames().get(9);
            boolean isBallOne = frameTen.getScoreType(BallNumber.ONE) != ScoreType.NONE;
            boolean isBallTwo = frameTen.getScoreType(BallNumber.TWO) != ScoreType.NONE;
            boolean isBallBonus = frameTen.getScoreType(BallNumber.BONUS) != ScoreType.NONE;
            boolean isBallOneStrike = players.get(currentPlayer).isStrikeSpare(frameTen.getBallPins(BallNumber.ONE));
            boolean isBallTwoStrike = players.get(currentPlayer).isStrikeSpare(frameTen.getBallPins(BallNumber.TWO));
            switch (currentBall) {
                case NONE:
                    //Strike on first ball
                    currentBall = BallNumber.ONE;
                    if (players.get(currentPlayer).isStrikeSpare(pins)) {
                        players.get(currentPlayer).addEmptyBall();
                        lane.cycleNoScore();
                    }
                    break;
                case ONE:
                    //Strike / spare on second ball
                    currentBall = BallNumber.TWO;
                    if (players.get(currentPlayer).isStrikeSpare(pins)) {
                        lane.cycleNoScore();
                    }
                    break;
                case TWO:
                    currentBall = BallNumber.NONE;
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
            //System.out.println("Ball Count: " + ballCount);
            //System.out.println("Allow 3rd " + allowThird);
            //System.out.println(frameTen);
            if (ballCount == 3 || (ballCount == 2 && !allowThird)) {
                currentPlayer = (currentPlayer + 1) % players.size();
                currentBall = BallNumber.NONE;
            }

        } else {
            if (currentBall == BallNumber.TWO) {
                currentBall = BallNumber.NONE;
                currentPlayer = (currentPlayer + 1) % players.size();
            }
        }

        synchronized (frameInterupt) {
            frameInterupt.notifyAll();
        }
    }

    @Override
    public void run() {
        isRunning.setValue(true);
        lane.getPinSetter().setPower(true);
        boolean triggerDisplayRefresh = false;
        while (run && !Thread.currentThread().isInterrupted()) {
            try {
                int framesLeft = 0;
                if(triggerDisplayRefresh){
                    refreshDisplay();
                }
                for (BowlingGame b : players) {
                    ArrayList<BowlingFrame> frames = b.getFrames();
                    framesLeft += (frames.size() == 11) ? 0 : 1;
                }
                if (framesLeft == 0 && GamesRemaining < 1) {
                    run = false;
                    lane.getPinSetter().setPower(false);
                } else if (framesLeft == 0 && GamesRemaining > 0) {
                    //create new game
                    for (int i = 0; i < players.size(); i++) {
                        //reset Game
                        if (GamesRemaining > 0) {
                            players.get(i).reset();
                            GamesRemaining--;
                        }
                        triggerDisplayRefresh = true;
                    }
                    currentBall = BallNumber.NONE;

                    currentPlayer = 0;
                }
                synchronized (frameInterupt) {
                    frameInterupt.wait();
                }
            } catch (InterruptedException ex) {
                System.out.println("NumberedManager Interupted");

            }
        }
        isRunning.setValue(false);

    }

    @Override
    public void resumeSession() {
        display.showMessageCard("NONE", -1);
        lane.getPinSetter().setPower(true);

    }

    public void refreshDisplay() {
        for (int i = 0; i < players.size(); i++) {
            display.setScore(players.get(i), i);
        }
    }
}
