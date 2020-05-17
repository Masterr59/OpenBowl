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
import java.util.Timer;
import javafx.beans.property.ObjectProperty;
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
import javafx.scene.layout.HBox;
import org.openbowl.common.UserRole;

/**
 *
 * @author Open Bowl <http://www.openbowlscoring.org/>
 */
public class TabDesk extends CommonTab implements Initializable {

    private final String HEADER_TEXT = "Front Desk";
    private final String TAB_TEXT = "Desk";
    private final long DEFAULT_PULL_PERIOD = 300000l;    // 5 minutes
    private final long INITIAL_DELAY = 10000l;           // 10 seconds
    public static final String PREFS_PULL_PERIOD = "Lane_Pull_Period";

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

    @FXML
    HBox hbox;

    Register mRegister;
    private ArrayList<LaneDisplay> laneDisplays;
    private ArrayList<LaneCheckTask> laneCheckers;
    private Timer timer;

    public TabDesk(ObjectProperty<AuthorizedUser> User, ObjectProperty<AuthorizedUser> Manager, DatabaseConnector db) throws IOException {
        super(User, Manager, db);
        this.setText(TAB_TEXT);
        this.setHeaderLabel(HEADER_TEXT);
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/openbowl/client/DeskTab.fxml"));
        loader.setController(this);
        Parent root = loader.load();

        mVBox.getChildren().add(root);
        mRegister = new Register();

        laneDisplays = new ArrayList<>();
        laneCheckers = new ArrayList<>();

        this.lanes = lanePane.getChildren();
        timer = new Timer();

    }

    @Override
    protected void onUserChange(AuthorizedUser newUser) {
        super.onUserChange(newUser);
        if (Permission.get(UserRole.GAME_ADMIN)) {
            loadLanes(newUser);
        } else {
            this.lanes.clear();
            this.laneCheckers.clear();
            this.laneDisplays.clear();
        }
    }

    protected void onManagerChange(AuthorizedUser newManager) {
        super.onManagerChange(newManager);
        if (Permission.get(UserRole.GAME_ADMIN)) {
            loadLanes(newManager);
        } else {
            this.lanes.clear();
            this.laneCheckers.clear();
            this.laneDisplays.clear();
        }
    }

    private void loadLanes(AuthorizedUser u) {
        stopTimers();
        long period = mPrefs.getLong(PREFS_PULL_PERIOD, DEFAULT_PULL_PERIOD);
        for (int i = 0; i < dbConnector.getNumLanes(u); i++) {
            LaneDisplay lane = new LaneDisplay(String.format("Lane %d", i));
            String style = PermissionStyle.get(UserRole.GAME_ADMIN);
            lane.setStyle(style);

            LaneCheckTask checkTask = new LaneCheckTask(dbConnector, i);
            lane.CrashProperty().bind(checkTask.CrashProperty());
            lane.GameStatusProperty().bind(checkTask.GameStatusProperty());
            lane.HeatProperty().bind(checkTask.HeatProperty());
            lane.OnlineProperty().bind(checkTask.OnlineProperty());
            lane.RebootProperty().bind(checkTask.RebootProperty());
            lane.UpdateProperty().bind(checkTask.UpdateProperty());
            lane.VoltProperty().bind(checkTask.VoltProperty());

            lanes.add(lane);
            this.laneDisplays.add(lane);
            this.laneCheckers.add(checkTask);
            timer.schedule(checkTask, INITIAL_DELAY * i, period);
        }
    }

    @Override
    public void initialize(URL arg0, ResourceBundle arg1) {
        if (hbox != null) {
            try {
                mRegister = new Register();
            } catch (IOException ex) {

            }
            hbox.getChildren().add(mRegister);
        }
    }

    public void stopTimers() {
        for (LaneCheckTask task : this.laneCheckers) {
            task.cancel();
        }
        timer.purge();
    }
    
    public void killTimers(){
        for (LaneCheckTask task : this.laneCheckers) {
            task.cancel();
        }
        this.laneCheckers.clear();
        timer.cancel();
        timer = null;
    }
}
