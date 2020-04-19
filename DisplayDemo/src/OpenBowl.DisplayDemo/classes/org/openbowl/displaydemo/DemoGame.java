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

import com.google.gson.Gson;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import org.openbowl.common.BowlingGame;
import org.openbowl.common.BowlingPins;
import org.openbowl.common.BowlingSplash;
import org.openbowl.scorer.DisplayConnector;

/**
 *
 * @author Open Bowl <http://www.openbowlscoring.org/>
 */
public class DemoGame {

    private final String FRED = "Fred F.";
    private final String BARNEY = "Barney R.";
    private final String SLATE = "Mr. Slate";
    private final String GAZOO = "GR8 Gazoo";

    private final String GEORGE = "George";
    private final String JANE = "Jane";
    private final String JUDY = "Judy";
    private final String ELROY = "Elroy";

    private final ArrayList<BowlingPins> STD_PINS[];

    private final Gson gson;
    private String displayAddress;
    private final Random rand;

    public DemoGame(String displayAddress) {
        this.gson = new Gson();
        this.displayAddress = displayAddress;
        this.rand = new Random();
        STD_PINS = new ArrayList[11];
        for (int i = 0; i < 11; i++) {
            STD_PINS[i] = new ArrayList<>();
            for (int j = 0; j < i; j++) {
                STD_PINS[i].add(BowlingPins.values()[j]);
            }
        }
    }

    void runDemo() {
        Thread evenThread = new Thread(new evenGame(this.displayAddress));
        Thread oddThread = new Thread(new oddGame(this.displayAddress));
        evenThread.start();
        oddThread.start();
        try {
            evenThread.join();
            oddThread.join();
        } catch (InterruptedException ex) {
            System.out.println("Demo Interupted");
        }

    }

    private class evenGame implements Runnable {

        private final DisplayConnector display;
        private final String players[] = {GEORGE, JANE, JUDY, ELROY};

        private Map<String, BowlingGame> game;

        public evenGame(String displayAddress) {
            this.game = new HashMap<>();
            this.display = new DisplayConnector("even", "none");
            for (String s : players) {
                this.game.put(s, new BowlingGame(s, s));
            }

            this.game.get(JUDY).setHandicap(10);
            this.game.get(ELROY).setHandicap(42);

            Map<String, Object> displaySettings = new HashMap<>();
            displaySettings.put(this.display.ADDRESS_SETTING, displayAddress);
            displaySettings.put(this.display.ENDPOINT_SETTING, "even");
            displaySettings.put("Type", this.display.getClass().getName());
            this.display.setConfiguration(displaySettings);
        }

        @Override
        public void run() {

            display.showMessageCard("Welcome", -1);
            sleep();
            sleep(10000);

            display.showMessageCard("NONE", -1);
            for (String s : players) {
                sleep(2000);
                display.newPlayer(game.get(s));
            }
            for (int i = 0; i < 10; i++) {
                for (int j = 0; j < players.length; j++) {
                    sleep(2000);
                    boolean split = rand.nextInt(100) < 15;
                    boolean splitSuccess = rand.nextInt(100) < 15;
                    int ball1 = rand.nextInt(11);
                    int ball2 = rand.nextInt(11);
                    if (ball2 > ball1) {
                        int t = ball2;
                        ball2 = ball1;
                        ball1 = t;
                    }
                    game.get(players[j]).addBall(STD_PINS[ball1], false, 25);
                    display.setScore(game.get(players[j]), j);
                    if (ball1 == 0) {
                        game.get(players[j]).addEmptyBall();
                        display.showSplash(BowlingSplash.Strike);
                        sleep();
                        sleep(2000);
                    } else {
                        if (ball1 == 10) {
                            display.showSplash(BowlingSplash.Gutter);
                            sleep();
                        }
                        if (split) {
                            display.showSplash(BowlingSplash.Split);
                            sleep();

                        }
                        sleep(2000);
                        game.get(players[j]).addBall(STD_PINS[ball2], false, 25);
                        display.setScore(game.get(players[j]), j);
                        if (ball2 == 0) {
                            if (splitSuccess) {
                                display.showSplash(BowlingSplash.Split_Success);
                            } else {
                                display.showSplash(BowlingSplash.Spare);
                            }
                            sleep();
                            sleep(2000);
                        } else if (ball2 == ball1) {
                            display.showSplash(BowlingSplash.Gutter);
                            sleep();
                            sleep(2000);
                        }

                    }
                    
                    if(i == 4 && j == 2){
                        display.showMessageCard("Pause", -1);
                        for(int z = 0; z < 10; z++){
                            sleep();
                        }
                        display.showMessageCard("NONE", -1);
                    }
                }
            }
        }

