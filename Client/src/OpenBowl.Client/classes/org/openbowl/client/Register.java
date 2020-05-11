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

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.layout.Pane;

/**
 *
 * @author Open Bowl <http://www.openbowlscoring.org/>
 */
public class Register extends Pane implements Initializable{

    public Register() throws IOException {
        
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/openbowl/client/Register.fxml"));
        loader.setController(this);
        Parent root = loader.load();

        getChildren().add(root);
    }
    
    

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        
    }
    
}
