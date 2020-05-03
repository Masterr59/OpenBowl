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

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.geometry.Orientation;
import javafx.scene.control.Label;
import javafx.scene.control.Separator;
import javafx.scene.layout.VBox;
import org.openbowl.common.AuthorizedUser;
import org.openbowl.common.Styles;

/**
 *
 * @author Open Bowl <http://www.openbowlscoring.org/>
 */
public class TabCompetitions extends CommonTab {

    private final String HEADER_TEXT = "Competitions";
    private final String TAB_TEXT = "Comp.";

    private ObjectProperty<AuthorizedUser> User;
    private ObjectProperty<AuthorizedUser> Manager;

    DatabaseConnector dbConnector;

    public TabCompetitions(ObjectProperty<AuthorizedUser> User, ObjectProperty<AuthorizedUser> Manager, DatabaseConnector db) {
        User = new SimpleObjectProperty<>();
        Manager = new SimpleObjectProperty<>();
        this.User.bindBidirectional(User);
        this.Manager.bindBidirectional(Manager);

        this.User.addListener((obs, oldUser, newUser) -> onUserChange(newUser));
        this.Manager.addListener((obs, oldManager, newManager) -> onManagerChange(newManager));

        this.setText(TAB_TEXT);

        dbConnector = db;
        VBox vbox = new VBox();
        Label HeaderLabel = new Label(HEADER_TEXT);
        HeaderLabel.setId(Styles.ID_H2);

        Separator s1 = new Separator();
        s1.setOrientation(Orientation.HORIZONTAL);

        vbox.getChildren().addAll(HeaderLabel, s1);
        Border.setCenter(vbox);
    }

    private void onUserChange(AuthorizedUser newUser) {

    }

    private void onManagerChange(AuthorizedUser newManager) {

    }

}