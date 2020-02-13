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
package org.openbowl.scorer;

/**
 *
 * @author Open Bowl <http://www.openbowlscoring.org/>
 */
public class RaspberryPiDetect {
        public static boolean isPi() {
        if (System.getProperty("os.name").equals("Linux")) {
            if (System.getProperty("os.arch").equals("x86") || System.getProperty("os.arch").equals("amd64")) {
                return false;
            }
            return true;
        }
        return false;
    }
}