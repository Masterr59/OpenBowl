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
import javafx.beans.property.SimpleDoubleProperty;

/**
 *
 * @author Open Bowl <http://www.openbowlscoring.org/>
 */
public class Product {

    public static final Product TEST_PRODUCT = new Product(-1, "Test Product", 19.99, -1, ProductType.TEST_TYPE, TaxType.TEST_RATE);

    private int Product_ID;
    private String Product_Name;
    private DoubleProperty Product_Price;

    private int Sub_Dept_ID;

    private ProductType Product_type;
    private TaxType Tax_Type;

    public Product(int Product_ID, String Product_Name, double Product_Price, int Sub_Dept_ID, ProductType Product_type, TaxType Tax_Type) {
        this.Product_ID = Product_ID;
        this.Product_Name = Product_Name;
        this.Product_Price = new SimpleDoubleProperty(Product_Price);
        this.Sub_Dept_ID = Sub_Dept_ID;
        this.Product_type = Product_type;
        this.Tax_Type = Tax_Type;

    }

    public String getProduct_Name() {
        return Product_Name;
    }

    public TaxType getTax_Type() {
        return Tax_Type;
    }

    public DoubleProperty Product_PriceProperty() {
        return Product_Price;
    }

}
