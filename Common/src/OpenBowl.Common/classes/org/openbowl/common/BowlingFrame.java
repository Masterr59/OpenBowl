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
        NONE,
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
    private double speed[];

    public BowlingFrame() {
        int numBalls = BallNumber.values().length;
        isBallFoul = new boolean[numBalls];
        balls = new ArrayList[numBalls];
        ballType = new ScoreType[numBalls];
        speed = new double[numBalls];
        for (BallNumber b : BallNumber.values()) {
            isBallFoul[b.ordinal()] = false;
            balls[b.ordinal()] = new ArrayList<>();
            ballType[b.ordinal()] = ScoreType.NONE;
            speed[b.ordinal()] = 0;
        }
        this.frameScore = -1;
        this.currentBall = BallNumber.NONE;
    }

    /**
     *
     * Adds a ball to the frame and sets the ScoreType to mechanical
     * 
     * @param p The pins that were still standing for this ball
     * @param foul If this ball was foul
     * @param speed The speed of the ball in fps
     */
    public void addBall(ArrayList<BowlingPins> p, boolean foul, double speed) {
        switch (currentBall) {
            case NONE:
                setBall(p, foul, BallNumber.ONE, ScoreType.MECHANICAL, speed);
                currentBall = BallNumber.ONE;
                break;
            case ONE:
                setBall(p, foul, BallNumber.TWO, ScoreType.MECHANICAL, speed);
                currentBall = BallNumber.TWO;
                break;
            case TWO:
                setBall(p, foul, BallNumber.BONUS, ScoreType.MECHANICAL, speed);
                currentBall = BallNumber.BONUS;
                break;
        }
    }

    /**
     *
     * Manually sets a ball in the frame, should be used for changing a score 
     * 
     * @param p The pins that were still standing for this ball
     * @param foul If this ball was foul
     * @param b The ball number in the frame
     * @param t The type of scoring for this ball
     * @param speed The speed of the ball in fps
     */
    public void setBall(ArrayList<BowlingPins> p, boolean foul, BallNumber b, ScoreType t, double speed) {
        int ballNum = b.ordinal();
        balls[ballNum] = p;
        isBallFoul[ballNum] = foul;
        ballType[ballNum] = t;
        this.speed[ballNum] = speed;
    }

    public boolean isBallFoul(BallNumber b) {
        return isBallFoul[b.ordinal()];
    }

    public ArrayList<BowlingPins> getBallPins(BallNumber b) {
        return balls[b.ordinal()];
    }

    public ScoreType getScoreType(BallNumber b) {
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

    @Override
    public String toString() {
        String ret = "Frame: ";
        for (int i = 0; i < 3; i++) {
            ret += "B" + i + " -> ";
            ret += ballType[i];
            ret += ": " + balls[i].size();
            ret += " " ;
        }
        return ret;
    }

}
