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
import javafx.beans.binding.Bindings;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

/**
 *
 * @author Open Bowl <http://www.openbowlscoring.org/>
 */
public class PayNowDialog extends Alert {

    private final String MSG_TEXT = "Select Payment";
    private final String DUE_TEXT = "Amount Due ";
    private final String TENDERED_TEXT = "Tendered ";
    private final String EXACT_CHANGE = "Exact\nchange";
    private final String PAY_5_NOW = "$5.00";
    private final String PAY_10_NOW = "$10.00";
    private final String PAY_20_NOW = "$20.00";
    private final String PAY_50_NOW = "$50.00";

    private final NumPad numPad;
    private final double amountDue;
    private final DoubleProperty tenderedProperty;
    private PaymentType paymentType;

    public PayNowDialog(double d) throws IOException {
        super(AlertType.INFORMATION);
        this.getDialogPane().getStylesheets().add(getClass().getResource("DarkMode.css").toExternalForm());
        this.titleProperty().set(MSG_TEXT);
        this.headerTextProperty().set(MSG_TEXT);
        this.graphicProperty().set(null);

        amountDue = d;
        tenderedProperty = new SimpleDoubleProperty(0.0);
        numPad = new NumPad();
        tenderedProperty.bind(numPad.NumPadValueProperty());
        tenderedProperty.addListener((obs, on, nn) -> onTenderedChange());
        onTenderedChange();

        numPad.ButtonTextProperty(0, 0).set(EXACT_CHANGE);
        numPad.ButtonTextProperty(1, 3).set(PAY_5_NOW);
        numPad.ButtonTextProperty(2, 3).set(PAY_10_NOW);
        numPad.ButtonTextProperty(3, 3).set(PAY_20_NOW);
        numPad.ButtonTextProperty(4, 3).set(PAY_50_NOW);

        numPad.setButtonOnAction(notUsed -> numPad.setValue(amountDue), 0, 0);

        numPad.setButtonOnAction(nutUsed -> addMoney(5.0), 1, 3);
        numPad.setButtonOnAction(nutUsed -> addMoney(10.0), 2, 3);
        numPad.setButtonOnAction(nutUsed -> addMoney(20.0), 3, 3);
        numPad.setButtonOnAction(nutUsed -> addMoney(50.0), 4, 3);

        VBox outerVbox = new VBox();
        HBox topHBox = new HBox();
        Label dueLabel = new Label(DUE_TEXT);
        TextField dueField = new TextField(String.format("$%,4.2f", amountDue));
        dueField.disableProperty().set(true);
        Label tenderedLabel = new Label(TENDERED_TEXT);
        TextField tenderedField = new TextField();
        tenderedField.textProperty().bind(Bindings.format("$%,4.2f", tenderedProperty));

        topHBox.getChildren().addAll(dueLabel, dueField, tenderedLabel, tenderedField);

        outerVbox.getChildren().addAll(topHBox, numPad);
        this.getDialogPane().setContent(outerVbox);
        this.getButtonTypes().add(ButtonType.CANCEL);
    }

    private void addMoney(double cash) {
        Double newSum = cash + tenderedProperty.get();
        numPad.setValue(newSum);
    }

    private void onTenderedChange() {
        boolean enabled = this.tenderedProperty.greaterThanOrEqualTo(amountDue).get();
        this.getDialogPane().lookupButton(ButtonType.OK).disableProperty().set(!enabled);
    }
}
