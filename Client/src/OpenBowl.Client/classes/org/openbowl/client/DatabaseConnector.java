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

import java.util.ArrayList;
import java.util.Map;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import org.openbowl.common.AuthorizedUser;
import org.openbowl.common.SimplePlayer;
import org.openbowl.common.SystemStatus;

/**
 *
 * @author Open Bowl <http://www.openbowlscoring.org/>
 */
public abstract class DatabaseConnector {

    public static final String GAME_DEPARTMENT_NAME = "Game";

    protected EventHandler<ActionEvent> onLaneActivated;

    /**
     *
     * @param UserName
     * @param Password
     * @return An AuthorizedUser object if login was successful otherwise
     * AuthorizedUser.NON_USER
     */
    public abstract AuthorizedUser login(String UserName, String Password);

    /**
     *
     * @param user
     * @param oldPassword
     * @param newPassword
     * @return Human readable string stating the status up the update
     */
    public abstract String updateUserPassword(AuthorizedUser user, String oldPassword, String newPassword);

    public abstract int getNumLanes(AuthorizedUser user);

    public abstract boolean isLaneOnline(int lane);

    public abstract ArrayList<SystemStatus> getLaneStatus(int lane);

    public abstract Map<Integer, String> getDepartments(AuthorizedUser user);

    public abstract Map<Integer, String> getSubDepartments(AuthorizedUser user, Integer DepartmentID);

    public abstract ArrayList<ProductUseage> getProducts(AuthorizedUser user, Integer SubDepartment);

    public abstract Integer saveTab(AuthorizedUser user, Receipt root);

    public abstract ArrayList<Integer> findTabs(AuthorizedUser user);

    public abstract Receipt getTab(AuthorizedUser user, Integer tabid);

    public abstract boolean removeTab(AuthorizedUser user, Integer tabid);

    public abstract Integer saveTransaction(AuthorizedUser user, Receipt root);

    public abstract ArrayList<PaymentType> getPaymentTypes(AuthorizedUser user);

    public void setOnLaneActivated(EventHandler<ActionEvent> onLaneActivated) {
        this.onLaneActivated = onLaneActivated;
    }

    public abstract void cycleLane(AuthorizedUser user, int laneID);

    public abstract void pauseResumeAbortSession(AuthorizedUser user, String type, int laneID);

    public abstract String getCurrentSession(int laneID);

    public abstract void configurationDialog();

    public abstract ArrayList<SimplePlayer> getPlayers(AuthorizedUser user);

    public abstract void addPlayer(AuthorizedUser user, int laneID, SimplePlayer player);

    public abstract Map<String, Object> getLaneConfig(AuthorizedUser user, int laneID, String type);

    public abstract void setLaneConfig(AuthorizedUser user, int laneID, String type, Map<String, Object> config);

    public abstract byte[] getLastImage(AuthorizedUser user, int laneID);
    
    public abstract void displayMaint(AuthorizedUser user, int laneID, String type);
}
