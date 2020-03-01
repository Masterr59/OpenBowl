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
import javafx.beans.binding.Bindings;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.control.TextField;

/**
 *
 * @author Open Bowl <http://www.openbowlscoring.org/>
 */
public class LaneOptionsDialogController extends Dialog<Void> implements Initializable {
    
    public final String DISPLAY_SETTING_NAME = "DisplayAddress";
    public final String DISPLAY_SETTING_VALUE = "127.0.0.1";

    @FXML
    private Label ballSweepLabel;
    @FXML
    private Label slowBallLabel;
    @FXML
    private Label foulLabel;
    @FXML
    private Label pinCounterCycleLabel;

    @FXML
    private Button pinSetterButton;
    @FXML
    private Button pinCounterButton;
    @FXML
    private Button sweepButton;
    @FXML
    private Button foulButton;
    @FXML
    private Button ballButton;
    @FXML
    private Button displayButton;

    @FXML
    private Slider ballSweepSlider;
    @FXML
    private Slider slowBallSlider;
    @FXML
    private Slider foulBallSlider;
    @FXML
    private Slider pinCounterCycleSlider;
    
    private final ButtonType okButton;
    private Lane lane;
    private String name;
    private Preferences prefs;

    public LaneOptionsDialogController(Lane lane, String n) throws IOException {
        this.lane = lane;
        this.name = n;
        prefs = Preferences.userNodeForPackage(this.getClass());

        FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/openbowl/scorer/LaneOptionsDialog.fxml"));
        loader.setController(this);
        Parent root = loader.load();
        getDialogPane().setContent(root);
        setTitle("Lane " + n + " options");
              
        
        okButton = new ButtonType("Apply", ButtonBar.ButtonData.APPLY);
        ButtonType cancel = new ButtonType("Close", ButtonBar.ButtonData.CANCEL_CLOSE);
        getDialogPane().getButtonTypes().addAll(okButton, cancel);

        getDialogPane().lookupButton(okButton).addEventFilter(ActionEvent.ACTION, eh -> onOK(eh));
        
        pinSetterButton.setOnAction(notUsed -> lane.getPinSetter().configureDialog());
        pinCounterButton.setOnAction(notUsed -> lane.getPinCounter().configureDialog());
        sweepButton.setOnAction(notUsed -> lane.getSweep().configureDialog());
        foulButton.setOnAction(notUsed -> lane.getFoul().configureDialog());
        ballButton.setOnAction(notUsed -> lane.getBall().configureDialog());
        displayButton.setOnAction(notUsed -> lane.getDisplay().configureDialog());
        
    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        ballSweepLabel.textProperty().bind(Bindings.format("%.2f in", ballSweepSlider.valueProperty()));
        slowBallLabel.textProperty().bind(Bindings.format("%,.0f ms", slowBallSlider.valueProperty()));
        foulLabel.textProperty().bind(Bindings.format("%,.0f ms", foulBallSlider.valueProperty()));
        pinCounterCycleLabel.textProperty().bind(Bindings.format("%,.0f ms", pinCounterCycleSlider.valueProperty()));
        
        ballSweepSlider.setValue(prefs.getDouble(name + lane.BALL_SWEEP_DISTANCE_SETTING, lane.FOUL_SWEEP_DISTANCE_DEFAULT));
        slowBallSlider.setValue(prefs.getLong(name + lane.SLOW_BALL_THRESHOLD_SETTING, lane.SLOW_BALL_THRESHOLD_DEFAULT));
        foulBallSlider.setValue(prefs.getLong(name + lane.FOUL_BALL_THRESHOLD_SETTING, lane.FOUL_BALL_THRESHOLD_DEFAULT));
        pinCounterCycleSlider.setValue(prefs.getLong(name + lane.PIN_COUNTER_DELAY_SETTING, lane.PIN_COUNTER_DELAY_DEFAULT));

    }

    private void onOK(ActionEvent eh) {
        prefs.putDouble(name + lane.BALL_SWEEP_DISTANCE_SETTING, ballSweepSlider.getValue());
        prefs.putLong(name + lane.SLOW_BALL_THRESHOLD_SETTING, new Double(slowBallSlider.getValue()).longValue());
        prefs.putLong(name + lane.FOUL_BALL_THRESHOLD_SETTING, new Double(foulBallSlider.getValue()).longValue());
        prefs.putLong(name + lane.PIN_COUNTER_DELAY_SETTING, new Double(pinCounterCycleSlider.getValue()).longValue());

    }

}
