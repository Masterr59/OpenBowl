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

import java.util.Calendar;
import javafx.scene.control.Alert;


/**
 *
 * @author Open Bowl <http://www.openbowlscoring.org/>
 */
public class AboutOpenBowl {
    public void onAbout(String ApplicationName){
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        int year = Calendar.getInstance().get(Calendar.YEAR);
        String msg = ApplicationName + "\n";
        msg += "Copyright (C) " + year + " Open Bowl <http://www.openbowlscoring.org/>";
        
        alert.setTitle("About");
        alert.setHeaderText(msg);
        alert.showAndWait();
        
    }
}
