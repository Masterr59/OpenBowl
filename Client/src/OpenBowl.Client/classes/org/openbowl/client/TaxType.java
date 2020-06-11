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
class TaxType {

    public static final TaxType TAX_EXEMPT = new TaxType("EXEMPT", 0.0);
    public static final TaxType TAX_META_PACKAGE = new TaxType("Package", 0.0);
    public static final TaxType TEST_RATE = new TaxType("Test", 0.089);

    private String name;
    private double rate;

    public TaxType(String name, double rate) {
        this.name = name;
        this.rate = rate;
    }

    public String getName() {
        return name;
    }

    public double getRate() {
        return rate;
    }

    @Override
    public TaxType clone() {
        return new TaxType(new String(name), rate);
    }
}
