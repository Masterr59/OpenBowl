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

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;
import javafx.beans.property.ObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import org.openbowl.common.AuthorizedUser;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.FlowPane;
import org.openbowl.common.UserRole;

/**
 *
 * @author Open Bowl <http://www.openbowlscoring.org/>
 */
public class TabDesk extends CommonTab implements Initializable {

    private final String HEADER_TEXT = "Front Desk";
    private final String TAB_TEXT = "Desk";

    private ObservableList<Node> lanes;

    @FXML
    Button clearBtn;

    @FXML
    Button getReceiptBtn;

    @FXML
    Button otherBtn;

    @FXML
    Label laneNumMin;

    @FXML
    Label laneNumMax;

    @FXML
    FlowPane lanePane;

    @FXML
    FlowPane sebDepartmentPane;

    @FXML
    FlowPane packagePane;

    @FXML
    FlowPane productPane;

    @FXML
    FlowPane modifierPane;

    public TabDesk(ObjectProperty<AuthorizedUser> User, ObjectProperty<AuthorizedUser> Manager, DatabaseConnector db) throws IOException {
        super(User, Manager, db);
        this.setText(TAB_TEXT);
        this.setHeaderLabel(HEADER_TEXT);
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/openbowl/client/DeskTab.fxml"));
        loader.setController(this);
        Parent root = loader.load();

        mVBox.getChildren().add(root);

        this.lanes = lanePane.getChildren();

    }

    @Override
    protected void onUserChange(AuthorizedUser newUser) {
        super.onUserChange(newUser);
        if (Permission.get(UserRole.GAME_ADMIN)) {
            loadLanes(newUser);
        } else {
            this.lanes.clear();
        }
    }

    protected void onManagerChange(AuthorizedUser newManager) {
        super.onManagerChange(newManager);
        if (Permission.get(UserRole.GAME_ADMIN)) {
            loadLanes(newManager);
        } else {
            this.lanes.clear();
        }
    }

    private void loadLanes(AuthorizedUser u) {
        for (int i = 0; i < dbConnector.getNumLanes(u); i++) {
            LaneDisplay lane = new LaneDisplay(String.format("Lane %d", i));
            String style = PermissionStyle.get(UserRole.GAME_ADMIN);
            lane.setStyle(style);
            lanes.add(lane);
        }
    }

    @Override
    public void initialize(URL arg0, ResourceBundle arg1) {

    }

}
