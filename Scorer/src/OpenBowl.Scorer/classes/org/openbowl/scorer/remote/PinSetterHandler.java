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
package org.openbowl.scorer.remote;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import java.io.IOException;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.openbowl.scorer.PinSetter;
import com.google.gson.Gson;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Iterator;
import org.openbowl.common.AuthorizedUser;

/**
 *
 * @author Open Bowl <http://www.openbowlscoring.org/>
 */
public class PinSetterHandler implements HttpHandler {

    private final PinSetter pinSetter;
    private final int lane;
    private final Gson gson;
    private final List<AuthorizedUser> users;

    public PinSetterHandler(PinSetter pinSetter, int lane) {
        this.pinSetter = pinSetter;
        this.lane = lane;
        gson = new Gson();
        users = new ArrayList();
        Calendar exp = Calendar.getInstance();
        exp.add(Calendar.YEAR, 10);
        users.add(new AuthorizedUser("yZ9Ut95MG3xdf5gc6WgT", exp));
    }
    
    public void addAuthorizedUser(AuthorizedUser u){
        users.add(u);
    }

    @Override
    public void handle(HttpExchange he) throws IOException {
        Map<String, List<String>> headers = he.getRequestHeaders();
        String method = he.getRequestMethod();
        if (isAuthorized(headers)) {

            if (method.equals(Common.GETMETHOD)) {
                Map<String, Object> map = new HashMap<>();
                map.put("Lane", lane);
                map.put("Power", pinSetter.getPowerState());
                map.put("CurentConfig", pinSetter.getConfiguration());
                String resp = gson.toJson(map);
                he.sendResponseHeaders(200, resp.length());
                OutputStream os = he.getResponseBody();
                os.write(resp.getBytes());
                os.close();

            } else if (method.equals(Common.POSTMETHOD)) {
                String response = "This is the POST response";

                he.sendResponseHeaders(200, response.length());
                OutputStream os = he.getResponseBody();
                os.write(response.getBytes());
                os.close();
            } else {
                String response = "Unknown method type;";

                he.sendResponseHeaders(200, response.length());
                OutputStream os = he.getResponseBody();
                os.write(response.getBytes());
                os.close();
            }
        } else {
            //sends 403 Forbidden
            he.sendResponseHeaders(403, 0);
            he.getResponseBody().close();
        }
    }

    private boolean isAuthorized(Map<String, List<String>> headers) {
        Iterator<AuthorizedUser> itr = users.listIterator();
        while (itr.hasNext()) {
            AuthorizedUser u = itr.next();
            if (u.isAuthorized(headers)) {
                return true;
            }
            if (u.isExpired()) {
                itr.remove();
            }
        }
        return false;
    }

}
