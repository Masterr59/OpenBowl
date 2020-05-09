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
import javafx.scene.web.WebView;
import org.openbowl.common.AuthorizedUser;

/**
 *
 * @author Open Bowl <http://www.openbowlscoring.org/>
 */
public class TabBowlers extends CommonTab {

    private final String WEB_PATH = "/bowlers.html";

    private final String HEADER_TEXT = "Bowlers";
    private final String TAB_TEXT = "Bowlers";
    private WebView webView;

    public TabBowlers(ObjectProperty<AuthorizedUser> User, ObjectProperty<AuthorizedUser> Manager, DatabaseConnector db) {
        super(User, Manager, db);
        this.setText(TAB_TEXT);
        this.setHeaderLabel(HEADER_TEXT);

        webView = new WebView();
        webView.prefHeightProperty().bind(mVBox.heightProperty());
        mVBox.getChildren().add(webView);
    }

    protected void onUserChange(AuthorizedUser newUser) {

    }

    protected void onManagerChange(AuthorizedUser newManager) {

    }

    @Override
    protected void onTabSelected() {
        String url = mPrefs.get(MainApp.PREFS_WEB_CLIENT_SITE, MainApp.DEFAULT_WEB_CLIENT_SITE);
        url += WEB_PATH;
        webView.getEngine().load(url);
    }

}
