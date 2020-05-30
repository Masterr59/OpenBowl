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
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.ReadOnlyDoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.StringProperty;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.layout.Pane;

/**
 *
 * @author Open Bowl <http://www.openbowlscoring.org/>
 */
public class NumPad extends Pane implements Initializable {

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

    private final DoubleProperty numPadProperty;
    private String numPadStringValue;
    EventHandler<ActionEvent> zeroAction;

    public NumPad() throws IOException {
        numPadStringValue = "";
        numPadProperty = new SimpleDoubleProperty(0.0);
        zeroAction = null;

        FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/openbowl/client/NumPad.fxml"));
        loader.setController(this);
        Parent root = loader.load();

        getChildren().add(root);

        num0.setOnAction(notUsed -> onNumPadType(NumPad.Keys.KEY_0));
        num1.setOnAction(notUsed -> onNumPadType(NumPad.Keys.KEY_1));
        num2.setOnAction(notUsed -> onNumPadType(NumPad.Keys.KEY_2));
        num3.setOnAction(notUsed -> onNumPadType(NumPad.Keys.KEY_3));
        num4.setOnAction(notUsed -> onNumPadType(NumPad.Keys.KEY_4));
        num5.setOnAction(notUsed -> onNumPadType(NumPad.Keys.KEY_5));
        num6.setOnAction(notUsed -> onNumPadType(NumPad.Keys.KEY_6));
        num7.setOnAction(notUsed -> onNumPadType(NumPad.Keys.KEY_7));
        num8.setOnAction(notUsed -> onNumPadType(NumPad.Keys.KEY_8));
        num9.setOnAction(notUsed -> onNumPadType(NumPad.Keys.KEY_9));
        numDot.setOnAction(notUsed -> onNumPadType(NumPad.Keys.KEY_DOT));
        eraseBtn.setOnAction(notUsed -> onNumPadType(NumPad.Keys.KEY_BACKSPACE));
        this.setPadding(new Insets(5, 5, 5, 5));
    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {

    }

    private void onNumPadType(NumPad.Keys keystroke) {
        switch (keystroke) {
            case KEY_0:
                numPadStringValue += "0";
                if (this.numPadProperty.get() == 0.0) {
                    onZeroItem();
                    return;
                }
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

    private void onZeroItem() {
        if (this.zeroAction != null) {
            this.zeroAction.handle(null);
        }
    }

    private enum Keys {
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

    public ReadOnlyDoubleProperty NumPadValueProperty() {
        return this.numPadProperty;
    }

    public void setValue(double d) {
        this.numPadProperty.set(d);
        if (d == 0.0) {
            numPadStringValue = "";
        } else {
            numPadStringValue = new Double(d).toString();
            if (numPadStringValue.endsWith(".0")) {
                numPadStringValue = numPadStringValue.substring(0, numPadStringValue.length() - 2);
            }
        }
    }

    public void setButtonOnAction(EventHandler<ActionEvent> value, int row, int col) {
        getButton(row, col).setOnAction(value);
    }

    public void setButtonID(String id, int row, int col) {
        getButton(row, col).setId(id);
    }

    public StringProperty ButtonTextProperty(int row, int col) {
        return getButton(row, col).textProperty();
    }

    private Button getButton(int row, int col) {
        if (row == 0 && col == 0) {
            return this.adjustBtn;
        } else if (row == 0 && col == 1) {
            return this.specialBtn;
        } else if (row == 0 && col == 2) {
            return this.noSaleBtn;
        } else if (row == 1 && col == 3) {
            return this.cancelBtn;
        } else if (row == 2 && col == 3) {
            return this.payLaterBtn;
        } else if (row == 3 && col == 3) {
            return this.findTabBtn;
        } else if (row == 4 && col == 3) {
            return this.payNowbtn;
        }
        return new Button("NULL");
    }

    public void setOnZeroAction(EventHandler<ActionEvent> zero) {
        this.zeroAction = zero;
    }
}
