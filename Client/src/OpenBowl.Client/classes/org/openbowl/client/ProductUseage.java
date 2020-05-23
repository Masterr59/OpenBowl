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

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.scene.control.TreeItem;

/**
 *
 * @author Open Bowl <http://www.openbowlscoring.org/>
 */
public class ProductUseage extends TreeItem {

    private int Transaction_ID;
    private Product Product_ID;
    private IntegerProperty QTY;
    private DoubleProperty subTotal;
    private DoubleProperty taxTotal;
    private int laneID;
    private StringProperty productDescription;
    private int Depth;

    public ProductUseage(Product Product_ID, int qty) {
        this.Depth = 0;
        this.Product_ID = Product_ID;
        this.QTY = new SimpleIntegerProperty(qty);
        this.subTotal = new SimpleDoubleProperty(0.0);
        this.subTotal.bind(this.QTY.multiply(Product_ID.Product_PriceProperty()));

        this.taxTotal = new SimpleDoubleProperty(0.0);
        this.taxTotal.bind(this.QTY.multiply(Product_ID.Product_PriceProperty().get() * Product_ID.getTax_Type().getRate()));

        productDescription = new SimpleStringProperty("TBD");
        this.QTY.addListener(not_used -> updateDisc());
        this.Product_ID.Product_PriceProperty().addListener(notUsed -> updateDisc());
        updateDisc();
        this.valueProperty().bind(productDescription);
    }

    public ProductUseage(Product Product_ID, int QTY, int laneID) {
        this(Product_ID, QTY);
        this.laneID = laneID;

    }

    public ProductUseage(int Transaction_ID, Product Product_ID, int QTY, int laneID) {
        this(Product_ID, QTY, laneID);
        this.Transaction_ID = Transaction_ID;
    }

    public int getTransaction_ID() {
        return Transaction_ID;
    }

    public void setTransaction_ID(int Transaction_ID) {
        this.Transaction_ID = Transaction_ID;
    }

    public Product getProduct_ID() {
        return Product_ID;
    }

    public void setProduct_ID(Product Product_ID) {
        this.Product_ID = Product_ID;
    }

    public IntegerProperty QTYProperty() {
        return QTY;
    }

    public int getLaneID() {
        return laneID;
    }

    public void setLaneID(int laneID) {
        this.laneID = laneID;
    }

    public void addChildProduct(ProductUseage subProduct) {
        subProduct.setDepth(Depth + 1);
        subProduct.updateDisc();
        this.getChildren().add(subProduct);
        this.updateDisc();
    }

    public void updateDisc() {
        String padd = "";
        String formattedLine = "";
        int qty = this.QTY.get();
        double price = this.Product_ID.Product_PriceProperty().doubleValue();
        double total = price * qty;
        int max = Register.MAX_LINE_LENGTH;
        max -= this.Depth;

        for (padd = ""; padd.length() < max && formattedLine.length() < max; padd += " ") {
            String bindingFormat = "%d @ $%(03.2f " + padd + "$%(03.2f";
            formattedLine = String.format(bindingFormat, qty, price, total);
        }
        String disc = this.Product_ID.getProduct_Name();
        if (disc.length() > max) {
            disc = disc.substring(0, max - 2);
            disc += "…";
        }
        disc += "\n";
        disc += formattedLine;
        if (!this.productDescription.get().equals(disc)) {
            this.productDescription.set(disc);
        }
    }

    public void setDepth(int d) {
        this.Depth = d;
    }

}
