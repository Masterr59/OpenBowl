/*
 * Copyright (C) 2020 patrick
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
package org.openbowl.common;

import javafx.scene.image.Image;

/**
 *
 * @author patrick
 */
public class CommonImages {

    public Image appIcon() {
        return new Image(getClass().getResource("/org/openbowl/common/images/applogowebshort.png").toExternalForm());
    }

}
