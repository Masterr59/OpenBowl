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
public class BowlingFrame implements Comparable<BowlingFrame> {

    public enum ScoreType {
        UNKNOWN,
        MECHANICAL,
        USER,
        ADMIN
    }

    public enum BallNumber {
        ONE,
        TWO,
        BONUS,
        NONE
    }

    private final boolean[] isBallFoul;
    private final ArrayList<BowlingPins>[] balls;
    private int frameScore;
    private BallNumber currentBall;
    private final ScoreType ballType[];

    public BowlingFrame() {
        int numBalls = BallNumber.values().length;
        isBallFoul = new boolean[numBalls];
        balls = new ArrayList[numBalls];
        ballType = new ScoreType[numBalls];
        for (BallNumber b : BallNumber.values()) {
            isBallFoul[b.ordinal()] = false;
            balls[b.ordinal()] = new ArrayList<>();
            ballType[b.ordinal()] = ScoreType.UNKNOWN;
        }
        this.frameScore = 0;
        this.currentBall = BallNumber.NONE;
    }

    public void addBall(ArrayList<BowlingPins> p, boolean foul) {
        switch (currentBall) {
            case NONE:
                setBall(p, foul, BallNumber.ONE, ScoreType.MECHANICAL);
                currentBall = BallNumber.ONE;
                break;
            case ONE:
                setBall(p, foul, BallNumber.TWO, ScoreType.MECHANICAL);
                currentBall = BallNumber.TWO;
                break;
            case TWO:
                setBall(p, foul, BallNumber.BONUS, ScoreType.MECHANICAL);
                currentBall = BallNumber.BONUS;
                break;
        }
    }

    public void setBall(ArrayList<BowlingPins> p, boolean foul, BallNumber b, ScoreType t) {
        int ballNum = b.ordinal();
        balls[ballNum] = p;
        isBallFoul[ballNum] = foul;
        ballType[ballNum] = t;
    }

    public boolean isBallFoul(BallNumber b) {
        return isBallFoul[b.ordinal()];
    }

    public ArrayList<BowlingPins> getBallPins(BallNumber b) {
        return balls[b.ordinal()];
    }

    public ScoreType getScoreType(BallNumber b){
        return ballType[b.ordinal()];
    }
    
    public int getFrameScore() {
        return frameScore;
    }

    public void setFrameScore(int frameScore) {
        this.frameScore = frameScore;
    }

    public BallNumber getCurrentBall() {
        return currentBall;
    }

    @Override
    public int compareTo(BowlingFrame t) {
        for (BallNumber b : BallNumber.values()) {
            if (!this.balls[b.ordinal()].equals(t.getBallPins(b))
                    || this.isBallFoul[b.ordinal()] != t.isBallFoul(b)) {
                return 1;
            }
        }
        return 0;
    }
}