        private void sleep(long Max) {
            long randLong = rand.nextLong() % Max;
            randLong += 500;
            randLong = (randLong < 0) ? (randLong * -1) : randLong;
            try {
                Thread.sleep(randLong);
            } catch (InterruptedException ex) {
                System.out.println("Demo Thread Interupted");
            }
        }

        private void sleep() {
            try {
                Thread.sleep(6000);
            } catch (InterruptedException ex) {
                System.out.println("Demo Thread Interupted");
            }
        }

    }

    private class oddGame implements Runnable {

        private final Map<String, BowlingGame> game;
        private final DisplayConnector display;

        public oddGame(String displayAddress) {
            this.game = new HashMap<>();

            this.game.put(FRED, new BowlingGame(FRED, FRED));
            this.game.put(BARNEY, new BowlingGame(BARNEY, BARNEY));
            this.game.put(SLATE, new BowlingGame(SLATE, SLATE));
            this.game.put(GAZOO, new BowlingGame(GAZOO, GAZOO));

            this.game.get(SLATE).setHandicap(19);

            this.display = new DisplayConnector("odd", "none");

            Map<String, Object> displaySettings = new HashMap<>();
            displaySettings.put(this.display.ADDRESS_SETTING, displayAddress);
            displaySettings.put(this.display.ENDPOINT_SETTING, "odd");
            displaySettings.put("Type", this.display.getClass().getName());

            this.display.setConfiguration(displaySettings);

        }

