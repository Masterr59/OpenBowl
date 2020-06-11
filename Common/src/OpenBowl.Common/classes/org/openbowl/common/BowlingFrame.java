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

    public enum ScoreType {
        NONE,
        MECHANICAL,
        USER,
        ADMIN
    }

    private final double[] mSpeed;
    private final boolean[] mFoul;
    private final ScoreType[] mScoreType;
    private ArrayList<BowlingPins>[] mPins;
    private int mBallIndex;
    private int mFrameScore;

    public BowlingFrame() {
        mSpeed = new double[3];
        mFoul = new boolean[3];
        mPins = new ArrayList[3];
        mScoreType = new ScoreType[3];
        mFrameScore = -1;
        for (int i = 0; i < 3; i++) {
            mSpeed[i] = -1.0;
            mFoul[i] = false;
            mPins[i] = new ArrayList<>();
            mScoreType[i] = ScoreType.NONE;
        }
        mBallIndex = 0;
    }

    public void addBall(ArrayList<BowlingPins> remainingPins, boolean foul, double speed) {
        if (mBallIndex < 3) {
            mFoul[mBallIndex] = foul;
            mSpeed[mBallIndex] = speed;
            mPins[mBallIndex] = remainingPins;
            mScoreType[mBallIndex] = ScoreType.MECHANICAL;
        } else {
            throw new IllegalArgumentException("Adding Ball when fram is full");
        }
        mBallIndex++;
    }

    @Override
    public String toString() {
        String result = "[";
        for (int i = 0; i < 3; i++) {

        }

        result += "]";

        return result;
    }

    public double getSpeed(int index) {
        return mSpeed[index];
    }

    public boolean isFoul(int index) {
        return mFoul[index];
    }

    public int getFrameScore() {
        return this.mFrameScore;
    }

    public ScoreType[] getScoreTypes() {
        return mScoreType;
    }

    public int getBallRawScore(int b) {
        int ret = 0;
        switch (b) {
            case 0:
                return 10 - this.mPins[0].size();
            case 1:
                return 10 - this.mPins[1].size();
            case 2:
                return 10 - this.mPins[2].size();
        }
        return ret;
    }

    void setFrameScore(int score) {
        this.mFrameScore = score;
    }
}
