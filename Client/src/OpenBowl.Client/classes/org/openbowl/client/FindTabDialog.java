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

import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.DialogEvent;
import javafx.scene.control.ListView;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.layout.HBox;
import org.openbowl.common.AuthorizedUser;

/**
 *
 * @author Open Bowl <http://www.openbowlscoring.org/>
 */
public class FindTabDialog extends Alert {

    private final String NONE_SELECTED = "None Selected";
    private final String MSG_TEXT = "Open Tabs";

    private final DatabaseConnector dbConnector;
    private final AuthorizedUser user;
    private final ListView<Integer> listView;
    private final TreeView treeView;
    private Integer tabID;
    private boolean openTab;
    private ButtonType reloadBtn;

    public FindTabDialog(DatabaseConnector dbConnector, AuthorizedUser user) {
        super(AlertType.INFORMATION);
        this.getDialogPane().getStylesheets().add(getClass().getResource("DarkMode.css").toExternalForm());
        this.titleProperty().set(MSG_TEXT);
        this.headerTextProperty().set(MSG_TEXT);
        this.graphicProperty().set(null);
        this.dbConnector = dbConnector;
        this.user = user;
        this.listView = new ListView<>();
        this.listView.setPrefWidth(150);
        this.listView.getSelectionModel().selectedItemProperty().addListener((obs, os, ns) -> onSelectionChange(ns));

        this.treeView = new TreeView();
        this.treeView.setMinWidth(296);
        this.treeView.setId("reciept");
        this.treeView.setRoot(new TreeItem(NONE_SELECTED));

        this.tabID = -1;
        this.openTab = false;
        this.reloadBtn = new ButtonType("Reload");

        this.setOnCloseRequest(eh -> onCloseRequest(eh));

        HBox hbox = new HBox();
        hbox.getChildren().addAll(this.listView, this.treeView);

        this.getDialogPane().setContent(hbox);
        this.getButtonTypes().addAll(ButtonType.CANCEL, reloadBtn);
        onReloadTabs();
    }

    /**
     *
     * @param eh the value of eh
     */
    private void onCloseRequest(DialogEvent eh) {
        if (this.getResult() == ButtonType.OK) {
            this.openTab = true;
            this.tabID = this.listView.getSelectionModel().getSelectedItem();

        } else if (this.getResult() == this.reloadBtn) {
            eh.consume();
            onReloadTabs();
        }
    }

    public Integer getTabID() {
        return tabID;
    }

    public boolean isOpenTab() {
        return openTab;
    }

    private void onReloadTabs() {
        this.listView.getItems().clear();
        this.treeView.setRoot(new Receipt());
        for (Integer I : dbConnector.findTabs(user)) {
            this.listView.getItems().add(I);
        }
    }

    private void onSelectionChange(Integer id) {
        if (id != null) {
            System.out.println("Selection Change");
            this.treeView.setRoot(dbConnector.getTab(user, id).clone());
            this.treeView.getRoot().expandedProperty().set(true);
        }
    }

}
