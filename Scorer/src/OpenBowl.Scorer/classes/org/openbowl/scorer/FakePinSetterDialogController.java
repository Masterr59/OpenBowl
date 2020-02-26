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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import org.openbowl.common.BowlingPins;

/**
 *
 * @author Open Bowl <http://www.openbowlscoring.org/>
 */
public class FakePinSetterDialogController extends Dialog<Void> implements Initializable, PinCounter {

    @FXML
    private Label BallOne;
    @FXML
    private Label BallTwo;
    @FXML
    private Button Strike;
    @FXML
    private CheckBox Pin1;
    @FXML
    private CheckBox Pin2;
    @FXML
    private CheckBox Pin3;
    @FXML
    private CheckBox Pin4;
    @FXML
    private CheckBox Pin5;
    @FXML
    private CheckBox Pin6;
    @FXML
    private CheckBox Pin7;
    @FXML
    private CheckBox Pin8;
    @FXML
    private CheckBox Pin9;
    @FXML
    private CheckBox Pin10;
    @FXML
    private Line lineTop;
    @FXML
    private Line lineLeft;
    @FXML
    private Line lineRight;
    @FXML
    private Line lineBottom;

    private String name;
    private FakePinSetter pinsetter;

    FakePinSetterDialogController(String n, FakePinSetter p) throws IOException {
        this.name = n;
        this.pinsetter = p;
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/openbowl/scorer/FakePinSetterDialog.fxml"));
        loader.setController(this);
        Parent root = loader.load();
        getDialogPane().setContent(root);
        setTitle("Fake " + n);
        setBall(0);
        resetPins();
        Strike.setOnAction(notUsed -> onStrike());
    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {

    }

    public void resetPins() {
        Pin1.setSelected(true);
        Pin2.setSelected(true);
        Pin3.setSelected(true);
        Pin4.setSelected(true);
        Pin5.setSelected(true);
        Pin6.setSelected(true);
        Pin7.setSelected(true);
        Pin8.setSelected(true);
        Pin9.setSelected(true);
        Pin10.setSelected(true);
    }

    public void onStrike() {
        Pin1.setSelected(false);
        Pin2.setSelected(false);
        Pin3.setSelected(false);
        Pin4.setSelected(false);
        Pin5.setSelected(false);
        Pin6.setSelected(false);
        Pin7.setSelected(false);
        Pin8.setSelected(false);
        Pin9.setSelected(false);
        Pin10.setSelected(false);
    }

    public void setBall(int ball) {
        log("Ball: " + Integer.toString(ball));
        switch (ball) {
            case 0:
                resetPins();
                BallOne.setText(" ");
                BallTwo.setText(" ");
                break;
            case 1:
                BallOne.setText("O");
                BallTwo.setText(" ");
                break;
            case 2:
                BallOne.setText("O");
                BallTwo.setText("O ");
                break;
        }
    }

    public void setPower(boolean power) {
        if (power) {
            lineTop.setStroke(Color.GOLDENROD);
            lineBottom.setStroke(Color.GOLDENROD);
            lineLeft.setStroke(Color.GOLDENROD);
            lineRight.setStroke(Color.GOLDENROD);
        } else {
            lineTop.setStroke(Color.BLACK);
            lineBottom.setStroke(Color.BLACK);
            lineLeft.setStroke(Color.BLACK);
            lineRight.setStroke(Color.BLACK);
        }
    }

    @Override
    public void configureDialog() {
        log("show configuration dialog");
    }

    @Override
    public String setConfiguration(Map<String, Object> configuration) {
        log("set configuration");
        return "";
    }

    @Override
    public Map<String, Object> getConfiguration() {
        Map<String, Object> map = new HashMap<>();
        map.put("Type", this.getClass().getName());
        log("get configuration");
        return map;
    }

    @Override
    public ArrayList<BowlingPins> countPins() {
        ArrayList<BowlingPins> pins = new ArrayList<>();
        if (Pin1.isSelected()) {
            pins.add(BowlingPins.PIN_01);
        }
        if (Pin2.isSelected()) {
            pins.add(BowlingPins.PIN_02);
        }
        if (Pin3.isSelected()) {
            pins.add(BowlingPins.PIN_03);
        }
        if (Pin4.isSelected()) {
            pins.add(BowlingPins.PIN_04);
        }
        if (Pin5.isSelected()) {
            pins.add(BowlingPins.PIN_05);
        }
        if (Pin6.isSelected()) {
            pins.add(BowlingPins.PIN_06);
        }
        if (Pin7.isSelected()) {
            pins.add(BowlingPins.PIN_07);
        }
        if (Pin8.isSelected()) {
            pins.add(BowlingPins.PIN_08);
        }
        if (Pin9.isSelected()) {
            pins.add(BowlingPins.PIN_09);
        }
        if (Pin10.isSelected()) {
            pins.add(BowlingPins.PIN_10);
        }

        return pins;
    }

    @Override
    public String setup() {
        log("setup");
        return "";
    }

    @Override
    public void teardown() {
        log("teardown");

    }

    @Override
    public void log(String s) {
        PinCounter.super.log(name + " " + s);
    }

}
