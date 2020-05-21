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

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.TimerTask;
import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

/**
 *
 * @author patrick
 */
public class ClockUpdateTask extends TimerTask {

    private StringProperty dateLabel;
    private DateTimeFormatter dtf;

    public ClockUpdateTask() {
        dateLabel = new SimpleStringProperty();
        dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");
    }

    public StringProperty DateLabelProperty() {
        return dateLabel;
    }

    @Override
    public void run() {
        LocalDateTime now = LocalDateTime.now();
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                dateLabel.set(dtf.format(now));
            }
        });

    }

}
