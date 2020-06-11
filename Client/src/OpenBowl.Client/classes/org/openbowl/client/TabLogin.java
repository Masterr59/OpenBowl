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

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.value.ObservableValue;
import javafx.geometry.Orientation;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.Separator;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import org.openbowl.common.AuthorizedUser;
import org.openbowl.common.Styles;

/**
 *
 * @author Open Bowl <http://www.openbowlscoring.org/>
 */
public class TabLogin extends SimpleTab {

    private final static String TAB_NAME = "Login";

    private final String USERNAME_PROMPT = "Username";
    private final String PASSWORD_PROMPT = "Password";
    private final String BUTTON_TEXT = "Log In";
    private final String LOGIN_ERROR_TEXT = "Invalid username and password combination";
    private final String LOGIN_SUCCESS_TEXT = "Login Successful";

    private final TextField UsernameBox;
    private final PasswordField PasswordBox;
    private final Button LoginButton;

    private final Label errorMsg;

    private final BooleanProperty ManagerLogin;

    DatabaseConnector dbConnector;

    public TabLogin(DatabaseConnector connector) {
        this.setText(TAB_NAME);
        this.setClosable(true);
        dbConnector = connector;

        VBox vbox = new VBox();
        //vbox.setAlignment(Pos.CENTER);
        
        Label LoginLabel= new Label(BUTTON_TEXT);
        LoginLabel.setId(Styles.ID_H2);
        vbox.getChildren().add(LoginLabel);
        
        Separator s1 = new Separator();
        s1.setOrientation(Orientation.HORIZONTAL);
        vbox.getChildren().add(s1);

        errorMsg = new Label();
        vbox.getChildren().add(errorMsg);

        HBox hbox = new HBox();
        //hbox.setAlignment(Pos.CENTER);

        UsernameBox = new TextField();
        UsernameBox.setPromptText(USERNAME_PROMPT);

        PasswordBox = new PasswordField();
        PasswordBox.setPromptText(PASSWORD_PROMPT);

        LoginButton = new Button(BUTTON_TEXT);
        LoginButton.setOnAction(not_used -> processLogin());
        LoginButton.setId(Styles.ID_SUBMIT_BUTTON);

        hbox.getChildren().addAll(UsernameBox, PasswordBox, LoginButton);

        vbox.getChildren().add(hbox);
        Border.setCenter(vbox);

        ManagerLogin = new SimpleBooleanProperty(false);
        ManagerLogin.addListener((observable, oldValue, newValue) -> onManagerLoginChange(observable, oldValue, newValue));
    }

    private void processLogin() {
        String Username = UsernameBox.getText();
        String Password = PasswordBox.getText();

        AuthorizedUser newUser = dbConnector.login(Username, Password);
        if (newUser != AuthorizedUser.NON_USER) {
            errorMsg.setText(LOGIN_SUCCESS_TEXT);
            if (ManagerLogin.get()) {
                this.getManager().set(newUser);
            } else {
                this.getUser().set(newUser);
            }
            this.setDisable(true);
        } else {
            errorMsg.setText(LOGIN_ERROR_TEXT);

        }
    }

    private void onManagerLoginChange(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
        this.setStyleManager(newValue);
    }

    public BooleanProperty getManagerLogin() {
        return ManagerLogin;
    }

}
