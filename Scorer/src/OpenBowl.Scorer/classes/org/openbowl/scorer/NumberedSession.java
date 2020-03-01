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
import org.openbowl.common.BowlingFrame;
import org.openbowl.common.BowlingFrame.BallNumber;
import org.openbowl.common.BowlingGame;

/**
 *
 * @author Open Bowl <http://www.openbowlscoring.org/>
 */
public class NumberedSession extends BowlingSession {

    private boolean run;
    private int GamesRemaining;
    private final DisplayConnector display;
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
        super.onBowlEvent();
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
                if (triggerDisplayRefresh) {
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

    
}
