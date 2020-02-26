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
package org.openbowl.scorer;

import com.pi4j.io.gpio.GpioController;
import com.pi4j.io.gpio.GpioFactory;
import com.sun.net.httpserver.HttpServer;
import java.io.IOException;
import java.util.LinkedList;
import java.util.Queue;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SeparatorMenuItem;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;
import javafx.scene.layout.BorderPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.openbowl.common.AboutOpenBowl;
import org.openbowl.common.WebFunctions;
import org.openbowl.scorer.remote.PinSetterHandler;

/**
 *
 * @author Open Bowl <http://www.openbowlscoring.org/>
 */
public class MainApp extends Application {

    private final String ApplicationName = "Open Bowl - Scorer";
    private HttpServer remoteControl;
    private FakeBowlerDialogController oddBowler, evenBowler;
    private Lane oddLane, evenLane;
    private Queue<BowlingSession> sessionQueue;

    @Override
    public void start(Stage stage) throws Exception {
        sessionQueue = new LinkedList<>();
        oddLane = new Lane("odd");
        evenLane = new Lane("even");
        if (RaspberryPiDetect.isPi()) {
            oddLane.setBall(new BasicDetector("Odd_Ball_Detector"));
            oddLane.setFoul(new BasicDetector("Odd_Foul_Detector"));
            oddLane.setSweep(new BasicDetector("Odd_Sweep_Detector"));
            oddLane.setPinSetter(new BasicPinSetter("Odd_PinSetter"));
            oddLane.setPinCounter(new BasicPinCounter("Odd_PinCounter"));

            evenLane.setBall(new BasicDetector("Even_Ball_Detector"));
            evenLane.setFoul(new BasicDetector("Even_Foul_Detector"));
            evenLane.setSweep(new BasicDetector("Even_Sweep_Detector"));
            evenLane.setPinSetter(new BasicPinSetter("Even_PinSetter"));
            evenLane.setPinCounter(new BasicPinCounter("Even_PinCounter"));

        } else {
            oddBowler = new FakeBowlerDialogController("odd");
            oddLane.setBall(oddBowler.getBall());
            oddLane.setFoul(oddBowler.getFoul());
            oddLane.setSweep(oddBowler.getSweep());
            oddLane.setPinSetter(oddBowler.getPinsetter());
            oddLane.setPinCounter(oddBowler.getCounter());

            evenBowler = new FakeBowlerDialogController("even");
            evenLane.setBall(evenBowler.getBall());
            evenLane.setFoul(evenBowler.getFoul());
            evenLane.setSweep(evenBowler.getSweep());
            evenLane.setPinSetter(evenBowler.getPinsetter());
            evenLane.setPinCounter(evenBowler.getCounter());

            oddBowler.initModality(Modality.NONE);
            oddBowler.show();

            evenBowler.initModality(Modality.NONE);
            evenBowler.show();

        }
        oddLane.getBall().addEventHandler(DetectedEvent.DETECTION, notUsed -> onBallDetected("odd"));
        evenLane.getBall().addEventHandler(DetectedEvent.DETECTION, notUsed -> onBallDetected("even"));

        oddLane.getFoul().addEventHandler(DetectedEvent.DETECTION, notUsed -> onFoulDetected("odd"));
        evenLane.getFoul().addEventHandler(DetectedEvent.DETECTION, notUsed -> onFoulDetected("even"));

        oddLane.getSweep().addEventHandler(DetectedEvent.DETECTION, notUsed -> onSweepDetected("odd"));
        evenLane.getSweep().addEventHandler(DetectedEvent.DETECTION, notUsed -> onSweepDetected("even"));

        BorderPane root = new BorderPane();
        root.setTop(buildMenuBar());

        stage.setTitle(ApplicationName);
        root.setTop(buildMenuBar());

        remoteControl = WebFunctions.createDefaultServer();
        remoteControl.createContext("/pinsetter/odd/", new PinSetterHandler(oddLane.getPinSetter(), 1));
        remoteControl.createContext("/pinsetter/even/", new PinSetterHandler(evenLane.getPinSetter(), 2));

        remoteControl.start();

        Scene scene = new Scene(root, 500, 440);
        stage.setScene(scene);
        stage.show();

    }

