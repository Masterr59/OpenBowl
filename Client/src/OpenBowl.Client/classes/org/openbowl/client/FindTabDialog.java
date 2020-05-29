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
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.DialogEvent;
import javafx.scene.control.ListView;
import javafx.scene.control.TreeView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import org.openbowl.common.AuthorizedUser;

/**
 *
 * @author Open Bowl <http://www.openbowlscoring.org/>
 */
public class FindTabDialog extends Alert {

    private final DatabaseConnector dbConnector;
    private final AuthorizedUser user;
    private final ListView listView;
    private final TreeView treeView;


    public FindTabDialog(DatabaseConnector dbConnector, AuthorizedUser user) {
        super(AlertType.INFORMATION);
        this.dbConnector = dbConnector;
        this.user = user;
        this.listView = new ListView();
        this.treeView = new TreeView();
        this.setOnCloseRequest(eh -> onCancel(eh));

        HBox hbox = new HBox();
        hbox.getChildren().addAll(this.listView, this.treeView);

        this.getDialogPane().setContent(hbox);
        this.getButtonTypes().addAll(ButtonType.CANCEL);
    }

    /**
     *
     * @param eh the value of eh
     */
    private void onCancel(DialogEvent eh) {
        System.out.println("On Cancel");
        if(this.getResult() == ButtonType.OK){
            System.out.println("ok");
            eh.consume();
            
        }
    }

}
