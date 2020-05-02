/*
 * Copyright (C) 2020 patrick
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
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ObservableValue;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.Tab;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import org.openbowl.common.AuthorizedUser;
import org.openbowl.common.Styles;

/**
 *
 * @author patrick
 */
public class TabLogin extends Tab {

    private final static String TAB_NAME = "Login";
    private final String MANAGER_PROMPT = "Manager";
    private final String USERNAME_PROMPT = "Username";
    private final String PASSWORD_PROMPT = "Password";
    private final String BUTTON_TEXT = "Log In";

    private AuthorizedUser User;
    private AuthorizedUser Manager;

    private TextField UsernameBox;
    private PasswordField PasswordBox;
    private Button LoginButton;

    private Label errorMsg;

    private BooleanProperty ManagerLogin;
    private StringProperty BorderText;
    BorderPane Border;

    public TabLogin() {
        super(TAB_NAME);

        this.setClosable(false);
        User = AuthorizedUser.NON_USER;
        Manager = AuthorizedUser.NON_USER;

        BorderText = new SimpleStringProperty();
        Border = new BorderPane();

        Label topText = new Label();
        topText.textProperty().bind(BorderText);

        Label bottomText = new Label();
        bottomText.textProperty().bind(BorderText);

        Border.setTop(topText);
        Border.setBottom(bottomText);

        VBox vbox = new VBox();
        vbox.setAlignment(Pos.CENTER);

        errorMsg = new Label();
        vbox.getChildren().add(errorMsg);

        HBox hbox = new HBox();
        hbox.setAlignment(Pos.CENTER);

        UsernameBox = new TextField();
        UsernameBox.setPromptText(USERNAME_PROMPT);

        PasswordBox = new PasswordField();
        PasswordBox.setPromptText(PASSWORD_PROMPT);

        LoginButton = new Button(BUTTON_TEXT);
        LoginButton.setOnAction(not_used -> processLogin());

        hbox.getChildren().addAll(UsernameBox, PasswordBox, LoginButton);

        vbox.getChildren().add(hbox);
        Border.setCenter(vbox);
        this.setContent(Border);

        ManagerLogin = new SimpleBooleanProperty(false);
        ManagerLogin.addListener((observable, oldValue, newValue) -> onManagerLoginChange(observable, oldValue, newValue));
    }

    private void processLogin() {
        String Username = UsernameBox.getText();
        String Password = PasswordBox.getText();
    }

    public AuthorizedUser getUser() {
        return User;
    }

    public AuthorizedUser getManager() {
        return Manager;
    }

    private void onManagerLoginChange(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
        String style = "";
        if (newValue) {
            BorderText.set(MANAGER_PROMPT);
            style = Styles.ManagerBorder;
        } else {
            BorderText.set("");
        }
        Border.setStyle(style);
        this.setStyle(style);
    }

    public BooleanProperty getManagerLogin() {
        return ManagerLogin;
    }
    
    

}
