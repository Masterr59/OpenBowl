/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.openbowl.display;

import com.sun.net.httpserver.HttpServer;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.geometry.Rectangle2D;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;
import javafx.scene.layout.BorderPane;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import org.openbowl.common.AboutOpenBowl;
import org.openbowl.common.WebFunctions;

/**
 *
 * @author Open Bowl <http://www.openbowlscoring.org/>
 */
public class MainApp extends Application {

    private final String ApplicationName = "Open Bowl - Display";
    private Stage primaryStage, secondaryStage;
    private BowlingGameDisplay oddGame, evenGame;
    private HttpServer server;

    @Override
    public void start(Stage stage) throws Exception {
        primaryStage = stage;
        secondaryStage = new Stage();
        oddGame = new BowlingGameDisplay();
        evenGame = new BowlingGameDisplay();
        
        ObservableList<Screen> screens = Screen.getScreens();//Get list of Screens
        System.out.println("NumScreens " + screens.size());
        double width = 0;
        for (int i = 0; i < screens.size(); i++) {
            Rectangle2D bounds = screens.get(i).getVisualBounds();
            System.out.println("Screen: " + i + " minX: " + bounds.getMinX() + " minY: " + bounds.getMinY());
        }
        Rectangle2D leftBounds;
        Rectangle2D rightBounds;
        if (screens.size() > 1) {
            leftBounds = screens.get(0).getVisualBounds();
            rightBounds = screens.get(1).getVisualBounds();
        }
        else{
            Rectangle2D bounds = screens.get(0).getVisualBounds();
            leftBounds = new Rectangle2D(bounds.getMinX(),bounds.getMinY(),bounds.getWidth() / 2, bounds.getHeight());
            rightBounds = new Rectangle2D(bounds.getWidth() / 2.0,bounds.getMinY(),bounds.getWidth() / 2, bounds.getHeight());
        }
        setupStage(primaryStage, leftBounds, false, 0, oddGame);
        setupStage(secondaryStage, rightBounds, false, 1, evenGame);

        server = WebFunctions.createDefaultServer();
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

        Menu helpMenu = new Menu("_Help");
        MenuItem aboutMenuItem = new MenuItem("_About");
        aboutMenuItem.setOnAction(actionEvent -> onAbout());
        helpMenu.getItems().add(aboutMenuItem);
        menuBar.getMenus().addAll(fileMenu, helpMenu);
        return menuBar;
    }

    public static void main(String[] args) {
        launch(args);
    }

    private void onAbout() {
        AboutOpenBowl about = new AboutOpenBowl();
        about.onAbout(ApplicationName);
    }

    private void onQuit() {
        server.stop(0);
        Platform.exit();
    }

    private void setupStage(Stage stage, Rectangle2D bounds, boolean fullscreen, int id, BowlingGameDisplay game) {
        //stage.initStyle(StageStyle.UNDECORATED);
        stage.setX(bounds.getMinX());
        stage.setY(bounds.getMinY());
        stage.setFullScreen(fullscreen);
        if (!fullscreen) {
            stage.setWidth(bounds.getWidth());
            stage.setHeight(bounds.getHeight());
        }
        Label label = new Label("Screen " + id);
        BorderPane root = new BorderPane();
        root.setTop(buildMenuBar());
        root.setCenter(game);
        root.setBottom(label);
        //stage.setTitle(ApplicationName);
        Scene scene = new Scene(root);
        stage.setScene(scene);

        stage.show();
    }

}
