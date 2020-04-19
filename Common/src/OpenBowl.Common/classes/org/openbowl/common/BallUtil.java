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

/**
 *
 * @author Open Bowl <http://www.openbowlscoring.org/>
 */
public class BallUtil 
{
   public static boolean isNum(Ball ball)
   {
	   if (ball.equals(Ball.STRIKE) || ball.equals(Ball.SPARE) 
			   || ball.equals(Ball.NOTUSED) 
			   || ball.equals(Ball.FOUL) )
		   return false;
	   else
		   return true;
   }
   
   public static char toChar(Ball ball)
   {
	   switch(ball)
	   {
	      case ZERO:    return '-';
	      case ONE:     return '1';
	      case TWO:     return '2';
	      case THREE:   return '3';
	      case FOUR:    return '4';
	      case FIVE:    return '5';
	      case SIX:     return '6';
	      case SEVEN:   return '7';
	      case EIGHT:   return '8';
	      case NINE:    return '9';
	      case STRIKE:  return 'X';
	      case SPARE:   return '/';
	      case NOTUSED: return ' ';
	      case FOUL:    return 'F';
	   }
	   
	   return ' ';
   }
   
   public static Ball toBall(char charVal)
   {
	   switch(charVal)
	   {
	      case '-':  return Ball.ZERO;
	      case '1':  return Ball.ONE;
	      case '2':  return Ball.TWO;
	      case '3':  return Ball.THREE;
	      case '4':  return Ball.FOUR;
	      case '5':  return Ball.FIVE;
	      case '6':  return Ball.SIX;
	      case '7':  return Ball.SEVEN;
	      case '8':  return Ball.EIGHT;
	      case '9':  return Ball.NINE;
	      case 'X':  return Ball.STRIKE;
	      case '/':  return Ball.SPARE;
	      case ' ':  return Ball.NOTUSED;
	      case 'F':  return Ball.FOUL;
	      default:   return Ball.NOTUSED;
	   }
   }
   
   public static Ball toBall(int num)
   {
	   switch(num)
	   {
	      case 1:  return Ball.ONE;
	      case 2:  return Ball.TWO;
	      case 3:  return Ball.THREE;
	      case 4:  return Ball.FOUR;
	      case 5:  return Ball.FIVE;
	      case 6:  return Ball.SIX;
	      case 7:  return Ball.SEVEN;
	      case 8:  return Ball.EIGHT;
	      case 9:  return Ball.NINE;
	      default:   return Ball.ZERO;
	   }
   }
   
   public static int toInt(Ball ball)
   {
	   switch(ball)
	   {
	      case ONE:    return 1;
	      case TWO:    return 2;
	      case THREE:  return 3;
	      case FOUR:   return 4;
	      case FIVE:   return 5;
	      case SIX:    return 6;
	      case SEVEN:  return 7;
	      case EIGHT:  return 8;
	      case NINE:   return 9;
	      default:     return 0;
	   }
   }
}
