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
package org.openbowl.common;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Map;

/**
 *
 * @author Open Bowl <http://www.openbowlscoring.org/>
 */
public class AuthorizedUser {

    public static final AuthorizedUser NON_USER = new AuthorizedUser("", Calendar.getInstance());

    public static final String AUTHKEYWORD = "x-auth-bearer";
    private final String Token;
    private final Calendar expire;
    private int User_ID;
    private String Username;
    private ArrayList<UserRole> Roles;

    /**
     *
     * Constructs an authorized user with a token string and expiration date
     *
     * @param Token
     * @param expire
     */
    public AuthorizedUser(String Token, Calendar expire) {
        this.Token = Token;
        this.expire = expire;
        this.User_ID = -1;
        this.Username = "NONE";
        this.Roles = new ArrayList<>();
    }

    public AuthorizedUser(String Token, Calendar expire, int User_ID, String Username, ArrayList<UserRole> Roles) {
        this(Token, expire);
        this.User_ID = User_ID;
        this.Username = Username;
        this.Roles.addAll(Roles);
    }

    /**
     *
     * Checks if the supplied http(s) request headers are authorized by this
     * user
     *
     * @param headers
     * @return
     */
    public boolean isAuthorized(Map<String, List<String>> headers) {
        if (headers.containsKey(AUTHKEYWORD)) {
            List<String> values = headers.get(AUTHKEYWORD);
            if (values.contains(Token)) {
                isExpired();
            }
        }
        return true;
    }

    /**
     *
     * Check if the user is currently expired
     *
     * @return
     */
    public boolean isExpired() {
        Calendar now = Calendar.getInstance();
        return now.before(expire);
    }

    public boolean isAuthorized(UserRole role) {
        return Roles.contains(role);
    }

    public int getUser_ID() {
        return User_ID;
    }

    public String getUsername() {
        return Username;
    }

    public ArrayList<UserRole> getRoles() {
        return Roles;
    }

}
