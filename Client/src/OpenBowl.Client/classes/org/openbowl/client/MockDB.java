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
import org.openbowl.common.AuthorizedUser;
import org.openbowl.common.UserRole;

/**
 *
 * @author Open Bowl <http://www.openbowlscoring.org/>
 */
public class MockDB implements DatabaseConnector {
    private final String DEFAULT_USERNAME = "OpenBowl";
    private final String DEFAULT_PASSWORD = "Password4OpenBowl";
    private final String DEFAULT_TOKEN = "yZ9Ut95MG3xdf5gc6WgT";

    @Override
    public AuthorizedUser login(String UserName, String Password) {
        if (UserName.equals(DEFAULT_USERNAME) && Password.equals(DEFAULT_PASSWORD)) {
            ArrayList<UserRole> roles = new ArrayList<>();
            for (UserRole ur : UserRole.values()) {
                if (ur != UserRole.NONE) {
                    roles.add(ur);
                }
            }
            Calendar exp = Calendar.getInstance();
            exp.add(Calendar.YEAR, 10);
            AuthorizedUser ret = new AuthorizedUser(DEFAULT_TOKEN, exp, 99, DEFAULT_USERNAME, roles);
            return ret;
        } else {
            return null;
        }
    }

}
