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

    private final String displayAddress;
    private final Random rand;
    private boolean showSplash;

    public DemoGame(String displayAddress) {

        this.displayAddress = displayAddress;
        this.showSplash = true;
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
        evenGame even = new evenGame(this.displayAddress);
        Thread evenThread = new Thread(even);
        oddGame odd = new oddGame(this.displayAddress);
        Thread oddThread = new Thread(odd);
        evenThread.start();
        oddThread.start();
        try {
            evenThread.join();
            oddThread.join();
        } catch (InterruptedException ex) {
            System.out.println("Demo Interupted");
        }
        System.out.println("Even games:\n:");
        even.showEnd();
        System.out.println("\n games:\n:");
        odd.showEnd();

    }

    void showSplash(boolean b) {
        this.showSplash = b;
    }

    private class evenGame implements Runnable {

        private final DisplayConnector display;
        private final String players[] = {GEORGE, JANE, JUDY, ELROY};

        private Map<String, BowlingGame> game;

        public evenGame(String displayAddress) {
            this.game = new HashMap<>();
            this.display = new DisplayConnector("even", "none");
            for (String s : players) {
                int hdcp = s.equals(JANE) ? 10 : 0;
                hdcp = s.equals(ELROY) ? 42 : 0;
                this.game.put(s, new BowlingGame(s, hdcp, s, 10));
            }

            Map<String, Object> displaySettings = new HashMap<>();
            displaySettings.put(this.display.ADDRESS_SETTING, displayAddress);
            displaySettings.put(this.display.ENDPOINT_SETTING, "even");
            displaySettings.put("Type", this.display.getClass().getName());
            this.display.setConfiguration(displaySettings);
        }

        @Override
        public void run() {
            display.newGame();
            if (showSplash) {
                display.showMessageCard("Welcome", -1);
                sleep();
                sleep(10000);
            }
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
                    display.setCurentPlayer(j);
                    game.get(players[j]).bowled(STD_PINS[ball1], false, 25);
                    display.setScore(game.get(players[j]), j);
                    if (ball1 == 0) {
                        showSplash(BowlingSplash.Strike);
                        sleep(2000);
                    } else {
                        if (ball1 == 10) {
                            showSplash(BowlingSplash.Gutter);
                        }
                        if (split) {
                            showSplash(BowlingSplash.Split);
                        }
                        sleep(2000);
                        game.get(players[j]).bowled(STD_PINS[ball2], false, 25);
                        display.setScore(game.get(players[j]), j);
                        if (ball2 == 0) {
                            if (splitSuccess) {
                                showSplash(BowlingSplash.Split_Success);
                            } else {
                                showSplash(BowlingSplash.Spare);
                            }

                            sleep(2000);
                        } else if (ball2 == ball1) {
                            showSplash(BowlingSplash.Gutter);
                            sleep(2000);
                        }

                    }

                    if (i == 4 && j == 2 && showSplash) {
                        display.showMessageCard("Pause", -1);
                        for (int z = 0; z < 10; z++) {
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

        private void showSplash(BowlingSplash sp) {
            if (showSplash) {
                display.showSplash(sp);
                sleep();
            }
        }

        public void showEnd() {
            for (BowlingGame g : game.values()) {
                System.out.println(g.toString());
            }
        }

    }

    private class oddGame implements Runnable {

        private final Map<String, BowlingGame> game;
        private final DisplayConnector display;

        public oddGame(String displayAddress) {
            this.game = new HashMap<>();

            this.game.put(FRED, new BowlingGame(FRED, 0, FRED, 10));
            this.game.put(BARNEY, new BowlingGame(BARNEY, 0, BARNEY, 10));
            this.game.put(SLATE, new BowlingGame(SLATE, 19, SLATE, 10));
            this.game.put(GAZOO, new BowlingGame(GAZOO, 0, GAZOO, 10));

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
            display.newGame();
            if (showSplash) {
                display.showMessageCard("Welcome", -1);
                sleep();
                sleep(10000);
            }
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
            showSplash(BowlingSplash.Strike);
            display.setCurentPlayer(0);
            //Barney 7 /
            addBall(BARNEY, 1, 3, 0, false, false);
            showSplash(BowlingSplash.Spare);
            display.setCurentPlayer(1);
            //Slate 5, f
            addBall(SLATE, 2, 5, 5, false, true);
            display.setCurentPlayer(2);
            //GAZOO 0, /
            display.setCurentPlayer(3);
            addBall(GAZOO, 3, 10, 0, false, false);
            showSplash(BowlingSplash.Spare);

            //Frame 2
            //Fred X
            display.setCurentPlayer(0);
            addBall(FRED, 0, 0, 0, false, false);
            showSplash(BowlingSplash.Strike_2);

            //Barney 6 2
            display.setCurentPlayer(1);
            addBall(BARNEY, 1, 4, 2, false, false);

            //Slate 0, 5
            display.setCurentPlayer(2);
            addBall(SLATE, 2, 10, 5, false, false);

            //GAZOO X
            display.setCurentPlayer(3);
            addBall(GAZOO, 3, 0, 0, false, false);
            showSplash(BowlingSplash.Strike);

            //Frame 3
            //Fred X
            display.setCurentPlayer(0);
            addBall(FRED, 0, 0, 0, false, false);
            showSplash(BowlingSplash.Turkey);

            //Barney X
            display.setCurentPlayer(1);
            addBall(BARNEY, 1, 0, 0, false, false);
            showSplash(BowlingSplash.Strike);

            //Slate 8, 1
            display.setCurentPlayer(2);
            addBall(SLATE, 2, 2, 1, false, false);

            //GAZOO 6 /
            display.setCurentPlayer(3);
            addBall(GAZOO, 3, 4, 0, false, false);
            showSplash(BowlingSplash.Spare);

            //Frame 4
            //Fred X
            display.setCurentPlayer(0);
            addBall(FRED, 0, 0, 0, false, false);
            showSplash(BowlingSplash.Strike_4);

            //Barney X
            display.setCurentPlayer(1);
            addBall(BARNEY, 1, 0, 0, false, false);
            showSplash(BowlingSplash.Strike_2);

            //Slate F, 5
            display.setCurentPlayer(2);
            addBall(SLATE, 2, 5, 5, true, false);

            //GAZOO X
            display.setCurentPlayer(3);
            addBall(GAZOO, 3, 0, 0, false, false);
            showSplash(BowlingSplash.Strike);

            //Frame 5
            //Fred X
            display.setCurentPlayer(0);
            addBall(FRED, 0, 0, 0, false, false);
            showSplash(BowlingSplash.Strike_5);

            //Barney 7 /
            display.setCurentPlayer(1);
            addBall(BARNEY, 1, 3, 0, false, false);
            showSplash(BowlingSplash.Spare);

            //Slate 2, 5
            display.setCurentPlayer(2);
            addBall(SLATE, 2, 8, 5, false, false);

            //GAZOO X
            display.setCurentPlayer(3);
            addBall(GAZOO, 3, 0, 0, false, false);
            showSplash(BowlingSplash.Strike_2);

            //Frame 6
            //Fred X
            display.setCurentPlayer(0);
            addBall(FRED, 0, 0, 0, false, false);
            showSplash(BowlingSplash.Strike_6);

            //Barney X
            display.setCurentPlayer(1);
            addBall(BARNEY, 1, 0, 0, false, false);
            showSplash(BowlingSplash.Strike);

            //Slate 7, 2
            display.setCurentPlayer(2);
            addBall(SLATE, 2, 3, 1, false, false);

            //GAZOO 1 /
            display.setCurentPlayer(3);
            addBall(GAZOO, 3, 9, 0, false, false);
            showSplash(BowlingSplash.Spare);

            //Frame 7
            //Fred X
            display.setCurentPlayer(0);
            addBall(FRED, 0, 0, 0, false, false);
            showSplash(BowlingSplash.Strike_7);

            //Barney X
            display.setCurentPlayer(1);
            addBall(BARNEY, 1, 0, 0, false, false);
            showSplash(BowlingSplash.Strike_2);

            //Slate 3, 5
            display.setCurentPlayer(2);
            addBall(SLATE, 2, 7, 5, false, false);

            //GAZOO X
            display.setCurentPlayer(3);
            addBall(GAZOO, 3, 0, 0, false, false);
            showSplash(BowlingSplash.Strike);

            //Frame 8
            //Fred X
            display.setCurentPlayer(0);
            addBall(FRED, 0, 0, 0, false, false);
            showSplash(BowlingSplash.Strike_8);

            //Barney X
            display.setCurentPlayer(1);
            addBall(BARNEY, 1, 0, 0, false, false);
            showSplash(BowlingSplash.Turkey);

            //Slate 7, 2
            display.setCurentPlayer(2);
            addBall(SLATE, 2, 3, 1, false, false);

            //GAZOO 3 /
            display.setCurentPlayer(3);
            addBall(GAZOO, 3, 7, 0, false, false);
            showSplash(BowlingSplash.Spare);

            //Frame 9
            //Fred X
            display.setCurentPlayer(0);
            addBall(FRED, 0, 0, 0, false, false);
            showSplash(BowlingSplash.Strike_9);

            //Barney 9 0
            display.setCurentPlayer(1);
            addBall(BARNEY, 1, 1, 1, false, false);

            //Slate F, F
            display.setCurentPlayer(2);
            addBall(SLATE, 2, 3, 1, true, true);

            //GAZOO X
            display.setCurentPlayer(3);
            addBall(GAZOO, 3, 0, 0, false, false);
            showSplash(BowlingSplash.Strike);

            //Frame 10
            //Fred X X X
            display.setCurentPlayer(0);
            addBall(FRED, 0, 0, 0, false, false);
            showSplash(BowlingSplash.Strike_10);

            sleep(2000);
            game.get(FRED).bowled(STD_PINS[0], false, 25);
            display.setScore(game.get(FRED), 0);
            showSplash(BowlingSplash.Strike_11);

            sleep(2000);
            game.get(FRED).bowled(STD_PINS[0], false, 25);
            display.setScore(game.get(FRED), 0);
            showSplash(BowlingSplash.PerfectGame);

            sleep();
            sleep();
            sleep();
            sleep();
            sleep(2000);

            //Barney 7 0
            display.setCurentPlayer(1);
            addBall(BARNEY, 1, 3, 3, false, false);

            //Slate 0, 5
            display.setCurentPlayer(2);
            addBall(SLATE, 2, 10, 5, false, false);

            //GAZOO 5 / X
            display.setCurentPlayer(3);
            addBall(GAZOO, 3, 5, 0, false, false);
            showSplash(BowlingSplash.Spare);

            game.get(GAZOO).bowled(STD_PINS[0], false, 25);
            display.setScore(game.get(GAZOO), 3);
            showSplash(BowlingSplash.Strike);

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
            game.get(Player).bowled(STD_PINS[b1], f1, 25);
            display.setScore(game.get(Player), id);
            if (f1) {
                showSplash(BowlingSplash.Foul);

            } else if (b1 == 10) {
                showSplash(BowlingSplash.Gutter);

            }
            if (b1 > 0) {
                sleep(2000);
                game.get(Player).bowled(STD_PINS[b2], f2, 25);
                display.setScore(game.get(Player), id);
                if (f2) {
                    showSplash(BowlingSplash.Foul);

                } else if (b1 == b2) {
                    showSplash(BowlingSplash.Gutter);

                }
            } else {
                //game.get(Player).addEmptyBall();
            }
        }

        private void showSplash(BowlingSplash sp) {
            if (showSplash) {
                display.showSplash(sp);
                sleep();
            }
        }

        public void showEnd() {
            for (BowlingGame g : game.values()) {
                System.out.println(g.toString());
            }
        }
    }
}
