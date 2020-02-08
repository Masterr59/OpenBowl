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
import javafx.beans.binding.Bindings;
import javafx.event.ActionEvent;
import javafx.scene.control.Dialog;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.control.Spinner;

/**
 *
 * @author Open Bowl <http://www.openbowlscoring.org/>
 */
public class BasicPinSetterOptionsController extends Dialog<Void> implements Initializable {

    @FXML
    private Spinner<String> PowerGPIOSpinner;

    @FXML
    private Spinner<String> PowerStateSpinner;
    
    @FXML
    private Spinner<String> CycleGPIOSpinner;
    
    @FXML
    private Spinner<String> CycleStateSpinner;
    
    @FXML
    private Slider CycleDelaySlider;
    
    @FXML
    private Label CycleLabel;

    private ButtonType okButton;

    public BasicPinSetterOptionsController() throws IOException {
        super();
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/openbowl/scorer/BasicPinsetterOptionsDialog.fxml"));
        loader.setController(this);
        Parent root = loader.load();
        getDialogPane().setContent(root);
        
        

        okButton = new ButtonType("Apply", ButtonData.APPLY);
        ButtonType cancel = new ButtonType("Close", ButtonData.CANCEL_CLOSE);
        getDialogPane().getButtonTypes().addAll(okButton, cancel);

        getDialogPane().lookupButton(okButton).addEventFilter(ActionEvent.ACTION, eh -> onOK(eh));
    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        CycleLabel.textProperty().bind(Bindings.format("%.0f", CycleDelaySlider.valueProperty()));
    }

    private void onOK(ActionEvent eh) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

}
