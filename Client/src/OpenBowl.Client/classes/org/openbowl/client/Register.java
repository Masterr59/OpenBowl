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
import java.util.Timer;
import javafx.beans.binding.Bindings;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TreeView;
import javafx.scene.layout.Pane;

/**
 *
 * @author Open Bowl <http://www.openbowlscoring.org/>
 */
public class Register extends Pane implements Initializable {

    private enum NumPadKeys {
        KEY_0,
        KEY_1,
        KEY_2,
        KEY_3,
        KEY_4,
        KEY_5,
        KEY_6,
        KEY_7,
        KEY_8,
        KEY_9,
        KEY_DOT,
        KEY_BACKSPACE,
    }

    @FXML
    Label dateTime;

    @FXML
    TreeView recieptView;

    @FXML
    Label salesTax;

    @FXML
    Label salesTotal;

    @FXML
    Button adjustBtn;

    @FXML
    Button specialBtn;

    @FXML
    Button noSaleBtn;

    @FXML
    Button num0;
    @FXML
    Button num1;
    @FXML
    Button num2;
    @FXML
    Button num3;
    @FXML
    Button num4;
    @FXML
    Button num5;
    @FXML
    Button num6;
    @FXML
    Button num7;
    @FXML
    Button num8;
    @FXML
    Button num9;

    @FXML
    Button eraseBtn;

    @FXML
    Button numDot;

    @FXML
    Button cancelBtn;

    @FXML
    Button payLaterBtn;

    @FXML
    Button findTabBtn;

    @FXML
    Button payNowbtn;

    private DoubleProperty numPadProperty;
    private DoubleProperty taxProperty;
    private DoubleProperty totalSaleProperty;
    private String numPadStringValue;
    private Timer timer;
    private ClockUpdateTask clockTask;

    public Register() throws IOException {

        FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/openbowl/client/Register.fxml"));
        loader.setController(this);
        Parent root = loader.load();

        getChildren().add(root);
        numPadStringValue = "";
        numPadProperty = new SimpleDoubleProperty(0.0);

        taxProperty = new SimpleDoubleProperty(0.0);
        totalSaleProperty = new SimpleDoubleProperty(0.0);

        num0.setOnAction(notUsed -> onNumPadType(NumPadKeys.KEY_0));
        num1.setOnAction(notUsed -> onNumPadType(NumPadKeys.KEY_1));
        num2.setOnAction(notUsed -> onNumPadType(NumPadKeys.KEY_2));
        num3.setOnAction(notUsed -> onNumPadType(NumPadKeys.KEY_3));
        num4.setOnAction(notUsed -> onNumPadType(NumPadKeys.KEY_4));
        num5.setOnAction(notUsed -> onNumPadType(NumPadKeys.KEY_5));
        num6.setOnAction(notUsed -> onNumPadType(NumPadKeys.KEY_6));
        num7.setOnAction(notUsed -> onNumPadType(NumPadKeys.KEY_7));
        num8.setOnAction(notUsed -> onNumPadType(NumPadKeys.KEY_8));
        num9.setOnAction(notUsed -> onNumPadType(NumPadKeys.KEY_9));
        numDot.setOnAction(notUsed -> onNumPadType(NumPadKeys.KEY_DOT));
        eraseBtn.setOnAction(notUsed -> onNumPadType(NumPadKeys.KEY_BACKSPACE));

        salesTax.textProperty().bind(Bindings.format("$%04.2f", taxProperty));
        salesTotal.textProperty().bind(Bindings.format("$%04.2f", totalSaleProperty));

        cancelBtn.setOnAction(notUsed -> clearRegister());

        timer = new Timer();
        clockTask = new ClockUpdateTask();
        dateTime.textProperty().bind(clockTask.DateLabelProperty());
        timer.scheduleAtFixedRate(clockTask, 1000, 1000);

    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {

    }

    private void onNumPadType(NumPadKeys keystroke) {
        switch (keystroke) {
            case KEY_0:
                numPadStringValue += "0";
                break;
            case KEY_1:
                numPadStringValue += "1";
                break;
            case KEY_2:
                numPadStringValue += "2";
                break;
            case KEY_3:
                numPadStringValue += "3";
                break;
            case KEY_4:
                numPadStringValue += "4";
                break;
            case KEY_5:
                numPadStringValue += "5";
                break;
            case KEY_6:
                numPadStringValue += "6";
                break;
            case KEY_7:
                numPadStringValue += "7";
                break;
            case KEY_8:
                numPadStringValue += "8";
                break;
            case KEY_9:
                numPadStringValue += "9";
                break;
            case KEY_DOT:
                if (!numPadStringValue.contains(".")) {
                    numPadStringValue += ".";
                }

                break;
            case KEY_BACKSPACE:
                if (!numPadStringValue.isBlank()) {
                    numPadStringValue = numPadStringValue.substring(0, numPadStringValue.length() - 1);
                }
                break;
        }
        if (!numPadStringValue.isBlank()) {
            numPadProperty.set(Double.parseDouble(numPadStringValue));
        } else {
            numPadProperty.set(0.0);
        }
    }

    public void clearRegister() {
        numPadProperty.set(0.0);
        numPadStringValue = "";
        //recieptView.getRoot().getChildren().clear();
    }

    public void killTasks() {
        System.out.println("killing clock");
        clockTask.cancel();
        clockTask = null;
        timer.cancel();
        timer.purge();
        timer = null;
    }
}
