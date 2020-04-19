/*
 * Copyright (C) 2020 Megatron
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

// DEPRECITATED / UNDER CONSTRUCTION


import java.util.Scanner;

/**
 *
 * @author Megatron
 */
public class FakeGameSession {
    
    public static void main(String[] args)
    {
      BowlingGame bowlingGame = new BowlingGame("Stu Steiner", 100, "0000");
      
      bowl(bowlingGame);
      System.out.println("*** GAME FINISHED ***");
   }
   
   
   private static void bowl(BowlingGame bowlingGame)
   {
      Scanner kb = new Scanner(System.in);
      
//      while (!bowlingGame.isFinished())
//      {
//    	  ArrayList<BowlingPins> pins = promptPins();
//    	  if (pins < 0 || pinsRemaining > 10)
//    		  bowlingGame.bowled(pinsRemaining, true, 20.0);
//    	  else
//    	     bowlingGame.bowled(pinsRemaining, false, 20.0);
//    	  System.out.println(bowlingGame);
//      }
//      
      kb.close();
   }

    
}
