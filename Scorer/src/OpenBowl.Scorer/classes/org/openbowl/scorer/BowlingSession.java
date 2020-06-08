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
import org.openbowl.common.BowlingGame;
import org.openbowl.common.BowlingPins;
import org.openbowl.common.BowlingSplash;

/**
 *
 * @author Open Bowl <http://www.openbowlscoring.org/>
 */
public abstract class BowlingSession implements Runnable, Comparable<BowlingSession> {

    protected ArrayList<BowlingGame> players;
    protected Lane lane;
    protected DisplayConnector display;
    protected BooleanProperty isRunning;
    protected boolean mSessionFinished;
    protected int currentPlayer;
    protected int currentBall;
    protected String UUID;

    public BowlingSession(Lane lane) {
        this.lane = lane;
        this.display = lane.getDisplay();
        this.lane.addEventHandler(LaneEvents.SLOW_BALL, notUsed -> onSlowBall());
        this.lane.addEventHandler(LaneEvents.BOWL_EVENT, notUsed -> onBowlEvent());
        this.isRunning = new SimpleBooleanProperty(false);
        players = new ArrayList<>();
        this.UUID = "";
        this.mSessionFinished = false;
        this.currentPlayer = 0;
        this.currentBall = 0;
    }

    public ArrayList<BowlingGame> getPlayers() {
        return players;
    }

    public BooleanProperty getIsRunning() {
        return isRunning;
    }

    public String getUUID() {
        return UUID;
    }

    public void setUUID(String UUID) {
        this.UUID = UUID;
    }

    public void setPlayers(ArrayList<BowlingGame> players) {
        this.players = players;
    }

    public abstract int addPlayer(BowlingGame g);

    public abstract void pauseSession();

    public abstract void resumeSession();

    public abstract void abortSession();

    private void onSlowBall() {
        display.showSplash(BowlingSplash.SlowBall);
    }

    /**
     *
     * Default action of a bowling event ie a bowler bowls a ball down the lane
     * The player gets two balls on frames 1-9 and an optional 3rd ball on frame
     * 10
     */
    protected void onBowlEvent() {
        boolean foul = lane.isLastBallFoul();
        double speed = lane.getLastBallSpeed();
        ArrayList<BowlingPins> pins = lane.getLastBallPins();
        int currentFrame = players.get(currentBall).getFrameIndex();
        players.get(currentPlayer).bowled(pins, foul, speed);
        display.setScore(players.get(currentPlayer), currentPlayer);
        currentBall++;

        if (foul) {
            display.showSplash(BowlingSplash.Foul);
        }

        if (currentFrame < 9) {
            if (currentBall == 1) {
                if (players.get(currentBall).isStrikeSpare(10 - pins.size())) {
                    display.showSplash(BowlingSplash.Spare);
                }
                incrementPlayer();
            } else if (players.get(currentBall).isStrikeSpare(10 - pins.size())) {
                display.showSplash(BowlingSplash.Strike);
                incrementPlayer();
                lane.cycleNoScore();
            }

        }
        if (currentFrame == 9) {

        }

    }

    private boolean isFinished() {
        for (int x = 0; x < players.size(); x++) {
            if (!players.get(x).isFinished()) {
                return false;
            }
        }

        return true;
    }

    public abstract void newGame();

    /**
     *
     * Resends the scores for every player to the display
     */
    public void refreshDisplay() {
        for (int i = 0; i < players.size(); i++) {
            display.setScore(players.get(i), i);
        }
    }

    public void resetDisplay() {
        display.newGame();
        for (int i = 0; i < players.size(); i++) {
            display.newPlayer(players.get(i));
        }
    }

    protected void incrementPlayer() {
        currentPlayer = (currentPlayer + 1) % players.size();
        display.setCurentPlayer(currentPlayer);
        currentBall = 0;
    }

    @Override
    public int compareTo(BowlingSession t) {
        return this.UUID.compareTo(t.getUUID());
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (obj == null || obj.getClass() != this.getClass()) {
            return false;
        }
        BowlingSession b = (BowlingSession) obj;
        return this.compareTo(b) == 0;
    }

}