        @Override
        public void run() {
            display.showMessageCard("Welcome", -1);
            sleep();
            sleep(10000);
            display.showMessageCard("NONE", -1);
            sleep(1000);
            display.newPlayer(game.get(FRED));
            sleep(2000);
            display.newPlayer(game.get(BARNEY));
            sleep(2000);
            display.newPlayer(game.get(SLATE));
            sleep(2000);
            display.newPlayer(game.get(GAZOO));

            // Frame 1
            //Fred X
            addBall(FRED, 0, 0, 0, false, false);
            display.showSplash(BowlingSplash.Strike);
            sleep();

            //Barney 7 /
            addBall(BARNEY, 1, 3, 0, false, false);
            display.showSplash(BowlingSplash.Spare);
            sleep();

            //Slate 5, f
            addBall(SLATE, 2, 5, 5, false, true);

            //GAZOO 0, /
            addBall(GAZOO, 3, 10, 0, false, false);
            display.showSplash(BowlingSplash.Spare);
            sleep();

            //Frame 2
            //Fred X
            addBall(FRED, 0, 0, 0, false, false);
            display.showSplash(BowlingSplash.Strike_2);
            sleep();

            //Barney 6 2
            addBall(BARNEY, 1, 4, 2, false, false);

            //Slate 0, 5
            addBall(SLATE, 2, 10, 5, false, false);

            //GAZOO X
            addBall(GAZOO, 3, 0, 0, false, false);
            display.showSplash(BowlingSplash.Strike);
            sleep();

            //Frame 3
            //Fred X
            addBall(FRED, 0, 0, 0, false, false);
            display.showSplash(BowlingSplash.Turkey);
            sleep();

            //Barney X
            addBall(BARNEY, 1, 0, 0, false, false);
            display.showSplash(BowlingSplash.Strike);
            sleep();

            //Slate 8, 1
            addBall(SLATE, 2, 2, 1, false, false);

            //GAZOO 6 /
            addBall(GAZOO, 3, 4, 0, false, false);
            display.showSplash(BowlingSplash.Spare);
            sleep();

            //Frame 4
            //Fred X
            addBall(FRED, 0, 0, 0, false, false);
            display.showSplash(BowlingSplash.Strike_4);
            sleep();

            //Barney X
            addBall(BARNEY, 1, 0, 0, false, false);
            display.showSplash(BowlingSplash.Strike_2);
            sleep();

            //Slate F, 5
            addBall(SLATE, 2, 5, 5, true, false);

            //GAZOO X
            addBall(GAZOO, 3, 0, 0, false, false);
            display.showSplash(BowlingSplash.Strike);
            sleep();

            //Frame 5
            //Fred X
            addBall(FRED, 0, 0, 0, false, false);
            display.showSplash(BowlingSplash.Strike_5);
            sleep();

            //Barney 7 /
            addBall(BARNEY, 1, 3, 0, false, false);
            display.showSplash(BowlingSplash.Spare);
            sleep();

            //Slate 2, 5
            addBall(SLATE, 2, 8, 5, false, false);

            //GAZOO X
            addBall(GAZOO, 3, 0, 0, false, false);
            display.showSplash(BowlingSplash.Strike_2);
            sleep();

            //Frame 6
            //Fred X
            addBall(FRED, 0, 0, 0, false, false);
            display.showSplash(BowlingSplash.Strike_6);
            sleep();

            //Barney X
            addBall(BARNEY, 1, 0, 0, false, false);
            display.showSplash(BowlingSplash.Strike);
            sleep();

            //Slate 7, 2
            addBall(SLATE, 2, 3, 1, false, false);

            //GAZOO 1 /
            addBall(GAZOO, 3, 9, 0, false, false);
            display.showSplash(BowlingSplash.Spare);
            sleep();

            //Frame 7
            //Fred X
            addBall(FRED, 0, 0, 0, false, false);
            display.showSplash(BowlingSplash.Strike_7);
            sleep();

            //Barney X
            addBall(BARNEY, 1, 0, 0, false, false);
            display.showSplash(BowlingSplash.Strike_2);
            sleep();

            //Slate 3, 5
            addBall(SLATE, 2, 7, 5, false, false);

            //GAZOO X
            addBall(GAZOO, 3, 0, 0, false, false);
            display.showSplash(BowlingSplash.Strike);
            sleep();

            //Frame 8
            //Fred X
            addBall(FRED, 0, 0, 0, false, false);
            display.showSplash(BowlingSplash.Strike_8);
            sleep();

            //Barney X
            addBall(BARNEY, 1, 0, 0, false, false);
            display.showSplash(BowlingSplash.Turkey);
            sleep();

            //Slate 7, 2
            addBall(SLATE, 2, 3, 1, false, false);

            //GAZOO 3 /
            addBall(GAZOO, 3, 7, 0, false, false);
            display.showSplash(BowlingSplash.Spare);
            sleep();

            //Frame 9
            //Fred X
            addBall(FRED, 0, 0, 0, false, false);
            display.showSplash(BowlingSplash.Strike_9);
            sleep();

            //Barney 9 0
            addBall(BARNEY, 1, 1, 1, false, false);

            //Slate F, F
            addBall(SLATE, 2, 3, 1, true, true);

            //GAZOO X
            addBall(GAZOO, 3, 0, 0, false, false);
            display.showSplash(BowlingSplash.Strike);
            sleep();

            //Frame 10
            //Fred X X X
            addBall(FRED, 0, 0, 0, false, false);
            display.showSplash(BowlingSplash.Strike_10);
            sleep();
            sleep(2000);
            game.get(FRED).addBall(STD_PINS[0], false, 25);
            display.setScore(game.get(FRED), 0);
            display.showSplash(BowlingSplash.Strike_11);
            sleep();
            sleep(2000);
            game.get(FRED).addBall(STD_PINS[0], false, 25);
            display.setScore(game.get(FRED), 0);
            display.showSplash(BowlingSplash.PerfectGame);
            sleep();
            sleep();
            sleep();
            sleep();
            sleep();
            sleep(2000);

            //Barney 7 0
            addBall(BARNEY, 1, 3, 3, false, false);

            //Slate 0, 5
            addBall(SLATE, 2, 10, 5, false, false);

            //GAZOO 5 / X
            addBall(GAZOO, 3, 5, 0, false, false);
            display.showSplash(BowlingSplash.Spare);
            sleep();
            game.get(GAZOO).addBall(STD_PINS[0], false, 25);
            display.setScore(game.get(GAZOO), 3);
            display.showSplash(BowlingSplash.Strike);
            sleep();
        }

        private void sleep(long Max) {
            long randLong = rand.nextLong() % Max;
            randLong += 500;
            randLong = (randLong < 0) ? (randLong * -1) : randLong;
            try {
                Thread.sleep(randLong);
            } catch (InterruptedException ex) {
                System.out.println("Demo Thread Interupted");
            }
        }

        private void sleep() {
            try {
                Thread.sleep(6000);
            } catch (InterruptedException ex) {
                System.out.println("Demo Thread Interupted");
            }
        }

        private void addBall(String Player, int id, int b1, int b2, boolean f1, boolean f2) {
            sleep(2000);
            game.get(Player).addBall(STD_PINS[b1], f1, 25);
            display.setScore(game.get(Player), id);
            if (f1) {
                display.showSplash(BowlingSplash.Foul);
                sleep();
            } else if (b1 == 10) {
                display.showSplash(BowlingSplash.Gutter);
                sleep();
            }
            if (b1 > 0) {
                sleep(2000);
                game.get(Player).addBall(STD_PINS[b2], f2, 25);
                display.setScore(game.get(Player), id);
                if (f2) {
                    display.showSplash(BowlingSplash.Foul);
                    sleep();
                } else if (b1 == b2) {
                    display.showSplash(BowlingSplash.Gutter);
                    sleep();
                }
            } else {
                game.get(Player).addEmptyBall();
            }
        }
    }
}
