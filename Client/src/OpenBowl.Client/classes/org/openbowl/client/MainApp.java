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

import java.util.Timer;
import java.util.prefs.Preferences;
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
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.image.Image;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import org.openbowl.common.AboutOpenBowl;
import org.openbowl.common.AuthorizedUser;
import org.openbowl.common.CommonImages;
import org.openbowl.common.ExitTask;
import org.openbowl.common.UserRole;

/**
 *
 * @author Open Bowl <http://www.openbowlscoring.org/>
 */
public class MainApp extends Application {
    public static final String DEFAULT_WEB_CLIENT_SITE = "http://northbowl.openbowlscoring.org";
    public static final String PREFS_WEB_CLIENT_SITE = "WebClientSite";    
    
    private final String ApplicationName = "Open Bowl - Client";

    private TabPane mTabPane;
    private DatabaseConnector dbConnector;
    private TabLogin UserLoginTab, ManagerLoginTab;
    private TabUser UserTab;
    private TabDesk DeskTab;
    private TabOffice OfficeTab;
    private TabMechanic MechTab;
    private TabReservations RsrvTab;
    private TabCompetitions CompTab;
    private TabBowlers BowlerTab;
    private TabGeneralSales SalesTab;
    private ObjectProperty<AuthorizedUser> User;
    private ObjectProperty<AuthorizedUser> Manager;

    private Preferences mPrefs;

