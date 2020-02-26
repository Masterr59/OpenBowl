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
import org.openbowl.common.BowlingGame;

/**
 *
 * @author Open Bowl <http://www.openbowlscoring.org/>
 */
public abstract class BowlingSession implements Runnable {

    protected ArrayList<BowlingGame> players;
    protected Lane lane;
    protected DisplayConnector display;

    public BowlingSession(Lane lane, DisplayConnector display) {
        this.lane = lane;
        this.display = display;
        this.lane.addEventHandler(LaneEvents.SLOW_BALL, notUsed -> onSlowBall());
        this.lane.addEventHandler(LaneEvents.BOWL_EVENT, notUsed -> onBowlEvent());
    }

    public ArrayList<BowlingGame> getPlayers() {
        return players;
    }

    public void setPlayers(ArrayList<BowlingGame> players) {
        this.players = players;
    }

    public abstract void pauseSession();

    public abstract void abortSession();

    private void onSlowBall() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    protected abstract void onBowlEvent();

}
