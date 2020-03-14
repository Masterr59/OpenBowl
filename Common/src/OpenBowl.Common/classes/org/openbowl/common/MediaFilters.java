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
package org.openbowl.common;

import java.io.File;
import java.io.FilenameFilter;

/**
 *
 * @author Open Bowl <http://www.openbowlscoring.org/>
 */
public class MediaFilters {

    public static FilenameFilter createVideoFileFilter() {
        return new FilenameFilter() {
            private final String[] extensions = {"mp4", "m4v", "flv"};

            @Override
            public boolean accept(File file, String name) {
                for (String ext : extensions) {
                    if (name.endsWith(ext) || name.endsWith(ext.toUpperCase())) {
                        return true;
                    }
                }
                return false;
            }

        };
    }
    
    public static FilenameFilter createAudioFileFilter() {
        return new FilenameFilter() {
            private final String[] extensions = {"mp3", "m4a", "wav", "aif", "aiff"};

            @Override
            public boolean accept(File file, String name) {
                for (String ext : extensions) {
                    if (name.endsWith(ext) || name.endsWith(ext.toUpperCase())) {
                        return true;
                    }
                }
                return false;
            }

        };
    }
}
