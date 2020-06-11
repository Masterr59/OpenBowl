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
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.scene.control.Label;
import javafx.scene.control.Tab;
import javafx.scene.layout.BorderPane;
import org.openbowl.common.AuthorizedUser;
import org.openbowl.common.Styles;

/**
 *
 * @author Open Bowl <http://www.openbowlscoring.org/>
 */
public abstract class SimpleTab extends Tab {
    private final String MANAGER_PROMPT = "Manager";

    private final ObjectProperty<AuthorizedUser> User;
    private final ObjectProperty<AuthorizedUser> Manager;

    private final StringProperty BorderText;
    protected BorderPane Border;

    public SimpleTab() {
        this.setClosable(false);
        User = new SimpleObjectProperty<>(AuthorizedUser.NON_USER);
        Manager = new SimpleObjectProperty<>(AuthorizedUser.NON_USER);

        BorderText = new SimpleStringProperty();
        Border = new BorderPane();

        Label topText = new Label();
        topText.textProperty().bind(BorderText);

        Label bottomText = new Label();
        bottomText.textProperty().bind(BorderText);

        Border.setTop(topText);
        Border.setBottom(bottomText);
        this.setContent(Border);
    }

    public final ObjectProperty<AuthorizedUser> getUser() {
        return User;
    }

    public final ObjectProperty<AuthorizedUser> getManager() {
        return Manager;
    }

    public void setStyleManager(boolean isManager){
        String style = "";
        if (isManager) {
            BorderText.set(MANAGER_PROMPT);
            style = Styles.ManagerBorder;
        } else {
            BorderText.set("");
        }
        Border.setStyle(style);
        this.setStyle(style);
    }
}
