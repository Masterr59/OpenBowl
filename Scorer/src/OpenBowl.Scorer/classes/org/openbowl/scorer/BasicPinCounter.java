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

import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.prefs.Preferences;
import javafx.scene.paint.Color;
import org.openbowl.common.BowlingPins;

/**
 *
 * @author Open Bowl <http://www.openbowlscoring.org/>
 */
public class BasicPinCounter implements PinCounter {

    public final String X_SETTING = "X";
    public final String Y_SETTING = "Y";
    public final String RADIUS_SETTING = "Radius";
    public final String LEVEL_SETTING = "Level";
    public final String RED_SETTING = "Red";
    public final String BLUE_SETTING = "Blue";
    public final String GREEN_SETTING = "Green";

    public final int DEFAULT_X = 1;
    public final int DEFAULT_Y = 1;
    public final int DEFAULT_LEVEL = 100;
    public final int DEFAULT_RADIUS = 10;
    public final Color DEFAULT_COLOR = Color.WHITE;

    private ArrayList<PinCounterTarget> targetList;
    private BufferedImage lastCameraImage;
    private String name;
    private final Preferences prefs;

    public BasicPinCounter(String name) {
        this.name = name;
        targetList = new ArrayList<>(0);
        prefs = Preferences.userNodeForPackage(this.getClass());

        try {
            lastCameraImage = LaneCamera.getCurrentCameraImage();
        } catch (InterruptedException e) {
            e.printStackTrace(System.out);
        }
        setup();
    }

    /**
     * Adds newTarget to the pin counter's target list if there are no other
     * pins in already in the list with the same pin number
     *
     * @param newTarget The target to be added
     *
     * @throws IllegalArgumentException on duplicate pin number or out-of-bounds
     * x/y positions
     */
    public void addTarget(PinCounterTarget newTarget) {
        for (int i = 0; i < targetList.size(); i++) {
            if (targetList.get(i).getPin() == newTarget.getPin()) {
                throw new IllegalArgumentException("Pin " + newTarget.getPin() + "is already in the target list.");
            }
        }

        if (newTarget.getXPosition() > lastCameraImage.getWidth()) {
            throw new IllegalArgumentException("Target for pin " + newTarget.getPin() + " has x position " + newTarget.getXPosition() + " larger than camera image width " + lastCameraImage.getWidth() + ".");
        }

        if (newTarget.getYPosition() > lastCameraImage.getHeight()) {
            throw new IllegalArgumentException("Target for pin " + newTarget.getPin() + " has y position " + newTarget.getYPosition() + " larger than camera image height " + lastCameraImage.getHeight() + ".");
        }

        targetList.add(newTarget);
    }

    /**
     * Opens a GUI dialog that allows the use to configure the pin counter
     * settings
     */
    @Override
    public void configureDialog() {
        try {
            BasicPinCounterOptionsController dialog = new BasicPinCounterOptionsController(name, this);
            dialog.setTitle(name);
            dialog.showAndWait();

        } catch (IOException e) {
            System.out.println("Error showing dialog " + e.toString());
            e.printStackTrace();
        }
    }

