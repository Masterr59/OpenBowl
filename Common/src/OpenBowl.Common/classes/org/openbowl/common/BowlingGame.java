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
public class BowlingGame
{
   private BowlingFrame[] mFrames;
   private int mFrameIndex;
   private String mPlayerName;
   private int mHandicap;
   private boolean mFinishedFrame;
   private String mPlayerUUID;


   public BowlingGame(String playerName, int handicap, String playerUUID)
   {
      mFrames = new BowlingFrame[10];
      mFrameIndex = 0;
      mPlayerName = playerName;
      mHandicap = handicap;
      mFinishedFrame = false;
      mPlayerUUID = playerUUID;
   }
   
   
   public void bowled(ArrayList<BowlingPins> remainingPins, boolean foul, double speed)
   {	   
      if (mFrames[mFrameIndex] == null)
		  initFrame();
		  
      if (!mFrames[mFrameIndex].isFinished())
      {
         mFrames[mFrameIndex].addBall(remainingPins, foul, speed);
         if (mFrames[mFrameIndex].isFinished())
             mFinishedFrame = true;
      }
      else
      {
            mFrameIndex++;
            if (!isFinished())
            {
                initFrame();
                mFrames[mFrameIndex].addBall(remainingPins, foul, speed);
            }
       }
   }
   
   private void initFrame()
   {
        if (mFrameIndex == mFrames.length-1)
           mFrames[mFrameIndex] = new BowlingFrame(true);
        else
           mFrames[mFrameIndex] = new BowlingFrame(false);
   }
   
   
   public boolean isFinished()
   {  
	  if (mFrameIndex >= mFrames.length)
		  return true;
	  else if (mFrameIndex == mFrames.length - 1) 
	  {
            if ( mFrames[mFrameIndex] != null && mFrames[mFrameIndex].isFinished())
                return true;
	  }
	  
	  return false;
   }
   
   public void reset()
   {
       mFrames = new BowlingFrame[10];
       mFrameIndex = 0;
       mFinishedFrame = false;
   }
   
   public void setHandicap(int handicap) { handicap = mHandicap; }
   public void setFinishedFrame(boolean finishedFrame)
   {
       mFinishedFrame = finishedFrame;
   }
   public void updateTo(BowlingGame bg)
   {
       mFrames = bg.getFrames();
       mFrameIndex = bg.getFrameIndex();
       mPlayerName = bg.getPlayerName();
       mHandicap = bg.getHandicap();
       mFinishedFrame = bg.getFinishedFrame();
       mPlayerUUID = bg.getPlayerUUID();
   }
   
   public boolean getFinishedFrame() { return mFinishedFrame; }
   public BowlingFrame[] getFrames() { return mFrames;        }
   public int getFrameIndex()        { return mFrameIndex;    }
   public String getPlayerName()     { return mPlayerName;    }
   public int getHandicap()          { return mHandicap;      }
   public String getPlayerUUID()     { return mPlayerUUID;    }

   
   @Override
   public String toString()
   {
	   String result = mPlayerName + ": ";
	   for (int x = 0; x < mFrames.length; x++)
	   {
		   if (mFrames[x] == null)
			   result += "[  ]";
		   else
			   result += mFrames[x].toString();
	   }
           result += "handicap: " + mHandicap + " Score: (tbd)";
	   
	   return result;
   }
}
