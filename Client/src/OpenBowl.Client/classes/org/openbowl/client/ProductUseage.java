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

import javafx.beans.binding.Bindings;
import javafx.beans.property.IntegerProperty;
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
    private int laneID;
    private StringProperty productDescription;

    public ProductUseage(Product Product_ID, int QTY) {
        this.Product_ID = Product_ID;
        this.QTY = new SimpleIntegerProperty(QTY);
        productDescription = new SimpleStringProperty();
        String bindingFormat = "%d " + Product_ID.getProduct_Name();
        productDescription.bind(Bindings.format(bindingFormat, QTY));
        
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
        this.getChildren().add(subProduct);
    }

}
