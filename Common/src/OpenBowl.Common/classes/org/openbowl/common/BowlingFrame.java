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
public class BowlingFrame {

    private boolean isFoul;
    private ArrayList<BowlingPins> ballOne;
    private ArrayList<BowlingPins> ballTwo;
    private ArrayList<BowlingPins> bonusBall;
    private int frameScore;

    public BowlingFrame() {
        this.isFoul = false;
        this.ballOne = new ArrayList<>();
        this.ballTwo = new ArrayList<>();
        this.bonusBall = new ArrayList<>();
        this.frameScore = 0;
    }

    public boolean isIsFoul() {
        return isFoul;
    }

    public void setIsFoul(boolean isFoul) {
        this.isFoul = isFoul;
    }

    public ArrayList<BowlingPins> getBallOne() {
        return ballOne;
    }

    public void setBallOne(ArrayList<BowlingPins> ballOne) {
        this.ballOne = ballOne;
    }

    public ArrayList<BowlingPins> getBallTwo() {
        return ballTwo;
    }

    public void setBallTwo(ArrayList<BowlingPins> ballTwo) {
        this.ballTwo = ballTwo;
    }

    public ArrayList<BowlingPins> getBonusBall() {
        return bonusBall;
    }

    public void setBonusBall(ArrayList<BowlingPins> bonusBall) {
        this.bonusBall = bonusBall;
    }

    public int getFrameScore() {
        return frameScore;
    }

    public void setFrameScore(int frameScore) {
        this.frameScore = frameScore;
    }

}
