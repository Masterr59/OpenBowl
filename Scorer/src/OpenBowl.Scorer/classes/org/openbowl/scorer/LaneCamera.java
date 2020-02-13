/*
 * Copyright (C) 2020 pi
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

import java.awt.image.BufferedImage;
import static java.awt.image.BufferedImage.TYPE_INT_RGB;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;

/**
 *
 * @author Open Bowl <http://www.openbowlscoring.org/>
 */
public class LaneCamera
{
    public static BufferedImage getCurrentCameraImage() throws InterruptedException
    {
        BufferedImage currentCameraImage = new BufferedImage(1, 1, TYPE_INT_RGB);
        
        try
        {   
            /*
             * raspistillScript contains the command "raspistill -e png -o currentPinImage.png -t 1 -n".
             * This script must be manually placed into the correct directory, as LaneCamera will not do that for you.
             *
             * raspistill is a generic tool used for taking images with raspberry pi camera modules
             *
             *
             * Parameters
             *
             * "-e png" encodes the resulting image as a PNG
             *
             * "-o currentPinImage.png" writes the output to file currentPinImage.png
             *
             * "-t 1" sets the delay between command execution and the image being taken to be 1 millisecond
             *
             * "-n" disables the preview window (displays what is in front of the camera until the image is taken, and stays open until the file is written)
             *
             * No shutter speed option is used - the default lasts quite a while, which isn't an issue for what is mostly a still image. 
             */
            ProcessBuilder raspistillProcessBuilder = new ProcessBuilder("./raspistillScript");
            Process raspistillProcess = raspistillProcessBuilder.start();
            raspistillProcess.waitFor(); // If the process never ends, then the program will hang. There is no simple way to make this time out.
            
            currentCameraImage = ImageIO.read(new File("currentPinImage.png"));
        }
        catch (IOException e)
        {
            e.printStackTrace(System.out);
        }
        
        return currentCameraImage;
    }
}
