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

import javafx.geometry.Orientation;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.Separator;
import javafx.scene.layout.VBox;
import org.openbowl.common.AuthorizedUser;
import org.openbowl.common.Styles;
import org.openbowl.common.UserRole;

/**
 *
 * @author Open Bowl <http://www.openbowlscoring.org/>
 */
public class TabUser extends CommonTab {

    private final String WELCOME_TEXT = "Welcome %s to OpenBowl";
    private final String PASSWORD_UPDATE_PROMPT = "Password update:";
    private final String PASSWORD_OLD = "Old Password";
    private final String PASSWORD_NEW = "New Password";
    private final String PASSWORD_CONF = "Confirm Password";
    private final String UPDATE_BUTTON_TEXT = "Update Password";
    private final String PRIVILAGES_TEXT = "Privilages: ";
    private final String ERROR_MISMATCH = "ERROR: Your new password and confirmation password do not match.";
    private final String ERROR_EMPTY = "ERROR: Empty passwords are not allowed.";
    private final String ERROR_EMPTY_OLD = "ERROR: Please enter your old password.";
    private final String UPDATING = "Updating Password";

    private PasswordField oldPassword;
    private PasswordField newPassword;
    private PasswordField confPassword;
    private Button updatePassword;
    private Label errorLabel;
    
    private AuthorizedUser User;
    
    private DatabaseConnector dbConnector;

    public TabUser(AuthorizedUser user, DatabaseConnector db) {
        this.getUser().set(user);
        this.setText(user.getUsername());
        dbConnector = db;
        User = user;
        VBox vbox = new VBox();
        String WelcomeText = String.format(WELCOME_TEXT, user.getUsername());

        Label WelcomeLabel = new Label(WelcomeText);
        WelcomeLabel.setId(Styles.ID_H2);

        Separator s1 = new Separator();
        s1.setOrientation(Orientation.HORIZONTAL);

        Label PasswordLabel = new Label(PASSWORD_UPDATE_PROMPT);
        PasswordLabel.setId(Styles.ID_H3);
        oldPassword = new PasswordField();
        oldPassword.setPromptText(PASSWORD_OLD);
        newPassword = new PasswordField();
        newPassword.setPromptText(PASSWORD_NEW);
        confPassword = new PasswordField();
        confPassword.setPromptText(PASSWORD_CONF);

        updatePassword = new Button(UPDATE_BUTTON_TEXT);
        updatePassword.setId(Styles.ID_SUBMIT_BUTTON);
        updatePassword.setOnAction(not_used -> onUpdatePassword());

        errorLabel = new Label();

        Separator s2 = new Separator();
        s2.setOrientation(Orientation.HORIZONTAL);

        Label PrivilagesLabel = new Label(PRIVILAGES_TEXT);
        PrivilagesLabel.setId(Styles.ID_H3);

        vbox.getChildren().addAll(WelcomeLabel, s1, PasswordLabel, oldPassword,
                newPassword, confPassword, updatePassword, errorLabel, s2,
                PrivilagesLabel);

        for (UserRole ur : user.getRoles()) {
            Label role = new Label("\t" + ur.toString());
            vbox.getChildren().add(role);
        }

        Border.setCenter(vbox);
    }

    private void onUpdatePassword() {
        if (oldPassword.getText().isEmpty()) {
            errorLabel.setText(ERROR_EMPTY_OLD);
        } else if (newPassword.getText().equals(confPassword.getText())) {
            if (newPassword.getText().isBlank()) {
                errorLabel.setText(ERROR_EMPTY);
            } else {
                errorLabel.setText(UPDATING);
                String updateStatus = dbConnector.updateUserPassword(User, 
                        oldPassword.getText(), newPassword.getText());
                errorLabel.setText(updateStatus);
            }
        } else {
            errorLabel.setText(ERROR_MISMATCH);
        }

    }

}
