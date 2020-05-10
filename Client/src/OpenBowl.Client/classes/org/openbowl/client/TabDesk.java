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
import java.util.ResourceBundle;
import javafx.beans.property.ObjectProperty;
import org.openbowl.common.AuthorizedUser;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;

/**
 *
 * @author Open Bowl <http://www.openbowlscoring.org/>
 */
public class TabDesk extends CommonTab implements Initializable{

    private final String HEADER_TEXT = "Front Desk";
    private final String TAB_TEXT = "Desk";
    
    

    public TabDesk(ObjectProperty<AuthorizedUser> User, ObjectProperty<AuthorizedUser> Manager, DatabaseConnector db) throws IOException {
        super(User, Manager, db);
        this.setText(TAB_TEXT);
        this.setHeaderLabel(HEADER_TEXT);
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/openbowl/client/DeskTab.fxml"));
        loader.setController(this);
        Parent root = loader.load();
        
        mVBox.getChildren().add(root);
    }

    protected void onUserChange(AuthorizedUser newUser) {

    }

    protected void onManagerChange(AuthorizedUser newManager) {

    }

    @Override
    public void initialize(URL arg0, ResourceBundle arg1) {
        
    }

}