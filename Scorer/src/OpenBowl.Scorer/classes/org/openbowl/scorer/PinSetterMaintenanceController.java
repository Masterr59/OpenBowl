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
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;

/**
 *
 * @author Open Bowl <http://www.openbowlscoring.org/>
 */
public class PinSetterMaintenanceController extends Dialog<Void> implements Initializable {

    @FXML
    private Label PowerLabel;

    @FXML
    private Button PowerButton;

    @FXML
    private Button CycleButton;

    private final PinSetter pinSetter;
    private final String name;

    public PinSetterMaintenanceController(PinSetter pinSetter, String name) throws IOException {
        super();
        this.pinSetter = pinSetter;
        this.name = name;

        FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/openbowl/scorer/PinSetterMaintenanceDialog.fxml"));
        loader.setController(this);
        Parent root = loader.load();

        ButtonType cancel = new ButtonType("Close", ButtonBar.ButtonData.CANCEL_CLOSE);
        getDialogPane().getButtonTypes().addAll(cancel);
        getDialogPane().setContent(root);
    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        PowerButton.setOnAction(notUsed -> onPower());
        CycleButton.setOnAction(notUsed -> pinSetter.cycle());
        setLabel();
    }

    private void onPower() {
        boolean currentPowerState = pinSetter.getPowerState();
        pinSetter.setPower(!currentPowerState);
        setLabel();
    }

    private void setLabel() {
        String msg = name + " is ";

        boolean currentPowerState = pinSetter.getPowerState();
        msg += currentPowerState ? "on" : "off";
        PowerLabel.setText(msg);

    }

}
