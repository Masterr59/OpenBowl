/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.openbowl.display;

import com.sun.net.httpserver.HttpServer;
import java.io.File;
import java.util.prefs.Preferences;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.geometry.Rectangle2D;
import javafx.scene.Cursor;
import javafx.scene.Scene;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;
import javafx.scene.layout.StackPane;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;
import javafx.stage.Screen;
import javafx.stage.Stage;
import org.openbowl.common.AboutOpenBowl;
import org.openbowl.common.WebFunctions;

/**
 *
 * @author Open Bowl <http://www.openbowlscoring.org/>
 */
public class MainApp extends Application {

    public final String BACKGROUND_SETTING = "Background";
    public final String BACKGROUND_VALUE = "/org/openbowl/display/images/Background_default.jpg";
    public final static String MEDIA_FOLDER_SETTING = "Media_Folder";
    public final static String MEDIA_FOLDER_VALUE = "Default";

    private final int ODD = 0;
    private final int EVEN = 1;
    private final int NUM_GAMES = 2;
    private final String ApplicationName = "Open Bowl - Display";
    private Stage Stages[];
    private StackPane stackPanes[];
    private BowlingGameDisplay Game[];
    private ImageView[] Background;
    private HttpServer server;
    private Preferences prefs;

    @Override
    public void start(Stage stage) throws Exception {
        prefs = Preferences.userNodeForPackage(this.getClass());

        Stages = new Stage[NUM_GAMES];
        Stages[ODD] = stage;
        Stages[EVEN] = new Stage();

        Background = new ImageView[NUM_GAMES];
        stackPanes = new StackPane[NUM_GAMES];
        Game = new BowlingGameDisplay[NUM_GAMES];
        for (int i = 0; i < NUM_GAMES; i++) {
            stackPanes[i] = new StackPane();
            Game[i] = new BowlingGameDisplay();
            Background[i] = new ImageView();
        }

        setBackground();
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
        } else {
            Rectangle2D bounds = screens.get(0).getVisualBounds();
            leftBounds = new Rectangle2D(bounds.getMinX(), bounds.getMinY(), bounds.getWidth() / 2, bounds.getHeight());
            rightBounds = new Rectangle2D(bounds.getWidth() / 2.0, bounds.getMinY(), bounds.getWidth() / 2, bounds.getHeight());
        }
        setupStage(leftBounds, false, 0, ODD);
        setupStage(rightBounds, false, 1, EVEN);

        server = WebFunctions.createDefaultServer();
        server.createContext("/gamedisplay/odd/", new BowlingGameDisplayHandler(1, Game[ODD]));
        server.createContext("/gamedisplay/even/", new BowlingGameDisplayHandler(2, Game[EVEN]));
        server.createContext("/system/", new DisplaySystemHandler(this));
        server.createContext("/splash/odd/", new SplashHandler(stackPanes[ODD]));
        server.createContext("/splash/even/", new SplashHandler(stackPanes[EVEN]));
        server.start();
    }

    private MenuBar buildMenuBar(BowlingGameDisplay game) {
        // build a menu bar
        MenuBar menuBar = new MenuBar();
        // File menu with just a quit item for now
        Menu fileMenu = new Menu("_File");
        MenuItem quitMenuItem = new MenuItem("_Quit");
        quitMenuItem.setAccelerator(new KeyCodeCombination(KeyCode.Q,
                KeyCombination.CONTROL_DOWN));
        quitMenuItem.setOnAction(notUsed -> onQuit());

        MenuItem resetMenuItem = new MenuItem("_Reset Game");
        resetMenuItem.setOnAction(notUsed -> onResetGame(game));

        fileMenu.getItems().addAll(resetMenuItem, quitMenuItem);

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

    public void onQuit() {
        server.stop(0);
        Platform.exit();
    }

    private void setupStage(Rectangle2D bounds, boolean fullscreen, int id, int display) {
        //stage.initStyle(StageStyle.UNDECORATED);
        Stages[display].setX(bounds.getMinX());
        Stages[display].setY(bounds.getMinY());
        Stages[display].setFullScreen(fullscreen);
        if (!fullscreen) {
            Stages[display].setWidth(bounds.getWidth());
            Stages[display].setHeight(bounds.getHeight());
        }

        Background[display].fitHeightProperty().bind(stackPanes[display].heightProperty());
        Background[display].fitWidthProperty().bind(stackPanes[display].widthProperty());

        Game[display].prefHeightProperty().bind(stackPanes[display].heightProperty());
        Game[display].prefWidthProperty().bind(stackPanes[display].widthProperty());

        stackPanes[display].getChildren().add(Background[display]);
        stackPanes[display].getChildren().add(Game[display]);

        Scene scene = new Scene(stackPanes[display]);

        scene.setCursor(Cursor.NONE);
        Stages[display].setScene(scene);

        Stages[display].show();

        Stages[display].setOnCloseRequest(notUsed -> onQuit());
    }

    private void onResetGame(BowlingGameDisplay game) {
        game.reset();
    }

    private void setBackground() {
        String defaultBackground = getClass().getResource(BACKGROUND_VALUE).toExternalForm();
        String backgroundURL = prefs.get(BACKGROUND_SETTING, defaultBackground);
        File tmp = new File(backgroundURL);
        if (!defaultBackground.equals(backgroundURL) && !tmp.isFile()) {
            System.out.println("Display - Background image not found");
            System.out.println(backgroundURL);
            backgroundURL = defaultBackground;
        }
        for (int i = 0; i < NUM_GAMES; i++) {

            Background[i].setImage(new Image(backgroundURL));
            Background[i].setPreserveRatio(false);
        }
    }
    
    public void applyTheme(){
        setBackground();
        for (int i = 0; i < NUM_GAMES; i++) {
            Game[i].loadColors();
            Game[1].draw();
        }
    }

}
