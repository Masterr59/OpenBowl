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

/**
 *
 * @author Open Bowl <http://www.openbowlscoring.org/>
 */
public class ProductType {

    public static final ProductType TEST_TYPE = new ProductType(-1, "Test Type");
    public static final ProductType GAME_TYPE = new ProductType(0, "Game");
    public static final ProductType FOOD_TYPE = new ProductType(1, "Food / Drink");

    private int ID;
    private String Name;

    public ProductType(int ID, String Name) {
        this.ID = ID;
        this.Name = Name;
    }

    public int getID() {
        return ID;
    }

    public String getName() {
        return Name;
    }

    @Override
    public ProductType clone() {
        if (this == TEST_TYPE) {
            return TEST_TYPE;
        }
        if (this == GAME_TYPE) {
            return GAME_TYPE;
        }
        if (this == FOOD_TYPE) {
            return FOOD_TYPE;
        }
        return new ProductType(ID, new String(Name));
    }

    @Override
    public String toString() {
        return "ProductType{" + "ID=" + ID + ", Name=" + Name + '}';
    }

}
