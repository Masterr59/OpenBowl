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
public class BowlingGame {

    private BowlingFrame[] mFrames;
    private int mFrameIndex;
    private int mBallIndex;
    private String mPlayerName;
    private int mHandicap;
    private int mTap;
    private boolean mFinishedFrame;
    private String mPlayerUUID;
    private int mGameScore;

    public BowlingGame(String playerName, int handicap, String playerUUID, int tap) {
        mFrames = new BowlingFrame[10];
        mFrameIndex = 0;
        mBallIndex = 0;
        mPlayerName = playerName;
        mHandicap = handicap;
        mTap = tap;
        mFinishedFrame = false;
        mPlayerUUID = playerUUID;
        mGameScore = 0;
    }

    public void bowled(ArrayList<BowlingPins> remainingPins, boolean foul, double speed) {
        if (mFrameIndex < 9 && mBallIndex > 1) {
            mFrameIndex++;
        }
        if (mFrames[mFrameIndex] == null) {
            initFrame();
        }
        mFrames[mFrameIndex].addBall(remainingPins, foul, speed);
        if (isStrikeSpare(mFrames[mFrameIndex].getBallRawScore(mBallIndex))) {
            if (mFrameIndex < 9 && mBallIndex == 0) {//strike
                mFrameIndex++;
            }

        }
        mBallIndex++;
        scoreGame();
    }

    private void initFrame() {
        mFrames[mFrameIndex] = new BowlingFrame();
        mBallIndex = 0;
    }

    public boolean isFinished() {

        return false;
    }

    public void reset() {
        mFrames = new BowlingFrame[10];
        mFrameIndex = 0;
        mFinishedFrame = false;
    }

    public void setHandicap(int handicap) {
        handicap = mHandicap;
    }

    public void updateTo(BowlingGame bg) {
        mFrames = bg.getFrames();
        mFrameIndex = bg.getFrameIndex();
        mPlayerName = bg.getPlayerName();
        mHandicap = bg.getHandicap();
        mPlayerUUID = bg.getPlayerUUID();
        this.mTap = bg.mTap;
        this.mGameScore = bg.getGameScore();
    }

    public BowlingFrame[] getFrames() {
        return mFrames;
    }

    public int getFrameIndex() {
        return mFrameIndex;
    }

    public String getPlayerName() {
        return mPlayerName;
    }

    public int getHandicap() {
        return mHandicap;
    }

    public String getPlayerUUID() {
        return mPlayerUUID;
    }

    public int getTap() {
        return mTap;
    }

    @Override
    public String toString() {
        String result = mPlayerName + ": ";
        for (int x = 0; x < mFrames.length; x++) {
            if (mFrames[x] == null) {
                result += "[  ]";
            } else {
                result += "[";
                result += getBallStringValue(x, 0);
                result += " ";
                result += getBallStringValue(x, 1);
                if (x == 9) {
                    result += " ";
                    result += getBallStringValue(x, 2);
                }
                result += "]";
            }
        }
        result += "handicap: " + mHandicap + " Score: " + mGameScore;

        return result;
    }

    public int getGameScore() {
        return this.mGameScore;
    }

    public String getBallStringValue(int frame, int ball) {
        String ret = " ";
        if (frame < 10 && mFrames[frame] != null && ball < 3) {
            int ball1 = mFrames[frame].getBallRawScore(0);
            int ball2 = mFrames[frame].getBallRawScore(1);
            int ball2Diff = ball2 - ball1;
            int ball3 = mFrames[frame].getBallRawScore(2);
            int ball3Diff = ball3 - ball1;

            if (ball == 0) {
                ret = isStrikeSpare(ball1) ? "X" : Integer.toString(ball1);
                ret = ball1 == 0 ? "-" : ret;
            } else if (ball == 1) {
                ret = isStrikeSpare(ball2) ? "/" : Integer.toString(ball2Diff);
                ret = (isStrikeSpare(ball1) && frame < 9) ? " " : ret;
                ret = (isStrikeSpare(ball2) && frame == 9) ? "X" : ret;
                ret = ball1 == ball2 && !isStrikeSpare(ball1) ? "-" : ret;
            } else if (ball == 2) {
                ret = isStrikeSpare(ball3) ? "X" : Integer.toString(ball3Diff);
                ret = isStrikeSpare(ball3) && !isStrikeSpare(ball2) ? "/" : ret;
                ret = (ball2 == ball3 && !isStrikeSpare(ball2)) ? "-" : ret;
            }
            ret = mFrames[frame].isFoul(ball) ? "F" : ret;
        }
        return ret;
    }

    private boolean isStrikeSpare(int rawScore) {
        return rawScore >= mTap;
    }

    public boolean isFrameScored(int frame) {
        if (frame > mFrameIndex) {
            return false;
        }
        return mFrames[frame].getFrameScore() > -1;
    }

    public void scoreGame() {
        this.mGameScore = 0;
        ArrayList<String> balls = new ArrayList<>();
        for (int i = 0; i < mFrameIndex; i++) {
            String ball = getBallStringValue(i, 0);         //Ball 1
            if (ballStringtoInt(ball) >= 0) {
                balls.add(ball);
                ball = getBallStringValue(i, 1);            //Ball 2
                if (ballStringtoInt(ball) >= 0) {
                    balls.add(ball);
                    ball = getBallStringValue(i, 2);        //Ball 3
                    if (ballStringtoInt(ball) >= 0) {
                        balls.add(ball);
                    }
                }
            }
        }// end collect balls
        int ballIndex = 0;
        for (int i = 0; i <= mFrameIndex; i++) {
            int frameScore = 0;
            if (ballIndex < balls.size()) {
                int ball = ballStringtoInt(balls.get(ballIndex));
                if (ball == 10) {
                    if (ballIndex + 2 < balls.size()) {
                        ball += ballStringtoInt(balls.get(ballIndex + 1));
                        ball += ballStringtoInt(balls.get(ballIndex + 2));
                    }
                } else { //ball2
                    ballIndex++;
                    if (ballIndex < balls.size()) {
                        int ball2 = ballStringtoInt(balls.get(ballIndex));
                        if (ball2 == 10 && i < 9) {
                            if (ballIndex + 1 < balls.size()) {
                                ball2 = ballStringtoInt(balls.get(ballIndex + 1));
                            }
                        }
                        ball += ball2;
                    }
                }
                frameScore += ball;
            }
            if (frameScore > 0) {
                this.mGameScore += frameScore;
                if (this.mFrames[i] != null) {
                    this.mFrames[i].setFrameScore(this.mGameScore);
                }
            }
        }

    }

    private int ballStringtoInt(String s) {
        switch (s) {
            case "X":
                return 10;
            case "/":
                return 10;
            case "F":
                return 0;
            case " ":
                return -1;
            case "-":
                return 0;
            default:
                try {
                    return Integer.parseInt(s);
                } catch (Exception e) {
                    return 0;
                }
        }
    }
}
