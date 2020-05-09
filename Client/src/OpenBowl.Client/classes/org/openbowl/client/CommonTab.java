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

import java.util.HashMap;
import java.util.Map;
import java.util.prefs.Preferences;
import javafx.beans.property.ObjectProperty;
import javafx.geometry.Orientation;
import javafx.scene.control.Label;
import javafx.scene.control.Separator;
import javafx.scene.layout.VBox;
import org.openbowl.common.AuthorizedUser;
import org.openbowl.common.Styles;
import org.openbowl.common.UserRole;

/**
 *
 * @author Open Bowl <http://www.openbowlscoring.org/>
 */
public abstract class CommonTab extends SimpleTab {

    private Label HeaderLabel;
    protected VBox mVBox;
    protected Map<UserRole, Boolean> Permission;
    protected Map<UserRole, String> PermissionStyle;

    protected DatabaseConnector dbConnector;
    protected Preferences mPrefs;

    public CommonTab(ObjectProperty<AuthorizedUser> User, ObjectProperty<AuthorizedUser> Manager, DatabaseConnector db) {

        this.getUser().bindBidirectional(User);
        this.getManager().bindBidirectional(Manager);

        this.getUser().addListener((obs, oldUser, newUser) -> onUserChange(newUser));
        this.getManager().addListener((obs, oldManager, newManager) -> onManagerChange(newManager));

        mPrefs = Preferences.userNodeForPackage(this.getClass());
        
        dbConnector = db;
        mVBox = new VBox();
        HeaderLabel = new Label();
        HeaderLabel.setId(Styles.ID_H2);

        Separator s1 = new Separator();
        s1.setOrientation(Orientation.HORIZONTAL);

        mVBox.getChildren().addAll(HeaderLabel, s1);
        Border.setCenter(mVBox);

        Permission = new HashMap<>();
        PermissionStyle = new HashMap<>();
    }

    protected void setHeaderLabel(String s) {
        HeaderLabel.setText(s);
    }

    protected void onUserChange(AuthorizedUser newUser) {
        for (UserRole ur : UserRole.values()) {
            Permission.put(ur, newUser.isAuthorized(ur));
            PermissionStyle.put(ur, Styles.BorderNone);
        }

    }

    protected void onManagerChange(AuthorizedUser newManager) {

        onUserChange(this.getUser().get());

        for (UserRole ur : UserRole.values()) {
            if (!Permission.get(ur) && newManager.isAuthorized(ur)) {
                Permission.put(ur, true);
                PermissionStyle.put(ur, Styles.ManagerBorder);
            }
        }

    }
    
    protected void onTabSelected(){
        //hook
    }
}
