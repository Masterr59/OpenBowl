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
import org.openbowl.common.AuthorizedUser;
import org.openbowl.common.SystemStatus;

/**
 *
 * @author Open Bowl <http://www.openbowlscoring.org/>
 */
public interface DatabaseConnector {
    public static final String GAME_DEPARTMENT_NAME = "Game";

    /**
     *
     * @param UserName
     * @param Password
     * @return An AuthorizedUser object if login was successful otherwise
     * AuthorizedUser.NON_USER
     */
    public AuthorizedUser login(String UserName, String Password);

    /**
     *
     * @param user
     * @param oldPassword
     * @param newPassword
     * @return Human readable string stating the status up the update
     */
    public String updateUserPassword(AuthorizedUser user, String oldPassword, String newPassword);

    public int getNumLanes(AuthorizedUser user);

    public boolean isLaneOnline(int lane);
    
    public ArrayList<SystemStatus> getLaneStatus(int lane);
    
    public Map<Integer, String> getDepartments(AuthorizedUser user);
    
    public Map<Integer, String> getSubDepartments(AuthorizedUser user, Integer DepartmentID);
    
    public ArrayList<ProductUseage> getProducts(AuthorizedUser user, Integer SubDepartment);
}
