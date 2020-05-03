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
package org.openbowl.client;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.geometry.Side;
import javafx.scene.Scene;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SeparatorMenuItem;
import javafx.scene.control.TabPane;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import org.openbowl.common.AboutOpenBowl;
import org.openbowl.common.AuthorizedUser;

/**
 *
 * @author Open Bowl <http://www.openbowlscoring.org/>
 */
public class MainApp extends Application {

    private final String ApplicationName = "Open Bowl - Client";

    private TabPane mTabPane;
    private DatabaseConnector dbConnector;
    private TabLogin UserLoginTab, ManagerLoginTab;
    private TabUser UserTab;
    private ObjectProperty<AuthorizedUser> User;
    private ObjectProperty<AuthorizedUser> Manager;

    @Override
    public void start(Stage stage) throws Exception {
        dbConnector = new MockDB();
        ManagerLoginTab = new TabLogin(dbConnector);
        UserLoginTab = new TabLogin(dbConnector);

        User = new SimpleObjectProperty<>(AuthorizedUser.NON_USER);
        Manager = new SimpleObjectProperty<>(AuthorizedUser.NON_USER);

        User.addListener((Obs, oldU, newU) -> onUserChange(newU));
        Manager.addListener((Obs, oldU, newU) -> onManagerChange(newU));

        BorderPane root = new BorderPane();
        root.setTop(buildMenuBar());

        stage.setTitle(ApplicationName);

        mTabPane = new TabPane();
        mTabPane.setRotateGraphic(true);
        mTabPane.setSide(Side.LEFT);

        root.setCenter(mTabPane);
        Scene scene = new Scene(root, 500, 440);
        scene.getStylesheets().add(getClass().getResource("DarkMode.css").toExternalForm());
        
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

        MenuItem login = new MenuItem("Login");
        login.setOnAction(not_used -> onLogin());

        MenuItem logout = new MenuItem("Logout");
        logout.setOnAction(not_used -> onLogout());

        fileMenu.getItems().addAll(login, logout, new SeparatorMenuItem(), quitMenuItem);

        Menu managerMenu = new Menu("_Manager");
        MenuItem managerLogin = new MenuItem("Login");
        managerLogin.setOnAction(not_used -> onManagerLogin());

        MenuItem managerLogout = new MenuItem("Logout");
        managerLogout.setOnAction(not_used -> onManagerLogout());

        managerMenu.getItems().addAll(managerLogin, managerLogout);

        Menu helpMenu = new Menu("_Help");
        MenuItem aboutMenuItem = new MenuItem("_About");
        aboutMenuItem.setOnAction(actionEvent -> onAbout());
        helpMenu.getItems().add(aboutMenuItem);
        menuBar.getMenus().addAll(fileMenu, managerMenu, helpMenu);
        return menuBar;
    }

    public static void main(String[] args) {
        launch(args);
    }

    private void onAbout() {
        AboutOpenBowl about = new AboutOpenBowl();
        about.onAbout(ApplicationName);
    }

    private void onManagerLogin() {
        synchronized (mTabPane) {
            mTabPane.getTabs().remove(ManagerLoginTab);
            ManagerLoginTab = new TabLogin(dbConnector);
            ManagerLoginTab.getManagerLogin().set(true);
            ManagerLoginTab.getManager().bindBidirectional(Manager);
            mTabPane.getTabs().add(ManagerLoginTab);
        }
    }

    private void onManagerLogout() {
        Manager.set(AuthorizedUser.NON_USER);
    }

    private void onLogin() {
        synchronized (mTabPane) {
            mTabPane.getTabs().remove(UserLoginTab);
            UserLoginTab = new TabLogin(dbConnector);
            UserLoginTab.getUser().bindBidirectional(User);
            mTabPane.getTabs().add(UserLoginTab);
        }
    }

    private void onLogout() {
        User.set(AuthorizedUser.NON_USER);
    }

    private void onUserChange(AuthorizedUser newU) {
        synchronized (mTabPane) {
            mTabPane.getTabs().remove(UserLoginTab);
            mTabPane.getTabs().remove(UserTab);
            if (newU != AuthorizedUser.NON_USER) {
                UserTab = new TabUser(newU);
                mTabPane.getTabs().add(UserTab);
            }
        }
    }

    private void onManagerChange(AuthorizedUser newU) {
        synchronized (mTabPane) {
            mTabPane.getTabs().remove(ManagerLoginTab);
        }
    }
}
