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
import org.openbowl.common.BowlingGame;

/**
 *
 * A bowling session with a number of games
 * 
 * @author Open Bowl <http://www.openbowlscoring.org/>
 */
public class NumberedSession extends BowlingSession {

    private boolean run;
    private int GamesRemaining;
    private final static Object frameInterupt = new Object();

    public NumberedSession(Lane lane, int numGames) {
        super(lane);
        GamesRemaining = numGames;
        currentPlayer = 0;
        run = true;
    }

    @Override
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
        refreshDisplay();
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
                if (triggerDisplayRefresh)
                    refreshDisplay();
                if (mSessionFinished && GamesRemaining < 1) {
                    run = false;
                    lane.getPinSetter().setPower(false);
                } 
                else if (mSessionFinished && GamesRemaining > 0) {
                    //create new game
                    newGame();
                    triggerDisplayRefresh = true;
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
    public void newGame()
    {
        for (int i = 0; i < players.size(); i++) 
        {
            if (GamesRemaining > 0) 
            {
                players.get(i).reset();
                GamesRemaining--;
            }
        }//end for
    }

    @Override
    public void resumeSession() {
        display.showMessageCard("NONE", -1);
        lane.getPinSetter().setPower(true);

    }

    
}
