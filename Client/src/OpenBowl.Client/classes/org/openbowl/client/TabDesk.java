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
import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.Timer;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.collections.ObservableList;
import org.openbowl.common.AuthorizedUser;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import org.openbowl.common.Styles;
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
    FlowPane subDepartmentPane;

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
    private ObservableList<Node> lanes;
    private IntegerProperty minSelected, maxSelected;
    private Timer timer;

    private Map<Integer, String> subDepartments;
    private Map<Integer, ArrayList<ProductUseage>> productMap;

    public TabDesk(ObjectProperty<AuthorizedUser> User, ObjectProperty<AuthorizedUser> Manager, DatabaseConnector db) throws IOException {
        super(User, Manager, db);
        this.setText(TAB_TEXT);
        this.setHeaderLabel(HEADER_TEXT);
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/openbowl/client/DeskTab.fxml"));
        loader.setController(this);
        Parent root = loader.load();

        mVBox.getChildren().add(root);

        laneDisplays = new ArrayList<>();
        laneCheckers = new ArrayList<>();

        this.lanes = lanePane.getChildren();
        timer = new Timer();
        minSelected = new SimpleIntegerProperty(-1);
        maxSelected = new SimpleIntegerProperty(-1);

        minSelected.addListener(notUsed -> updateLaneSelected());
        maxSelected.addListener(notUsed -> updateLaneSelected());
        updateLaneSelected();
        clearBtn.setOnAction(notUsed -> onClearBtn());

        subDepartments = new HashMap<Integer, String>();
        productMap = new HashMap<Integer, ArrayList<ProductUseage>>();
    }

    @Override
    protected void onUserChange(AuthorizedUser newUser) {
        super.onUserChange(newUser);
        this.lanes.clear();
        this.laneCheckers.clear();
        this.laneDisplays.clear();
        mRegister.setUser(AuthorizedUser.NON_USER);
        if (Permission.get(UserRole.GAME_ADMIN)) {
            loadLanes(newUser);
            mRegister.setUser(newUser);
        }
    }

    protected void onManagerChange(AuthorizedUser newManager) {
        super.onManagerChange(newManager);
        if (Permission.get(UserRole.GAME_ADMIN)) {
            loadLanes(newManager);
            mRegister.setUser(newManager);
        } else {
            mRegister.setUser(AuthorizedUser.NON_USER);
            this.lanes.clear();
            this.laneCheckers.clear();
            this.laneDisplays.clear();
        }
    }

    private void loadLanes(AuthorizedUser u) {
        stopTimers();
        long period = mPrefs.getLong(PREFS_PULL_PERIOD, DEFAULT_PULL_PERIOD);
        for (int i = 0; i < dbConnector.getNumLanes(u); i++) {
            LaneDisplay lane = new LaneDisplay(String.format("Lane %d", i + 1));
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
            int finalInt = i;
            lane.setOnMouseClicked(mouseEvent -> onLaneClicked(mouseEvent, finalInt));
            //lane.selectedProperty().addListener((obs, ob, nb) -> onLaneClicked(finalInt, nb));
        }
        subDepartments.clear();
        subDepartments.putAll(dbConnector.getDepartments(u));
        int bowlingDept = -1;
        for (int i : subDepartments.keySet()) {
            if (subDepartments.get(i).equals(DatabaseConnector.GAME_DEPARTMENT_NAME)) {
                bowlingDept = i;
            }
        }
        if (bowlingDept != -1) {
            subDepartments.clear();
            subDepartments.putAll(dbConnector.getSubDepartments(u, bowlingDept));
            productMap.clear();
            for (int i : subDepartments.keySet()) {
                productMap.put(i, dbConnector.getProducts(u, i));
            }
        }

        subDepartmentPane.getChildren().clear();
        for (int i : subDepartments.keySet()) {
            Button deptBtn = new Button(subDepartments.get(i));
            deptBtn.setId(Styles.ID_GREEN_BUTTON);
            deptBtn.setMinSize(Styles.MIN_BUTTON_SIZE, Styles.MIN_BUTTON_SIZE);
            int finalInt = i + 0;
            deptBtn.setOnAction(notUsed -> onDepartmentChange(finalInt));
            subDepartmentPane.getChildren().add(deptBtn);
        }

    }

    private void onDepartmentChange(int i) {
        productPane.getChildren().clear();
        packagePane.getChildren().clear();
        for (ProductUseage pu : productMap.get(i)) {
            Button btn = new Button(pu.getProduct_ID().getProduct_Name());
            btn.setMinSize(Styles.MIN_BUTTON_SIZE, Styles.MIN_BUTTON_SIZE);
            btn.setOnAction(notUsed -> onAddProductToRegister(pu));

            if (!pu.getChildren().isEmpty()) {
                btn.setId(Styles.ID_YELLOW_BUTTON);
                packagePane.getChildren().add(btn);
            } else {
                btn.setId(Styles.ID_NORMAL_BUTTON);
                productPane.getChildren().add(btn);
            }
        }
    }

    @Override
    public void initialize(URL arg0, ResourceBundle arg1) {
        if (hbox != null) {
            try {
                mRegister = new Register(dbConnector);
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

    public void killTimers() {
        for (LaneCheckTask task : this.laneCheckers) {
            task.cancel();
        }
        this.laneCheckers.clear();
        timer.cancel();
        timer = null;
        mRegister.killTasks();
    }

    private void onLaneClicked(MouseEvent mouseEvent, int laneID) {
        if (mouseEvent.getButton() == MouseButton.PRIMARY) {
            int oldMin;
            oldMin = minSelected.get();
            boolean select = laneDisplays.get(laneID).selectedProperty().get();
            boolean online = laneDisplays.get(laneID).OnlineProperty().get();
            if (online) {
                System.out.println(laneID + " Clicked!");
                if (!select) {
                    if (oldMin < 0) {//no prev selected
                        minSelected.set(laneID);
                    } else {
                        if (oldMin > laneID) {
                            maxSelected.set(oldMin);
                            minSelected.set(laneID);
                        } else if (oldMin < laneID) {
                            maxSelected.setValue(laneID);
                        }
                    }
                } else { //deselect
                    minSelected.set(laneID);
                    maxSelected.set(-1);
                }
            }
        }
    }

    private void updateLaneSelected() {
        laneNumMin.setText("");
        laneNumMax.setText("");
        int min = minSelected.get();
        int max = maxSelected.get();
        if (min >= 0) {
            laneNumMin.setText(laneDisplays.get(min).getLaneName());
        }
        if (max >= 0) {
            laneNumMax.setText(laneDisplays.get(max).getLaneName());
        }
        for (int i = 0; i < laneDisplays.size(); i++) {
            boolean selected = (i >= min && i <= max) || i == min;
            boolean online = laneDisplays.get(i).OnlineProperty().get();
            if (online) {
                laneDisplays.get(i).selectedProperty().set(selected);
            }
        }

    }

    private void onClearBtn() {
        minSelected.set(-1);
        maxSelected.set(-1);
    }

    private void onAddProductToRegister(ProductUseage pu) {
        mRegister.addProductUseageToRegister(pu);
    }

}
