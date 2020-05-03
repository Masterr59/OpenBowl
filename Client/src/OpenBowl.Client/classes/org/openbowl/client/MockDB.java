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
import java.util.Calendar;
import java.util.Random;
import org.openbowl.common.AuthorizedUser;
import org.openbowl.common.UserRole;

/**
 *
 * @author Open Bowl <http://www.openbowlscoring.org/>
 */
public class MockDB implements DatabaseConnector {

    private final String ADMIN = "Admin";
    private final String MANAGER = "Manager";
    private final String DESK = "Desk";
    private final String RESTAURANT = "Restaurant";
    private final String DEFAULT_TOKEN = "yZ9Ut95MG3xdf5gc6WgT";

    private final Random rand;

    public MockDB() {
        rand = new Random();
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
                roles.add(UserRole.SCORE_MACHINE);
                ret = new AuthorizedUser(DEFAULT_TOKEN, exp, 99, UserName, roles);
                break;
            case DESK:
                roles.add(UserRole.TRANSACTION_ADD);
                roles.add(UserRole.SCORE_MACHINE);
                ret = new AuthorizedUser(DEFAULT_TOKEN, exp, 99, UserName, roles);
                break;
            case RESTAURANT:
                roles.add(UserRole.TRANSACTION_ADD);
                ret = new AuthorizedUser(DEFAULT_TOKEN, exp, 99, UserName, roles);
                break;
        }

        return ret;

    }

    @Override
    public String updateUserPassword(AuthorizedUser user, String oldPassword, String newPassword) {
        String ret = "MockDB Connector: updating password for user %s ... random %s";
        String status = rand.nextBoolean()? "Success" : "Failure";

        return String.format(ret, user.getUsername(), status);
    }

}
