/*
 * Copyright (C) 2020 patrick
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

import java.util.UUID;

/**
 *
 * @author patrick
 */
public class SimplePlayer {

    private String name;
    private String uuid;
    private int hdcp;

    public SimplePlayer(String name) {
        this(name, UUID.randomUUID().toString(), 0);
    }

    public SimplePlayer(String name, String uuid, int hdcp) {
        this.name = name;
        this.uuid = uuid;
        this.hdcp = hdcp;
    }

    public String getName() {
        return name;
    }

    public String getUuid() {
        return uuid;
    }

    public int getHdcp() {
        return hdcp;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public void setHdcp(int hdcp) {
        this.hdcp = hdcp;
    }

    @Override
    public String toString() {
        return name;
    }

}
