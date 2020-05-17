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
package org.openbowl.client;

import java.util.ArrayList;
import java.util.TimerTask;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import org.openbowl.common.SystemStatus;

/**
 *
 * @author patrick
 */
public class LaneCheckTask extends TimerTask {

    private DatabaseConnector dbConnector;
    private int lane;

    private BooleanProperty onlineProperty;
    private BooleanProperty updateProperty;
    private BooleanProperty heatProperty;
    private BooleanProperty voltProperty;
    private BooleanProperty rebootProperty;
    private BooleanProperty crashProperty;
    private BooleanProperty gameStatusProperty;

    public LaneCheckTask(DatabaseConnector dbConnector, int lane) {
        this.dbConnector = dbConnector;
        this.lane = lane;
        this.onlineProperty = new SimpleBooleanProperty(false);
        this.updateProperty = new SimpleBooleanProperty(false);
        this.heatProperty = new SimpleBooleanProperty(false);
        this.voltProperty = new SimpleBooleanProperty(false);
        this.rebootProperty = new SimpleBooleanProperty(false);
        this.crashProperty = new SimpleBooleanProperty(false);
        this.gameStatusProperty = new SimpleBooleanProperty(false);
    }

    @Override
    public void run() {
        if (onlineProperty.get()) {
            ArrayList<SystemStatus> status = dbConnector.getLaneStatus(lane);
            boolean online = status.contains(SystemStatus.ONLINE);
            boolean update = status.contains(SystemStatus.UPDATE_AVAILABLE);
            boolean heat = status.contains(SystemStatus.OVERHEAT);
            boolean volt = status.contains(SystemStatus.UNDERVOLT);
            boolean reboot = status.contains(SystemStatus.REBOOT_REQUIRED);
            boolean crash = status.contains(SystemStatus.CRASH_DETECTED);
            boolean gameS = (lane % 2 == 0) ? status.contains(SystemStatus.ODD_GAME_RUNNING) : status.contains(SystemStatus.EVEN_GAME_RUNNING);
            
            this.onlineProperty.set(online);
            this.updateProperty.set(update);
            this.heatProperty.set(heat);
            this.voltProperty.set(volt);
            this.rebootProperty.set(reboot);
            this.crashProperty.set(crash);
            this.gameStatusProperty.set(gameS);

        } else {
            onlineProperty.set(dbConnector.isLaneOnline(lane));
            System.out.println("Lane " + lane + " was offline now " + onlineProperty.toString());
        }

    }

    public BooleanProperty OnlineProperty() {
        return onlineProperty;
    }

    public BooleanProperty UpdateProperty() {
        return updateProperty;
    }

    public BooleanProperty HeatProperty() {
        return heatProperty;
    }

    public BooleanProperty VoltProperty() {
        return voltProperty;
    }

    public BooleanProperty RebootProperty() {
        return rebootProperty;
    }

    public BooleanProperty CrashProperty() {
        return crashProperty;
    }

    public BooleanProperty GameStatusProperty() {
        return gameStatusProperty;
    }

}