    @Override
    public void start(Stage stage) throws Exception {
        dbConnector = new MockDB();
        ManagerLoginTab = new TabLogin(dbConnector);
        UserLoginTab = new TabLogin(dbConnector);

        User = new SimpleObjectProperty<>(AuthorizedUser.NON_USER);
        Manager = new SimpleObjectProperty<>(AuthorizedUser.NON_USER);

        User.addListener((Obs, oldU, newU) -> onUserChange(newU));
        Manager.addListener((Obs, oldU, newU) -> onManagerChange(newU));

        DeskTab = new TabDesk(User, Manager, dbConnector);
        OfficeTab = new TabOffice(User, Manager, dbConnector);
        MechTab = new TabMechanic(User, Manager, dbConnector);
        RsrvTab = new TabReservations(User, Manager, dbConnector);
        CompTab = new TabCompetitions(User, Manager, dbConnector);
        BowlerTab = new TabBowlers(User, Manager, dbConnector);
        SalesTab = new TabGeneralSales(User, Manager, dbConnector);

        BorderPane root = new BorderPane();
        root.setTop(buildMenuBar());

        stage.setTitle(ApplicationName);

        mTabPane = new TabPane();
        mTabPane.setRotateGraphic(true);
        mTabPane.setSide(Side.LEFT);

        mTabPane.getSelectionModel().selectedItemProperty().addListener((obs, oTab, nTab) -> onTabChange(nTab));

        root.setCenter(mTabPane);
        Scene scene = new Scene(root, 500, 440);
        scene.getStylesheets().add(getClass().getResource("DarkMode.css").toExternalForm());

        stage.setScene(scene);
        stage.show();

        mPrefs = Preferences.userNodeForPackage(this.getClass());
        CommonImages cImage = new CommonImages();
        stage.getIcons().add(cImage.appIcon());
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
        quitMenuItem.setOnAction(actionEvent -> onQuit());

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
            mTabPane.getSelectionModel().select(ManagerLoginTab);
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
            mTabPane.getSelectionModel().select(UserLoginTab);
        }
    }

    private void onLogout() {
        User.set(AuthorizedUser.NON_USER);
    }

    private void onUserChange(AuthorizedUser newU) {
        synchronized (mTabPane) {
            mTabPane.getTabs().clear();
            if (newU != AuthorizedUser.NON_USER) {
                UserTab = new TabUser(newU, dbConnector);
                mTabPane.getTabs().add(UserTab);

                if (DeskTabAuthorized(newU)) {
                    DeskTab.setStyleManager(false);
                    mTabPane.getTabs().add(DeskTab);
                }
                if (SalesTabAuthorized(newU)) {
                    SalesTab.setStyleManager(false);
                    mTabPane.getTabs().add(SalesTab);
                }
                if (OfficeTabAuthorized(newU)) {
                    OfficeTab.setStyleManager(false);
                    mTabPane.getTabs().add(OfficeTab);
                }
                if (MechTabAuthorized(newU)) {
                    MechTab.setStyleManager(false);
                    mTabPane.getTabs().add(MechTab);
                }
                if (RsrvTabAuthorized(newU)) {
                    RsrvTab.setStyleManager(false);
                    mTabPane.getTabs().add(RsrvTab);
                }
                if (CompTabAuthorized(newU)) {
                    CompTab.setStyleManager(false);
                    mTabPane.getTabs().add(CompTab);
                }
                if (BowlersTabAuthorized(newU)) {
                    BowlerTab.setStyleManager(false);
                    mTabPane.getTabs().add(BowlerTab);
                }
            }
        }
    }

    private void onManagerChange(AuthorizedUser newM) {
        onUserChange(User.get()); //reset
        synchronized (mTabPane) {
            mTabPane.getTabs().remove(ManagerLoginTab);
            if (!mTabPane.getTabs().contains(DeskTab) && DeskTabAuthorized(newM)) {
                DeskTab.setStyleManager(true);
                mTabPane.getTabs().add(DeskTab);
            }
            if (!mTabPane.getTabs().contains(SalesTab) && SalesTabAuthorized(newM)) {
                SalesTab.setStyleManager(true);
                mTabPane.getTabs().add(SalesTab);
            }
            if (!mTabPane.getTabs().contains(OfficeTab) && OfficeTabAuthorized(newM)) {
                OfficeTab.setStyleManager(true);
                mTabPane.getTabs().add(OfficeTab);
            }
            if (!mTabPane.getTabs().contains(MechTab) && MechTabAuthorized(newM)) {
                MechTab.setStyleManager(true);
                mTabPane.getTabs().add(MechTab);
            }
            if (!mTabPane.getTabs().contains(RsrvTab) && RsrvTabAuthorized(newM)) {
                RsrvTab.setStyleManager(true);
                mTabPane.getTabs().add(RsrvTab);
            }
            if (!mTabPane.getTabs().contains(CompTab) && CompTabAuthorized(newM)) {
                CompTab.setStyleManager(true);
                mTabPane.getTabs().add(CompTab);
            }
            if (!mTabPane.getTabs().contains(BowlerTab) && BowlersTabAuthorized(newM)) {
                BowlerTab.setStyleManager(true);
                mTabPane.getTabs().add(BowlerTab);
            }
        }
    }

    private boolean DeskTabAuthorized(AuthorizedUser u) {
        return u.isAuthorized(UserRole.GAME_ADMIN);
    }

    private boolean SalesTabAuthorized(AuthorizedUser u) {
        return u.isAuthorized(UserRole.TRANSACTION_ADD);
    }

    private boolean BowlersTabAuthorized(AuthorizedUser u) {
        return u.isAuthorized(UserRole.GAME_ADMIN);
    }

    private boolean MechTabAuthorized(AuthorizedUser u) {
        return u.isAuthorized(UserRole.MANAGE_SCORER)
                || u.isAuthorized(UserRole.MANAGE_DISPLAY);
    }

    private boolean OfficeTabAuthorized(AuthorizedUser u) {
        return u.isAuthorized(UserRole.GENERATE_REPORTS)
                || u.isAuthorized(UserRole.USER_ADD)
                || u.isAuthorized(UserRole.USER_DELETE)
                || u.isAuthorized(UserRole.USER_RENAME);
    }

    private boolean RsrvTabAuthorized(AuthorizedUser u) {
        return u.isAuthorized(UserRole.GAME_ADMIN);
    }

    private boolean CompTabAuthorized(AuthorizedUser u) {
        return u.isAuthorized(UserRole.GAME_ADMIN);
    }

    private void onTabChange(Tab nTab) {
        if (nTab instanceof CommonTab) {
            CommonTab ct = (CommonTab) nTab;
            ct.onTabSelected();
        }
    }

    private void onQuit() {
        DeskTab.stopTimers();
        DeskTab.killTimers();
        Platform.exit();
        Timer timer = new Timer();
        timer.schedule(new ExitTask(0), ExitTask.DEFAULT_EXIT_TIME);
    }
}
