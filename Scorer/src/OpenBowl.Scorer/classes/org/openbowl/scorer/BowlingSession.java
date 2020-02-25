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
public abstract class BowlingSession implements Runnable{

    protected ArrayList<BowlingGame> players;
    protected PinSetter pinSetter;
    protected Detector sweep, ball, foul;
    protected DisplayConnector display;

    public BowlingSession(PinSetter pinSetter, Detector sweep, Detector ball, Detector foul, DisplayConnector display) {
        this.pinSetter = pinSetter;
        this.sweep = sweep;
        this.ball = ball;
        this.foul = foul;
        this.display = display;
    }

    public ArrayList<BowlingGame> getPlayers() {
        return players;
    }

    public void setPlayers(ArrayList<BowlingGame> players) {
        this.players = players;
    }

    public abstract void pauseSession();
    
    public abstract void abortSession();

}
