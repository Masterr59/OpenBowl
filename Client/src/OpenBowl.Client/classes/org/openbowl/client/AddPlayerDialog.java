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

import java.util.UUID;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import org.openbowl.common.AuthorizedUser;
import org.openbowl.common.SimplePlayer;

/**
 *
 * @author patrick
 */
public class AddPlayerDialog extends Alert {

    private final String MSG_TEXT = "Add User";

    private DatabaseConnector dbConnector;
    private AuthorizedUser user;
    private TextField playerName;
    private TextField playerUUID;
    private TextField playerHDCP;
    private ListView<SimplePlayer> list;

    public AddPlayerDialog(DatabaseConnector db, AuthorizedUser u) {
        super(AlertType.INFORMATION);
        this.getDialogPane().getStylesheets().add(getClass().getResource("DarkMode.css").toExternalForm());
        this.titleProperty().set(MSG_TEXT);
        this.headerTextProperty().set(MSG_TEXT);
        this.graphicProperty().set(null);
        this.dbConnector = db;
        this.user = u;

        this.playerName = new TextField("");
        this.playerUUID = new TextField(UUID.randomUUID().toString());
        this.playerUUID.focusedProperty().addListener(notUsed -> onUUIDFocus());
        this.playerHDCP = new TextField("0");

        this.list = new ListView<>();
        this.list.getItems().addAll(dbConnector.getPlayers(user));
        HBox hbox = new HBox();
        hbox.getChildren().add(this.list);
        GridPane gp = new GridPane();
        hbox.getChildren().add(gp);
        gp.add(new Label("Name:"), 0, 0);
        gp.add(new Label("UUID:"), 0, 1);
        gp.add(new Label("HDCP:"), 0, 2);
        gp.add(playerName, 1, 0);
        gp.add(playerUUID, 1, 1);
        gp.add(playerHDCP, 1, 2);

        this.list.getSelectionModel().selectedItemProperty().addListener((obs, o, n) -> onSelctionChange(n));

        this.getDialogPane().setContent(hbox);

    }

    private void onSelctionChange(SimplePlayer n) {
        if (n != null) {
            this.playerName.textProperty().set(n.getName());
            this.playerUUID.textProperty().set(n.getUuid());
            this.playerHDCP.textProperty().set(Integer.toString(n.getHdcp()));

        }
    }

    public SimplePlayer getPlayer() {
        String name = this.playerName.textProperty().get();
        String UUID = this.playerUUID.textProperty().get();
        String hdcpString = this.playerHDCP.textProperty().get();
        int hdcp = 0;
        boolean isInt = true;
        try {
            hdcp = Integer.parseInt(hdcpString);
        } catch (NumberFormatException ex) {
            isInt = false;
        }
        if (!name.isEmpty() && !UUID.isEmpty() && isInt) {
            return new SimplePlayer(name, UUID, hdcp);
        } else {
            return null;
        }

    }

    private void onUUIDFocus() {
        this.playerUUID.textProperty().set(UUID.randomUUID().toString());
    }
}