    /**
     * Counts the number of standing pins and returns their pin numbers
     *
     * @return A list of the standing pins' pin numbers
     */
    @Override
    public ArrayList<BowlingPins> countPins() {
        try {
            lastCameraImage = LaneCamera.getCurrentCameraImage();
        } catch (InterruptedException e) {
            e.printStackTrace(System.out);
        }

        Rectangle radiusBoundary = new Rectangle();
        ArrayList<BowlingPins> standingPinList = new ArrayList<>(0);

        for (int i = 0; i < targetList.size(); i++) {
            int boundarySize;
            PinCounterTarget currentTarget = targetList.get(i);
            int currentX = currentTarget.getXPosition();
            int currentY = currentTarget.getYPosition();
            int currentRadius = currentTarget.getRadius();
            int currentPixel;
            int pixelSumRed = 0;
            int pixelSumGreen = 0;
            int pixelSumBlue = 0;
            int pixelAverage;
            int pixelAverageRed;
            int pixelAverageGreen;
            int pixelAverageBlue;

            if (currentX - (currentRadius - 1) < 0) {
                radiusBoundary.x = 0;
                radiusBoundary.width = ((2 * currentRadius) - 1) + (currentX - (currentRadius - 1));
            } else if (currentX + (currentRadius - 1) > lastCameraImage.getWidth()) {
                radiusBoundary.x = currentX - (currentRadius - 1);
                radiusBoundary.width = ((2 * currentRadius) - 1) - ((currentX + (currentRadius - 1)) - lastCameraImage.getWidth());
            } else {
                radiusBoundary.x = currentX - (currentRadius - 1);
                radiusBoundary.width = (2 * currentRadius) - 1;
            }

            if (currentY - (currentRadius - 1) < 0) {
                radiusBoundary.y = 0;
                radiusBoundary.height = ((2 * currentRadius) - 1) + (currentY - (currentRadius - 1));
            } else if (currentY + (currentRadius - 1) > lastCameraImage.getHeight()) {
                radiusBoundary.y = currentY - (currentRadius - 1);
                radiusBoundary.height = ((2 * currentRadius) - 1) - ((currentY + (currentRadius - 1)) - lastCameraImage.getHeight());
            } else {
                radiusBoundary.y = currentY - (currentRadius - 1);
                radiusBoundary.height = (2 * currentRadius) - 1;
            }

            for (int j = radiusBoundary.x; j < radiusBoundary.x + radiusBoundary.width; j++) {
                for (int k = radiusBoundary.y; k < radiusBoundary.y + radiusBoundary.height; k++) {
                    currentPixel = lastCameraImage.getRGB(j, k);
                    pixelSumRed += (currentPixel & 0x000000ff);
                    pixelSumGreen += (currentPixel & 0x0000ff00) >> 8;
                    pixelSumBlue += (currentPixel & 0x00ff0000) >> 16;
                }
            }

            boundarySize = radiusBoundary.height * radiusBoundary.width;
            pixelAverageRed = pixelSumRed / boundarySize;
            pixelAverageGreen = pixelSumGreen / boundarySize;
            pixelAverageBlue = pixelSumBlue / boundarySize;

            if (currentTarget.isColorDifferenceAveraged()) {
                pixelAverage = (pixelAverageRed + pixelAverageGreen + pixelAverageBlue) / 3;

                if (Math.abs(currentTarget.getAverageIntensitySetpoint() - pixelAverage) < currentTarget.getAverageDifferenceThreshold()) {
                    standingPinList.add(currentTarget.getPin());
                }
            } else {
                if (Math.abs(currentTarget.getRDifferenceThreshold() - pixelAverageRed) < currentTarget.getRIntensitySetpoint()
                        && Math.abs(currentTarget.getGDifferenceThreshold() - pixelAverageGreen) < currentTarget.getGIntensitySetpoint()
                        && Math.abs(currentTarget.getBDifferenceThreshold() - pixelAverageBlue) < currentTarget.getBIntensitySetpoint()) {
                    standingPinList.add(currentTarget.getPin());
                }
            }
        }

        return standingPinList;
    }

    /**
     * Gets the current pin counter configuration settings
     *
     * @return
     *
     */
    @Override
    public Map<String, Object> getConfiguration() {
        log("Get Configuration called");
        Map<String, Object> ret = new HashMap<>();
        ret.put("Type", this.getClass().getName());
        BowlingPins[] allPins = BowlingPins.values();
        for (BowlingPins p : allPins) {
            int X, Y, R, level;
            X = prefs.getInt(name + "-" + p.toString() + "-" + X_SETTING, DEFAULT_X);
            Y = prefs.getInt(name + "-" + p.toString() + "-" + Y_SETTING, DEFAULT_Y);
            R = prefs.getInt(name + "-" + p.toString() + "-" + RADIUS_SETTING, DEFAULT_RADIUS);
            level = prefs.getInt(name + "-" + p.toString() + "-" + LEVEL_SETTING, DEFAULT_LEVEL);
            double r, g, b;
            r = prefs.getDouble(name + "-" + p.toString() + "-" + RED_SETTING, DEFAULT_COLOR.getRed());
            g = prefs.getDouble(name + "-" + p.toString() + "-" + GREEN_SETTING, DEFAULT_COLOR.getGreen());
            b = prefs.getDouble(name + "-" + p.toString() + "-" + BLUE_SETTING, DEFAULT_COLOR.getBlue());

            ret.put(p.toString() + "-" + X_SETTING, X);
            ret.put(p.toString() + "-" + Y_SETTING, Y);
            ret.put(p.toString() + "-" + RADIUS_SETTING, R);
            ret.put(p.toString() + "-" + LEVEL_SETTING, level);
            ret.put(p.toString() + "-" + RED_SETTING, r);
            ret.put(p.toString() + "-" + GREEN_SETTING, g);
            ret.put(p.toString() + "-" + BLUE_SETTING, b);

        }
        return ret;
    }

