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
package org.openbowl.server;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import org.openbowl.common.AboutOpenBowl;

/**
 *
 * @author Open Bowl <http://www.openbowlscoring.org/>
 */
public class MainApp extends Application {

    private final String ApplicationName = "Open Bowl - Server";

    @Override
    public void start(Stage stage) throws Exception {
        BorderPane root = new BorderPane();
        root.setTop(buildMenuBar());

        stage.setTitle(ApplicationName);
        root.setTop(buildMenuBar());

        Scene scene = new Scene(root, 500, 440);
        stage.setScene(scene);
        stage.show();

    }

    private MenuBar buildMenuBar() {
        // from pdf provided
        // build a menu bar
        MenuBar menuBar = new MenuBar();
        // File menu with just a quit item for now
        Menu fileMenu = new Menu("_File");
        MenuItem quitMenuItem = new MenuItem("_Quit");
        quitMenuItem.setAccelerator(new KeyCodeCombination(KeyCode.Q,
                KeyCombination.CONTROL_DOWN));
        quitMenuItem.setOnAction(actionEvent -> Platform.exit());
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
}

