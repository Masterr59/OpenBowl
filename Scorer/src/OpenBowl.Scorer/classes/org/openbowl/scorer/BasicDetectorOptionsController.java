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
import java.util.Arrays;
import java.util.Collections;
import java.util.ResourceBundle;
import java.util.prefs.Preferences;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory;

/**
 *
 * @author Open Bowl <http://www.openbowlscoring.org/>
 */
public class BasicDetectorOptionsController extends Dialog<Void> implements Initializable {
    @FXML
    private Spinner<String> pinNumberSpinner;

    @FXML
    private Spinner<String> pullDirectionSpinner;

    @FXML
    private Spinner<String> triggerStateSpinner;

    @FXML
    private Label ErrorLabel;

    private final ButtonType okButton;
    private final String name;
    private final Preferences prefs;
    private BasicDetector detector;

    public BasicDetectorOptionsController(String name, BasicDetector d) throws IOException {
        super();
        this.name = name;
        this.detector = d;

        prefs = Preferences.userNodeForPackage(this.getClass());
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/openbowl/scorer/BasicDetectorOptionsDialog.fxml"));
        loader.setController(this);
        Parent root = loader.load();

        getDialogPane().setContent(root);

        okButton = new ButtonType("Apply", ButtonBar.ButtonData.APPLY);
        ButtonType cancel = new ButtonType("Close", ButtonBar.ButtonData.CANCEL_CLOSE);
        getDialogPane().getButtonTypes().addAll(okButton, cancel);

        getDialogPane().lookupButton(okButton).addEventFilter(ActionEvent.ACTION, eh -> onOK(eh));
    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        ArrayList<String> pins = new ArrayList<>();
        ArrayList<String> states = new ArrayList<>();
        ArrayList<String> resistances = new ArrayList<>();

        Pin[] allPins = RaspiPin.allPins();
        for (Pin p : allPins) {
            pins.add(p.getName());
        }
        Collections.sort(pins);
        PinState[] allStates = PinState.allStates();
        for (PinState p : allStates) {
            states.add(p.getName());
        }
        String[] allResitance = {"OFF", "PULL_DOWN", "PULL_UP"};
        resistances.addAll(Arrays.asList(allResitance));

        ObservableList<String> gpioPins = FXCollections.observableArrayList(pins);
        SpinnerValueFactory<String> GPIOPinFactory = new SpinnerValueFactory.ListSpinnerValueFactory<>(gpioPins);

        ObservableList<String> gpioStates = FXCollections.observableArrayList(states);
        SpinnerValueFactory<String> GPIOStateFactory = new SpinnerValueFactory.ListSpinnerValueFactory<>(gpioStates);

        ObservableList<String> gpioRes = FXCollections.observableArrayList(resistances);
        SpinnerValueFactory<String> GPIOResFactory = new SpinnerValueFactory.ListSpinnerValueFactory<>(gpioRes);

        String PinName = prefs.get(name + detector.PIN_SETTING, detector.DEFAULT_PIN);
        String PinState = prefs.get(name + detector.TRIGGER_SETTING, detector.DEFAULT_TRIGGER);
        String PinDirection = prefs.get(name + detector.RESISTANCE_SETTING, detector.DEFAULT_RESISTANCE);

        GPIOStateFactory.setValue(PinState);
        GPIOPinFactory.setValue(PinName);
        GPIOResFactory.setValue(PinDirection);

        pinNumberSpinner.setValueFactory(GPIOPinFactory);
        triggerStateSpinner.setValueFactory(GPIOStateFactory);
        pullDirectionSpinner.setValueFactory(GPIOResFactory);

    }

    private void onOK(ActionEvent eh) {
        detector.teardown();
        prefs.put(name + detector.PIN_SETTING, pinNumberSpinner.getValue());
        prefs.put(name + detector.TRIGGER_SETTING, triggerStateSpinner.getValue());
        prefs.put(name + detector.RESISTANCE_SETTING, pullDirectionSpinner.getValue());
        String results = detector.setup();
        if (!results.isBlank()) {
            ErrorLabel.setText(results);
            eh.consume();
        }
    }

}