    /**
     * Gets the last camera image taken by the pin counter
     *
     * @return The last camera image
     */
    public BufferedImage getLastCameraImage() {
        return lastCameraImage;
    }

    /**
     * Gets the list of PinCounterTarget objects in the target list
     *
     * @return The list of PinCounterTarget objects in the target list
     */
    public ArrayList<PinCounterTarget> getTargetList() {
        return targetList;
    }

    /**
     * Removes the PinCounterTarget with the pin number equal to the "pin"
     * parameter
     *
     * @param pin The pin number (BowlingPins) of the PinCounterTarget to be
     * removed
     *
     * @return True if a PinCounterTarget with the provided pin number was
     * removed, false if no PinCounterTarget with the pin number was found
     */
    public boolean removeTarget(BowlingPins pin) {
        boolean isPinRemoved = false;

        for (int i = 0; i < targetList.size(); i++) {
            if (targetList.get(i).getPin() == pin) {
                targetList.remove(i);
                isPinRemoved = true;
                break;
            }
        }

        return isPinRemoved;
    }

    /**
     * Sets the current pin counter configuration settings
     *
     * @param configuration
     * @return
     *
     */
    @Override
    public String setConfiguration(Map<String, Object> configuration) {
        String results = "";
        try {
            String type = (String) configuration.get("Type");

            if (type.equals(this.getClass().getName())) {
                teardown();
                BowlingPins[] allPins = BowlingPins.values();
                for (BowlingPins p : allPins) {
                    int X, Y, R, level;
                    double r, g, b;
                    X = (new Double((double) configuration.get(p.toString() + "-" + X_SETTING))).intValue();
                    Y = (new Double((double) configuration.get(p.toString() + "-" + Y_SETTING))).intValue();
                    R = (new Double((double) configuration.get(p.toString() + "-" + RADIUS_SETTING))).intValue();
                    level = (new Double((double) configuration.get(p.toString() + "-" + LEVEL_SETTING))).intValue();

                    r = (double) configuration.get(p.toString() + "-" + RED_SETTING);
                    g = (double) configuration.get(p.toString() + "-" + GREEN_SETTING);
                    b = (double) configuration.get(p.toString() + "-" + BLUE_SETTING);

                    prefs.putInt(name + "-" + p.toString() + "-" + X_SETTING, X);
                    prefs.putInt(name + "-" + p.toString() + "-" + Y_SETTING, Y);
                    prefs.putInt(name + "-" + p.toString() + "-" + RADIUS_SETTING, R);
                    prefs.putInt(name + "-" + p.toString() + "-" + LEVEL_SETTING, level);

                    prefs.putDouble(name + "-" + p.toString() + "-" + RED_SETTING, r);
                    prefs.putDouble(name + "-" + p.toString() + "-" + BLUE_SETTING, g);
                    prefs.putDouble(name + "-" + p.toString() + "-" + GREEN_SETTING, b);

                }
                setup();
            } else {
                results += "Incorrect device type";
            }
        } catch (ClassCastException e) {
            results += e.getMessage();
        } catch (NullPointerException e) {
            results += "NullPointException: " + e.getMessage();
        }
        log("Set configuration called: " + results);
        return results;
    }

    @Override
    public String setup() {
        String ret = "";
        BowlingPins[] allPins = BowlingPins.values();
        for (BowlingPins p : allPins) {
            int X, Y, R, level;
            X = prefs.getInt(name + "-" + p.toString() + "-" + X_SETTING, DEFAULT_X);
            Y = prefs.getInt(name + "-" + p.toString() + "-" + Y_SETTING, DEFAULT_Y);
            R = prefs.getInt(name + "-" + p.toString() + "-" + RADIUS_SETTING, DEFAULT_RADIUS);
            level = prefs.getInt(name + "-" + p.toString() + "-" + LEVEL_SETTING, DEFAULT_LEVEL);
            double r, g, b;
            r = prefs.getDouble(name + "-" + p.toString() + "-" + RED_SETTING, DEFAULT_COLOR.getRed());
            g = prefs.getDouble(name + "-" + p.toString() + "-" + GREEN_SETTING, DEFAULT_COLOR.getGreen());
            b = prefs.getDouble(name + "-" + p.toString() + "-" + BLUE_SETTING, DEFAULT_COLOR.getBlue());

            PinCounterTarget pinTarget = new PinCounterTarget(level, 255, p, R, X, Y);
            addTarget(pinTarget);

        }
        return ret;
    }

    @Override
    public void teardown() {
        targetList.clear();
    }
}
