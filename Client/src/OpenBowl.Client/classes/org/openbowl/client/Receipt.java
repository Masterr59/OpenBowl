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

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.scene.control.TreeItem;

/**
 *
 * @author Open Bowl <http://www.openbowlscoring.org/>
 */
public class Receipt extends TreeItem<String> {

    private final double DOUBLE_NOT_SET = Double.MIN_VALUE;

    private final String DEFAULT_TITLE = "New Tab";

    private IntegerProperty TransactionProperty;
    private double amountDue;
    private double amountTendered;
    private double amountTax;
    private double amountSubTotal;
    private PaymentType paymentType;

    public Receipt() {
        this.amountDue = DOUBLE_NOT_SET;
        this.amountTendered = DOUBLE_NOT_SET;
        this.amountTax = DOUBLE_NOT_SET;
        this.amountSubTotal = DOUBLE_NOT_SET;
        this.paymentType = null;
        this.TransactionProperty = new SimpleIntegerProperty(-1);
        this.valueProperty().set(DEFAULT_TITLE);
        this.TransactionProperty.addListener((obs, ov, nv) -> onTransactionChange(nv));
    }

    public Receipt(int id) {
        this();
        this.TransactionProperty.set(id);
    }

    @Override
    public String toString() {
        String ret = "Reciept id: " + this.valueProperty().get() + "\n";
        for (Object o : this.getChildren()) {
            ret += o.toString();
        }
        ret += "\n";
        if (this.amountTax != DOUBLE_NOT_SET) {
            ret += String.format("Tax: $%(03.2f\n", this.amountTax);
        }
        if (this.amountSubTotal != DOUBLE_NOT_SET) {
            ret += String.format("Sub Total: $%(03.2f\n", this.amountSubTotal);
        }
        if (this.amountDue != DOUBLE_NOT_SET) {
            ret += String.format("Due: $%(03.2f\n", this.amountDue);
        }
        if (this.paymentType != null) {
            ret += "Payment Type: " + this.paymentType.toString() + "\n";
        }
        if (this.amountTendered != DOUBLE_NOT_SET) {
            ret += String.format("Tendered: $%(03.2f\n", this.amountTendered);
        }
        if (this.amountTendered != DOUBLE_NOT_SET && this.amountDue != DOUBLE_NOT_SET) {
            double change = this.amountTendered - this.amountDue;
            ret += String.format("Change: $%(03.2f\n", change);
        }

        return ret;
    }

    public IntegerProperty TransactionProperty() {
        return TransactionProperty;
    }

    private void onTransactionChange(Number nv) {
        this.valueProperty().set(String.format("%d", nv.intValue()));
    }

    @Override
    public Receipt clone() {
        Receipt clone = new Receipt();
        if (this.amountTax != DOUBLE_NOT_SET) {
            clone.setAmountTax(amountTax);
        }
        if (this.amountSubTotal != DOUBLE_NOT_SET) {
            clone.setAmountSubTotal(amountSubTotal);
        }
        if (this.amountDue != DOUBLE_NOT_SET) {
            clone.setAmountDue(amountDue);
        }
        if (this.paymentType != null) {
            clone.setPaymentType(paymentType);
        }
        if (this.amountTendered != DOUBLE_NOT_SET) {
            clone.setAmountTendered(amountTendered);
        }
        if (this.TransactionProperty.get() > -1) {
            clone.TransactionProperty().set(this.TransactionProperty.get());
            System.out.printf("Setting clone id to %d\n", this.TransactionProperty.get());
        }
        for (TreeItem<String> ti : this.getChildren()) {
            if (ti instanceof ProductUseage) {

                clone.getChildren().add((ProductUseage) ((ProductUseage) ti).clone());
            } else {
                clone.getChildren().add(ti);
            }
        }

        return clone;
    }

    public double getAmountDue() {
        return amountDue;
    }

    public void setAmountDue(double amountDue) {
        this.amountDue = amountDue;
    }

    public double getAmountTendered() {
        return amountTendered;
    }

    public void setAmountTendered(double amountTendered) {
        this.amountTendered = amountTendered;
    }

    public void setAmountTax(double amountTax) {
        this.amountTax = amountTax;
    }

    public void setAmountSubTotal(double amountSubTotal) {
        this.amountSubTotal = amountSubTotal;
    }

    public void setPaymentType(PaymentType paymentType) {
        this.paymentType = paymentType;
    }

    public double getAmountTax() {
        return amountTax;
    }

    public double getAmountSubTotal() {
        return amountSubTotal;
    }

    public PaymentType getPaymentType() {
        return paymentType;
    }

    public Receipt requiresLaneSelection() {
        for (Object o : this.getChildren()) {
            if (o instanceof ProductUseage) {
                ProductUseage pu = (ProductUseage) o;
                if (pu.requiresLaneSelection() != null) {
                    return pu.requiresLaneSelection();
                }
            }
        }
        return null;
    }
}
