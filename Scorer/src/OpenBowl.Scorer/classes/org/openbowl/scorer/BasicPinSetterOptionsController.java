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

import com.pi4j.io.gpio.Pin;
import com.pi4j.io.gpio.PinState;
import com.pi4j.io.gpio.RaspiPin;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.ResourceBundle;
import java.util.prefs.Preferences;
import javafx.beans.binding.Bindings;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
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
import javafx.scene.control.SpinnerValueFactory;

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

    private final ButtonType okButton;
    private final String name;
    private final Preferences prefs;

    public BasicPinSetterOptionsController(String name) throws IOException {
        super();
        this.name = name;
        prefs = Preferences.userNodeForPackage(this.getClass());
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
        ArrayList<String> pins = new ArrayList<>();
        ArrayList<String> states = new ArrayList<>();

        Pin[] allPins = RaspiPin.allPins();
        for (Pin p : allPins) {
            pins.add(p.getName());
        }
        Collections.sort(pins);
        PinState[] allStates = PinState.allStates();
        for (PinState p : allStates) {
            states.add(p.getName());
        }
        

        CycleLabel.textProperty().bind(Bindings.format("%.0f", CycleDelaySlider.valueProperty()));
        ObservableList<String> gpioPins = FXCollections.observableArrayList(pins);
        SpinnerValueFactory<String> powerGPIOPinFactory = new SpinnerValueFactory.ListSpinnerValueFactory<>(gpioPins);
        SpinnerValueFactory<String> cycleGPIOPinFactory = new SpinnerValueFactory.ListSpinnerValueFactory<>(gpioPins);

        ObservableList<String> gpioStates = FXCollections.observableArrayList(states);
        SpinnerValueFactory<String> powerGPIOStateFactory = new SpinnerValueFactory.ListSpinnerValueFactory<>(gpioStates);
        SpinnerValueFactory<String> cycleGPIOStateFactory = new SpinnerValueFactory.ListSpinnerValueFactory<>(gpioStates);

        String powerPinName = prefs.get(name + "PowerName", "GPIO 7");
        String powerPinState = prefs.get(name + "PowerState", "HIGH");

        String cyclePinName = prefs.get(name + "cycleName", "GPIO 0");
        String cyclePinState = prefs.get(name + "cycleState", "HIGH");
        powerGPIOStateFactory.setValue(powerPinState);
        powerGPIOPinFactory.setValue(powerPinName);
        
        cycleGPIOStateFactory.setValue(cyclePinState);
        cycleGPIOPinFactory.setValue(cyclePinName);

        PowerGPIOSpinner.setValueFactory(powerGPIOPinFactory);
        PowerStateSpinner.setValueFactory(powerGPIOStateFactory);
        CycleGPIOSpinner.setValueFactory(cycleGPIOPinFactory);
        CycleStateSpinner.setValueFactory(cycleGPIOStateFactory);
        long delay = prefs.getLong(name + "CycleDelay", 100);
        CycleDelaySlider.setValue(delay);
        
    }

    private void onOK(ActionEvent eh) {
        prefs.put(name + "PowerName", PowerGPIOSpinner.getValue());
        prefs.put(name + "PowerState", PowerStateSpinner.getValue());
        prefs.put(name + "cycleName", CycleGPIOSpinner.getValue());
        prefs.put(name + "cycleState", CycleStateSpinner.getValue());
        prefs.putLong(name + "CycleDelay", (long)CycleDelaySlider.getValue());
    }

}
