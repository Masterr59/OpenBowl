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
package org.openbowl.scorer;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.prefs.Preferences;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.TextField;

/**
 *
 * @author Open Bowl <http://www.openbowlscoring.org/>
 */
public class DisplayConnectorDialogController extends Dialog<Void> implements Initializable {

    @FXML
    private TextField address;
    @FXML
    private TextField endpoint;

    private String name;
    private DisplayConnector display;
    private Preferences prefs;
    private ButtonType okButton;

    public DisplayConnectorDialogController(String name, DisplayConnector display) throws IOException {
        this.name = name;
        this.display = display;
        prefs = Preferences.userNodeForPackage(this.getClass());

        FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/openbowl/scorer/DisplayConnectorDialog.fxml"));
        loader.setController(this);
        Parent root = loader.load();
        getDialogPane().setContent(root);
        setTitle("Lane " + name + " display options");

        okButton = new ButtonType("Apply", ButtonBar.ButtonData.APPLY);
        ButtonType cancel = new ButtonType("Close", ButtonBar.ButtonData.CANCEL_CLOSE);
        getDialogPane().getButtonTypes().addAll(okButton, cancel);
        getDialogPane().lookupButton(okButton).addEventFilter(ActionEvent.ACTION, eh -> onOK(eh));
    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        address.setText(prefs.get(name + display.DISPLAY_ADDRESS_NAME, display.DISPLAY_ADDRESS_VALUE));
        endpoint.setText(prefs.get(name + display.DISPLAY_ENDPOINT_NAME, display.DISPLAY_ENDPOINT_VALUE));
    }

    private void onOK(ActionEvent eh) {
        prefs.put(name + display.DISPLAY_ADDRESS_NAME, address.getText());
        prefs.put(name + display.DISPLAY_ENDPOINT_NAME, endpoint.getText());
    }

}