    private MenuBar buildMenuBar() {
        // build a menu bar
        MenuBar menuBar = new MenuBar();
        // File menu with just a quit item for now
        Menu fileMenu = new Menu("_File");
        MenuItem quitMenuItem = new MenuItem("_Quit");
        quitMenuItem.setAccelerator(new KeyCodeCombination(KeyCode.Q,
                KeyCombination.CONTROL_DOWN));
        quitMenuItem.setOnAction(notUsed -> onQuit());
        fileMenu.getItems().add(quitMenuItem);

        Menu testMenu = new Menu("_Test");
        MenuItem testOddPinCounter = new MenuItem("Test Odd Pin Detector");
        testOddPinCounter.setOnAction(notUsed -> onCountPins("Odd", oddLane.getPinCounter()));

        MenuItem testEvenPinCounter = new MenuItem("Test Even Pin Detector");
        testEvenPinCounter.setOnAction(notUsed -> onCountPins("Even", evenLane.getPinCounter()));

        testMenu.getItems().addAll(testOddPinCounter, testEvenPinCounter);

        Menu configMenu = new Menu("_Configure");
        MenuItem oddPinSetterConfig = new MenuItem("OddPinSetter");
        oddPinSetterConfig.setOnAction(notUsed -> oddLane.getPinSetter().configureDialog());

        MenuItem evenPinSetterConfig = new MenuItem("EvenPinSetter");
        evenPinSetterConfig.setOnAction(notUsed -> evenLane.getPinSetter().configureDialog());

        MenuItem oddFoulConf = new MenuItem("OddFoulDetect");
        oddFoulConf.setOnAction(notUsed -> oddLane.getFoul().configureDialog());

        MenuItem evenFoulConf = new MenuItem("EvenFoulDetect");
        evenFoulConf.setOnAction(notUsed -> evenLane.getFoul().configureDialog());

        MenuItem oddBallConf = new MenuItem("OddBallDetect");
        oddBallConf.setOnAction(notUsed -> oddLane.getBall().configureDialog());

        MenuItem evenBallConf = new MenuItem("EvenBallDetect");
        evenBallConf.setOnAction(notUsed -> evenLane.getBall().configureDialog());

        MenuItem oddPinCounterConfig = new MenuItem("OddPinCounter");
        oddPinCounterConfig.setOnAction(notUsed -> oddLane.getPinCounter().configureDialog());

        MenuItem evenPinCounterConfig = new MenuItem("EvenPinCounter");
        evenPinCounterConfig.setOnAction(notUsed -> evenLane.getPinCounter().configureDialog());

        configMenu.getItems().addAll(oddPinSetterConfig, oddFoulConf, oddBallConf, oddPinCounterConfig,
                new SeparatorMenuItem(), evenPinSetterConfig, evenFoulConf, evenBallConf, evenPinCounterConfig);

        Menu maintMenu = new Menu("_Maintenance");
        MenuItem oddPinSetterMaint = new MenuItem("OddPinSetter");
        oddPinSetterMaint.setOnAction(notUsed -> onPinSetterMaint(oddLane.getPinSetter(), "OddPinSetter"));

        MenuItem evenPinSetterMaint = new MenuItem("EvenPinSetter");
        evenPinSetterMaint.setOnAction(notUsed -> onPinSetterMaint(evenLane.getPinSetter(), "EvenPinSetter"));

        maintMenu.getItems().addAll(oddPinSetterMaint, evenPinSetterMaint);

        Menu helpMenu = new Menu("_Help");
        MenuItem aboutMenuItem = new MenuItem("_About");
        aboutMenuItem.setOnAction(actionEvent -> onAbout());
        helpMenu.getItems().add(aboutMenuItem);
        menuBar.getMenus().addAll(fileMenu, configMenu, maintMenu, testMenu, helpMenu);
        return menuBar;
    }

    public static void main(String[] args) {
        launch(args);
    }

    private void onAbout() {
        AboutOpenBowl about = new AboutOpenBowl();
        about.onAbout(ApplicationName);
    }

    private void onBallDetected(String lane) {
        System.out.println("Ball Detected on lane: " + lane);
    }

    private void onFoulDetected(String lane) {
        System.out.println("Foul Detected on lane: " + lane);
    }

    private void onSweepDetected(String lane) {
        System.out.println("Sweep detected on lane: " + lane);
    }

    private void onCountPins(String lane, PinCounter p) {
        System.out.println("Counting pins on lane: " + lane);
        System.out.println(p.countPins());
    }

    private void onQuit() {

        if (RaspberryPiDetect.isPi()) {
            oddLane.getPinSetter().setPower(false);
            evenLane.getPinSetter().setPower(false);
            oddLane.getPinSetter().teardown();
            evenLane.getPinSetter().teardown();
            oddLane.getFoul().teardown();
            evenLane.getFoul().teardown();
            oddLane.getBall().teardown();
            evenLane.getBall().teardown();
            oddLane.getSweep().teardown();
            evenLane.getSweep().teardown();
            GpioController gpioController = GpioFactory.getInstance();
            gpioController.shutdown();
        }
        remoteControl.stop(0);
        Platform.exit();
    }

    private void onPinSetterMaint(PinSetter p, String n) {
        try {
            PinSetterMaintenanceController dialog = new PinSetterMaintenanceController(p, n);
            dialog.setTitle(n);
            dialog.initModality(Modality.NONE);
            dialog.show();
        } catch (IOException e) {
            System.out.println("Error showing dialog " + e.toString());
        }
    }

    private void onTestNumberedSession(Lane l, DisplayConnector d, int numGames) {
        NumberedSession session = new NumberedSession(l, d, numGames);
    }
}
