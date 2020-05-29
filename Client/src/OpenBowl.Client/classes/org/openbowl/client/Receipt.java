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

    private final String DEFAULT_TITLE = "New Tab";

    private IntegerProperty TransactionProperty;

    public Receipt() {
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

        return ret;
    }

    public IntegerProperty TransactionProperty() {
        return TransactionProperty;
    }

    private void onTransactionChange(Number nv) {
        this.valueProperty().set(String.format("%d", nv.intValue()));
    }
    
    @Override
    public Receipt clone(){
        Receipt clone = new Receipt();
        if(this.TransactionProperty.get() > -1){
            clone.TransactionProperty().set(this.TransactionProperty.get());
        }
        for(TreeItem<String> ti : this.getChildren()){
            if(ti instanceof ProductUseage){
                clone.getChildren().add((ProductUseage)((ProductUseage) ti).clone());
            }
            else{
                clone.getChildren().add(ti);
            }
        }
        
        return clone;
    }

}
