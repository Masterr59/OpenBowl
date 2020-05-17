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

import com.google.gson.Gson;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Map;
import java.util.Random;
import org.openbowl.common.AuthorizedUser;
import org.openbowl.common.SystemStatus;
import org.openbowl.common.UserRole;
import org.openbowl.common.WebFunctions;

/**
 *
 * @author Open Bowl <http://www.openbowlscoring.org/>
 */
public class MockDB implements DatabaseConnector {

    private final String ADMIN = "Admin";
    private final String MANAGER = "Manager";
    private final String DESK = "Desk";
    private final String RESTAURANT = "Restaurant";
    private final String NONE = "None";
    private final String DEFAULT_TOKEN = "yZ9Ut95MG3xdf5gc6WgT";
    private final String GET_LANE_STATUS_PATH = "system/get=?status";

    private final Random rand;
    private final Gson gson;

    public MockDB() {
        rand = new Random();
        gson = new Gson();
    }

    @Override
    public AuthorizedUser login(String UserName, String Password) {
        ArrayList<UserRole> roles = new ArrayList<>();
        AuthorizedUser ret = AuthorizedUser.NON_USER;
        Calendar exp = Calendar.getInstance();
        exp.add(Calendar.YEAR, 10);
        switch (UserName) {
            case ADMIN:
                for (UserRole ur : UserRole.values()) {
                    if (ur != UserRole.NONE) {
                        roles.add(ur);
                    }
                }
                ret = new AuthorizedUser(DEFAULT_TOKEN, exp, 99, UserName, roles);
                break;
            case MANAGER:
                roles.add(UserRole.GENERATE_REPORTS);
                roles.add(UserRole.TRANSACTION_ADD);
                roles.add(UserRole.TRANSACTION_DELETE);
                roles.add(UserRole.GAME_ADMIN);
                ret = new AuthorizedUser(DEFAULT_TOKEN, exp, 99, UserName, roles);
                break;
            case DESK:
                roles.add(UserRole.TRANSACTION_ADD);
                roles.add(UserRole.GAME_ADMIN);
                ret = new AuthorizedUser(DEFAULT_TOKEN, exp, 99, UserName, roles);
                break;
            case RESTAURANT:
                roles.add(UserRole.TRANSACTION_ADD);
                ret = new AuthorizedUser(DEFAULT_TOKEN, exp, 99, UserName, roles);
                break;
            case NONE:
                roles.add(UserRole.NONE);
                ret = new AuthorizedUser(DEFAULT_TOKEN, exp, 99, UserName, roles);
                break;
        }

        return ret;

    }

    @Override
    public String updateUserPassword(AuthorizedUser user, String oldPassword, String newPassword) {
        String ret = "MockDB Connector: updating password for user %s ... random %s";
        String status = rand.nextBoolean() ? "Success" : "Failure";

        return String.format(ret, user.getUsername(), status);
    }

    @Override
    public int getNumLanes(AuthorizedUser user) {
        if (user.isAuthorized(UserRole.GAME_ADMIN)) {
            return 4;
        }
        return 0;
    }

    @Override
    public boolean isLaneOnline(int lane) {
        if (lane < 2) {
            try {
                String Response = WebFunctions.doHttpGetRequest("127.0.0.1", GET_LANE_STATUS_PATH, DEFAULT_TOKEN);
                Map<String, ArrayList<SystemStatus>> status = gson.fromJson(Response, Map.class);
                if (status.containsKey("status")) {
                    ArrayList<SystemStatus> laneStatus = status.get("status");
                    return laneStatus.contains(SystemStatus.ONLINE);
                }
                return false;
            } catch (IOException | InterruptedException ex) {
                return false;
            }
        } else {
            return false;
        }
    }

    @Override
    public ArrayList<SystemStatus> getLaneStatus(int lane) {
        ArrayList<SystemStatus> status = new ArrayList<>();
        try {
            String Response = WebFunctions.doHttpGetRequest("127.0.0.1", GET_LANE_STATUS_PATH, DEFAULT_TOKEN);
            Map<String, ArrayList<SystemStatus>> statusJSON = gson.fromJson(Response, Map.class);
            if (statusJSON.containsKey("status")) {
                ArrayList<SystemStatus> laneStatus = statusJSON.get("status");
                return laneStatus;
            }

        } catch (IOException | InterruptedException ex) {
            System.out.println("Error getting lane status - " + ex.toString());
        }
        return status;
    }

}
