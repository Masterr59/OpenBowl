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
package org.openbowl.displaydemo;

/**
 *
 * @author Open Bowl <http://www.openbowlscoring.org/>
 */
public class MainApp {
    
    public static void main(String[] args) {
        if(args.length == 1){
            System.out.printf("Display Address: %s\n", args[0]);
            DemoGame game = new DemoGame(args[0]);
            //DemoSimple game = new DemoSimple(args[0]);
            game.runDemo();
        }
        else{
            System.out.println("Requires Display Address");
        }
    }
    
}
