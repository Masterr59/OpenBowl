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
import java.util.logging.Level;
import java.util.logging.Logger;
import org.openbowl.common.BowlingFrame;
import org.openbowl.common.BowlingFrame.BallNumber;
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
    private Object frameInterupt;

    public NumberedSession(Lane lane, DisplayConnector display, int numGames) {
        super(lane, display);
        GamesRemaining = numGames;
        currentBall = BallNumber.NONE;
        currentPlayer = 0;
        frameInterupt = new Object();
        run = true;
    }

    public int addPlayer(BowlingGame g) {
        if (GamesRemaining > 0) {
            this.players.add(g);
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
        frameInterupt.notifyAll();
    }

    @Override
    protected void onBowlEvent() {
        boolean foul = lane.isLastBallFoul();
        double speed = lane.getLastBallSpeed();
        ArrayList<BowlingPins> pins = lane.getLastBallPins();
        players.get(currentPlayer).addBall(pins, foul, speed);
        display.setScore(players.get(currentPlayer), currentPlayer);

        if (foul) {
            display.showSplash(BowlingSplash.Foul);
        }
        switch (currentBall) {
            case NONE:
                currentBall = BallNumber.ONE;
                if (pins.isEmpty()) {
                    //Strike
                    players.get(currentPlayer).addBall(pins, foul, speed);
                    lane.getPinSetter().cycle();
                }
                break;
            case ONE:
                currentBall = BallNumber.TWO;
                //spare
                if (pins.isEmpty()) {
                    display.showSplash(BowlingSplash.Spare);
                }
                break;
            case BONUS:
                currentBall = BallNumber.NONE;
                lane.getPinSetter().cycle();
                break;
        }
        //tenth frame and strike / spare
        if (players.get(currentPlayer).getFrames().size() == 10) {
            switch (currentBall) {
                case ONE:
                    //Strike on first ball
                    if (pins.isEmpty()) {
                        lane.getPinSetter().cycle();
                    }
                    break;
                case TWO:
                    ArrayList<BowlingPins> ballOne = players.get(currentPlayer)
                            .getFrames().get(9).getBallPins(BallNumber.ONE);
                    if (!pins.isEmpty() && !ballOne.isEmpty()) {
                        currentPlayer = (currentPlayer + 1) % players.size();
                    }
                    break;
                case BONUS:
                    currentPlayer = (currentPlayer + 1) % players.size();
                    lane.getPinSetter().cycle();
                    break;
            }
        } else {
            currentPlayer = (currentPlayer + 1) % players.size();
        }

        frameInterupt.notifyAll();
    }

    @Override
    public void run() {
        while (run) {
            boolean framesLeft = false;
            for (BowlingGame b : players) {
                ArrayList<BowlingFrame> frames = b.getFrames();
                if (frames.size() == 11) {
                    framesLeft = false;
                } else {
                    framesLeft = true;
                    break;
                }
            }
            if (!framesLeft && GamesRemaining < 1) {
                run = false;
                lane.getPinSetter().setPower(false);
            } else if (!framesLeft && GamesRemaining > 0) {
                //create new game
                for (int i = 0; i < players.size(); i++) {
                    //reset Game
                    players.get(i).setFrames(new ArrayList<>());
                    GamesRemaining --;
                }
            }

            try {
                frameInterupt.wait();
            } catch (InterruptedException ex) {
                Logger.getLogger(NumberedSession.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

    }

    @Override
    public void resumeSession() {
        display.showMessageCard("NONE", -1);
        lane.getPinSetter().setPower(true);

    }

}
