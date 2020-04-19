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
public class BowlingFrame
{

   private final boolean mIs10thFrame;
   private final Ball[] mBalls;
   private final double[] mBallSpeeds;
   private ArrayList<BowlingPins>[] mPins;
   private int mCurrentBallIndex;
   private boolean mIsFinished;
   
   public BowlingFrame(boolean is10thFrame)
   {
      mIs10thFrame = is10thFrame;
      
      if (is10thFrame)
      {
         mBalls = new Ball[3];
         mBallSpeeds = new double[3];
         mPins = new ArrayList[3];
      }
         
      else
      {
         mBalls = new Ball[2];
         mBallSpeeds = new double[2];
         mPins = new ArrayList[2];
      }
         
      mCurrentBallIndex = 0;
      mIsFinished = false;
   }
   
   public void addBall(ArrayList<BowlingPins> remainingPins, boolean foul, double speed)
   {
	if (!mIsFinished)
	{
		switch (mCurrentBallIndex)
		{
		    case 0: addFirstBall(remainingPins, foul, speed);
		      break;
		    case 1: addSecondBall(remainingPins, foul, speed);
		       break;
		    default: addThirdBall(remainingPins, foul, speed);
		       break;
		}
	}
	   
	checkFinished();
   }
   
   private void addFirstBall(ArrayList<BowlingPins> remainingPins, boolean foul, double speed)
   {
       int pinsRemaining = remainingPins.size();
       mPins[0] = remainingPins;
       mBallSpeeds[0] = speed;
	   if (foul)
	   {
		mBalls[0] = Ball.FOUL;
                mCurrentBallIndex++;
	   }
	   else if (pinsRemaining == 0)
	   {
		   mBalls[0] = Ball.STRIKE;
		   if (!mIs10thFrame)
		       finishFrame();
		   else
			mCurrentBallIndex++;
	   }
	   else
	   {
		   switch (pinsRemaining)
		   {
		      case 1:  mBalls[0] = Ball.NINE;
		         break;
		      case 2:  mBalls[0] = Ball.EIGHT;
		         break;
		      case 3:  mBalls[0] = Ball.SEVEN;
		         break;
		      case 4:  mBalls[0] = Ball.SIX;
		         break;
		      case 5:  mBalls[0] = Ball.FIVE;
		         break;
		      case 6:  mBalls[0] = Ball.FOUR;
		         break;
		      case 7:  mBalls[0] = Ball.THREE;
		         break;
		      case 8:  mBalls[0] = Ball.TWO;
		         break;
		      case 9:  mBalls[0] = Ball.ONE;
		         break;
		      default:   mBalls[0] = Ball.ZERO;
		         break;
		   }
		   
		   mCurrentBallIndex++;
	   }
   }
   
   private void addSecondBall(ArrayList<BowlingPins> remainingPins, boolean foul, double speed)
   {
       int pinsRemaining = remainingPins.size();
       mPins[1] = remainingPins;
       mBallSpeeds[1] = speed;
	   if (foul)
	   {
		mBalls[1] = Ball.FOUL;
                mCurrentBallIndex++;
	   }
	   else if (pinsRemaining == 0)
	   {
		   if (mIs10thFrame)
		   {
			   if (mBalls[0].equals(Ball.STRIKE))
			      mBalls[1] = Ball.STRIKE;
			   else
				  mBalls[1] = Ball.SPARE;
			   mCurrentBallIndex++;
		   }
		   else
		   {
			   mBalls[1] = Ball.SPARE;
			   finishFrame();
		   }
	   }
	   else
	   {
		   addSecondBallHelper(pinsRemaining);
		   
	   }
   }
   
   private void addSecondBallHelper(int pinsRemaining)
   {
	   if (!BallUtil.isNum(mBalls[0]))
		   mBalls[1] = BallUtil.toBall(10 - pinsRemaining);
	   else
	   {
		   int prevBallCount = BallUtil.toInt(mBalls[0]);
		   int currentBallCount = 10 - pinsRemaining - prevBallCount;
		   mBalls[1] = BallUtil.toBall(currentBallCount);
	   }
	   
	   if (mIs10thFrame)
		   finishFrame();
	   else
	      mCurrentBallIndex++;
   }
   
   private void addThirdBall(ArrayList<BowlingPins> remainingPins, boolean foul, double speed)
   {
       int pinsRemaining = remainingPins.size();
       mPins[2] = remainingPins;
       mBallSpeeds[2] = speed;
       
	   if (foul)
		   mBalls[2] = Ball.FOUL;
	   else if (pinsRemaining <= 0)
	       mBalls[2] = Ball.STRIKE;
	   else
		   mBalls[2] = BallUtil.toBall(10 - pinsRemaining); 
	   finishFrame();
   }
   
   private void finishFrame()
   {
	   mCurrentBallIndex++;
	   while (mCurrentBallIndex < mBalls.length)
	   {
		   mBalls[mCurrentBallIndex] = Ball.NOTUSED;
		   mCurrentBallIndex++;
	   }
	   
	   checkFinished();
   }
   
   private void checkFinished()
   {
	   if (mCurrentBallIndex >= mBalls.length)
		   mIsFinished = true;
	   else
		   mIsFinished = false;
   }
   
   public boolean isFinished() { return mIsFinished; }
   
   
   public boolean is10thFrame() {  return mIs10thFrame;  }
   
   @Override
   public String toString()
   {
	   String result = "[";
	   for (int x = 0; x < mBalls.length; x++)
	   {
		   if (mBalls[x] == null)
		      result += " ";
		   else
		      result += BallUtil.toChar(mBalls[x]);
		   
		   result += " ";
	   }
	   
	   result += "]";
	   
	   return result;
   }
   
   private void checkBallIndex(int index)
   {
       if (index <0 || index > 3 || (index ==3 && !mIs10thFrame))
           throw new IllegalArgumentException("Ball index was out of bounds");
   }
   
   /*****************************
   *          GETTERS           *
   *****************************/
   public Ball getBall(int index)
   {
       checkBallIndex(index);
       return mBalls[index];
   }
   
   public Ball[] getBalls() { return mBalls; }
   public double getSpeed(int index)
   {
       checkBallIndex(index);
       return mBallSpeeds[index];
   }

}
