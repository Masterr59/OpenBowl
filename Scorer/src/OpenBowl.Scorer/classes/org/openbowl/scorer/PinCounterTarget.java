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

import org.openbowl.common.BowlingPins;

/**
 *
 * @author Open Bowl <http://www.openbowlscoring.org/>
 */
public class PinCounterTarget
{
    private int averageDifferenceThreshold;
    private int averageIntensitySetpoint;
    private int bDifferenceThreshold;
    private int bIntensitySetpoint;
    private int gDifferenceThreshold;
    private int gIntensitySetpoint;
    private boolean isColorDifferenceAveraged;
    private BowlingPins pin;
    private int rDifferenceThreshold;
    private int rIntensitySetpoint;
    private int radius;
    private int xPosition;
    private int yPosition;
    
    public PinCounterTarget(int rDifferenceThreshold, int rIntensitySetpoint, int gDifferenceThreshold, int gIntensitySetpoint, int bDifferenceThreshold, int bIntensitySetpoint, boolean isColorDifferenceAveraged, int averageDifferenceThreshold, int averageIntensitySetpoint, BowlingPins pin, int radius, int xPosition, int yPosition)
    {
        setRDifferenceThreshold(rDifferenceThreshold);
        setRIntensitySetpoint(rIntensitySetpoint);
        setGDifferenceThreshold(gDifferenceThreshold);
        setGIntensitySetpoint(gIntensitySetpoint);
        setBDifferenceThreshold(bDifferenceThreshold);
        setBIntensitySetpoint(bIntensitySetpoint);
        setColorDifferenceAveraged(isColorDifferenceAveraged);
        setAverageDifferenceThreshold(averageDifferenceThreshold);
        setAverageIntensitySetpoint(averageIntensitySetpoint);
        setPin(pin);
        setRadius(radius);
        setXPosition(xPosition);
        setYPosition(yPosition);
    }
    
    /**
     * Gets the average difference threshold of the target
     * 
     * If the average color intensity of the red, green, and blue channels is
     * less than averageDifferenceThreshold away from averageIntensitySetpoint,
     * then the targeted pin is considered standing.
     * 
     * @return The average difference threshold of the target
     */
    public int getAverageDifferenceThreshold()
    {
        return averageDifferenceThreshold;
    }
    
    
    /**
     * Gets the average intensity setpoint of the target
     * 
     * If the average color intensity of the red, green, and blue channels is
     * less than averageDifferenceThreshold away from averageIntensitySetpoint,
     * then the targeted pin is considered standing.
     * 
     * @return The average intensity setpoint of the target
     */
    public int getAverageIntensitySetpoint()
    {
        return averageIntensitySetpoint;
    }
    
    /**
     * Gets the blue channel difference threshold of the target
     * 
     * If the intensity of the blue channel is greater than bDifferenceThreshold
     * away from bIntensitySetpoint, then the targeted pin is not considered
     * standing.
     * 
     * @return The blue channel difference threshold of the target
     */
    public int getBDifferenceThreshold()
    {
        return bDifferenceThreshold;
    }
    
    /**
     * Gets the blue channel intensity setpoint of the target
     * 
     * If the intensity of the blue channel is greater than bDifferenceThreshold
     * away from bIntensitySetpoint, then the targeted pin is not considered
     * standing.
     * 
     * @return The blue channel intensity setpoint of the target
     */
    public int getBIntensitySetpoint()
    {
        return bIntensitySetpoint;
    }
    
    /**
     * Gets the green channel difference threshold of the target
     * 
     * If the intensity of the green channel is greater than gDifferenceThreshold
     * away from gIntensitySetpoint, then the targeted pin is not considered
     * standing.
     * 
     * @return The green channel difference threshold of the target
     */
    public int getGDifferenceThreshold()
    {
        return gDifferenceThreshold;
    }
    
    /**
     * Gets the green channel intensity setpoint of the target
     * 
     * If the intensity of the green channel is greater than gDifferenceThreshold
     * away from gIntensitySetpoint, then the targeted pin is not considered
     * standing.
     * 
     * @return The green channel intensity setpoint of the target
     */
    public int getGIntensitySetpoint()
    {
        return gIntensitySetpoint;
    }
    
    /**
     * Gets the target pin's number (a BowlingPins enum value)
     * 
     * @return The target pin's number
     */
    public BowlingPins getPin()
    {
        return pin;
    }
    
    /**
     * Gets the red channel difference threshold of the target
     * 
     * If the intensity of the red channel is greater than rDifferenceThreshold
     * away from rIntensitySetpoint, then the targeted pin is not considered
     * standing.
     * 
     * @return The red channel difference threshold of the target
     */
    public int getRDifferenceThreshold()
    {
        return rDifferenceThreshold;
    }
    
    /**
     * Gets the red channel intensity setpoint of the target
     * 
     * If the intensity of the red channel is greater than rDifferenceThreshold
     * away from rIntensitySetpoint, then the targeted pin is not considered
     * standing.
     * 
     * @return The red channel intensity setpoint of the target
     */
    public int getRIntensitySetpoint()
    {
        return rIntensitySetpoint;
    }
    
    /**
     * Gets the radius describing the number of pixels around the target's
     * x and y position.
     * 
     * A radius of "1" contains only the pixel at the chosen position.
     * A radius of "2" is a 3x3 square of pixels centered at the chosen position.
     * A radius of "3" is a 5x5 square of pixels centered at the chosen position.
     * etc.
     * 
     * If pixels in the radius exceed the camera image's boundaries, then they
     * are not taken into consideration.
     * 
     * @return The size of the square radius describing what pixels around the
     * chosen position to check
     */
    public int getRadius()
    {
        return radius;
    }
    
    /**
     * Gets the x position of the target's center pixel
     * 
     * @return The x position of the target's center pixel
     */
    public int getXPosition()
    {
        return xPosition;
    }
    
    /**
     * Gets the y position of the target's center pixel
     * 
     * @return The y position of the target's center pixel
     */
    public int getYPosition()
    {
        return yPosition;
    }
    
    /**
     * Gets whether the pin standing condition is based on the average of the
     * red, green, and blue color channels (true) or based on each color channel
     * individually (false).
     * 
     * @return The value of the boolean controlling the condition used to
     * determine if the pin is standing
     */
    public boolean isColorDifferenceAveraged()
    {
        return isColorDifferenceAveraged;
    }
    
    /**
     * Sets the average difference threshold of the target
     * 
     * If the average color intensity of the red, green, and blue channels is
     * less than averageDifferenceThreshold away from averageIntensitySetpoint,
     * then the targeted pin is considered standing.
     * 
     * @param averageDifferenceThreshold The new average distance threshold - it must be between 0 and 255 inclusive.
     */
    public final void setAverageDifferenceThreshold(int averageDifferenceThreshold)
    {
        if (averageDifferenceThreshold < 0 || averageDifferenceThreshold > 255)
        {
            throw new IllegalArgumentException("averageDifferenceThreshold value of " + averageDifferenceThreshold + "is out of the acceptable range of 0 to 255 inclusive.");
        }
        
        this.averageDifferenceThreshold = averageDifferenceThreshold;
    }
    
    /**
     * Sets the average intensity setpoint of the target
     * 
     * If the average color intensity of the red, green, and blue channels is
     * less than averageDifferenceThreshold away from averageIntensitySetpoint,
     * then the targeted pin is considered standing.
     * 
     * @param averageIntensitySetpoint The new average intensity setpoint - it must be between 0 and 255 inclusive.
     */
    public final void setAverageIntensitySetpoint(int averageIntensitySetpoint)
    {
        if (averageIntensitySetpoint < 0 || averageIntensitySetpoint > 255)
        {
            throw new IllegalArgumentException("averageIntensitySetpoint value of " + averageIntensitySetpoint + "is out of the acceptable range of 0 to 255 inclusive.");
        }
        
        this.averageIntensitySetpoint = averageIntensitySetpoint;
    }
    
    /**
     * Sets the blue channel difference threshold of the target
     * 
     * If the intensity of the blue channel is greater than bDifferenceThreshold
     * away from bIntensitySetpoint, then the targeted pin is not considered
     * standing.
     * 
     * @param bDifferenceThreshold The new blue channel difference threshold - it must be between 0 and 255 inclusive
     */
    public final void setBDifferenceThreshold(int bDifferenceThreshold)
    {
        if (bDifferenceThreshold < 0 || bDifferenceThreshold > 255)
        {
            throw new IllegalArgumentException("bDifferenceThreshold value of " + bDifferenceThreshold + "is out of the acceptable range of 0 to 255 inclusive.");
        }
        
        this.bDifferenceThreshold = bDifferenceThreshold;
    }
    
    /**
     * Sets the blue channel intensity setpoint of the target
     * 
     * If the intensity of the blue channel is greater than bDifferenceThreshold
     * away from bIntensitySetpoint, then the targeted pin is not considered
     * standing.
     * 
     * @param bIntensitySetpoint The new blue channel intensity setpoint - it must be between 0 and 255 inclusive
     */
    public final void setBIntensitySetpoint(int bIntensitySetpoint)
    {
        if (bIntensitySetpoint < 0 || bIntensitySetpoint > 255)
        {
            throw new IllegalArgumentException("bIntensitySetpoint value of " + bIntensitySetpoint + "is out of the acceptable range of 0 to 255 inclusive.");
        }
        
        this.bIntensitySetpoint = bIntensitySetpoint;
    }
    
    /**
     * Sets whether the pin standing condition is based on the average of the
     * red, green, and blue color channels (true) or based on each color channel
     * individually (false).
     * 
     * @parameter isColorDifferenceAveragecd The new value of the boolean
     * controlling the condition used to determine if the pin is standing
     */
    public final void setColorDifferenceAveraged(boolean isColorDifferenceAveraged)
    {
        this.isColorDifferenceAveraged = isColorDifferenceAveraged;
    }
    
    /**
     * Sets the green channel difference threshold of the target
     * 
     * If the intensity of the green channel is greater than gDifferenceThreshold
     * away from gIntensitySetpoint, then the targeted pin is not considered
     * standing.
     * 
     * @param gDifferenceThreshold The new green channel difference threshold - it must be between 0 and 255 inclusive
     */
    public final void setGDifferenceThreshold(int gDifferenceThreshold)
    {
        if (gDifferenceThreshold < 0 || gDifferenceThreshold > 255)
        {
            throw new IllegalArgumentException("gDifferenceThreshold value of " + gDifferenceThreshold + "is out of the acceptable range of 0 to 255 inclusive.");
        }
        
        this.gDifferenceThreshold = gDifferenceThreshold;
    }
    
    /**
     * Sets the green channel intensity setpoint of the target
     * 
     * If the intensity of the green channel is greater than gDifferenceThreshold
     * away from gIntensitySetpoint, then the targeted pin is not considered
     * standing.
     * 
     * @param gIntensitySetpoint The new green channel intensity setpoint - it must be between 0 and 255 inclusive.
     */
    public final void setGIntensitySetpoint(int gIntensitySetpoint)
    {
        if (gIntensitySetpoint < 0 || gIntensitySetpoint > 255)
        {
            throw new IllegalArgumentException("gIntensitySetpoint value of " + gIntensitySetpoint + "is out of the acceptable range of 0 to 255 inclusive.");
        }
        
        this.gIntensitySetpoint = gIntensitySetpoint;
    }
    
    /**
     * Sets the target pin's number (a BowlingPins enum value)
     * 
     * @parameter pin The new pin number
     */
    public final void setPin(BowlingPins pin)
    {
        this.pin = pin;
    }
    
    /**
     * Sets the red channel difference threshold of the target
     * 
     * If the intensity of the red channel is greater than rDifferenceThreshold
     * away from rIntensitySetpoint, then the targeted pin is not considered
     * standing.
     * 
     * @param rDifferenceThreshold The new red channel difference threshold - it must be between 0 and 255 inclusive.
     */
    public final void setRDifferenceThreshold(int rDifferenceThreshold)
    {
        if (rDifferenceThreshold < 0 || rDifferenceThreshold > 255)
        {
            throw new IllegalArgumentException("rDifferenceThreshold value of " + rDifferenceThreshold + "is out of the acceptable range of 0 to 255 inclusive.");
        }
        
        this.rDifferenceThreshold = rDifferenceThreshold;
    }
    
    /**
     * Sets the red channel intensity setpoint of the target
     * 
     * If the intensity of the red channel is greater than rDifferenceThreshold
     * away from rIntensitySetpoint, then the targeted pin is not considered
     * standing.
     * 
     * @param rIntensitySetpoint The new red channel intensity setpoint - it must be between 0 and 255 inclusive.
     */
    public final void setRIntensitySetpoint(int rIntensitySetpoint)
    {
        if (rIntensitySetpoint < 0 || rIntensitySetpoint > 255)
        {
            throw new IllegalArgumentException("rIntensitySetpoint value of " + rIntensitySetpoint + "is out of the acceptable range of 0 to 255 inclusive.");
        }
        
        this.rIntensitySetpoint = rIntensitySetpoint;
    }
    
    /**
     * Sets the radius describing the number of pixels around the target's
     * x and y position.
     * 
     * A radius of "1" contains only the pixel at the chosen position.
     * A radius of "2" is a 3x3 square of pixels centered at the chosen position.
     * A radius of "3" is a 5x5 square of pixels centered at the chosen position.
     * etc.
     * 
     * If pixels in the radius exceed the camera image's boundaries, then they
     * are not taken into consideration.
     * 
     * @param radius The new size of the square radius describing what pixels
     * around the chosen position to check
     */
    public final void setRadius(int radius)
    {
        if (radius <= 0)
        {
            throw new IllegalArgumentException("radius value of " + radius + " is not greater than 0.");
        }
        
        this.radius = radius;
    }
    
    /**
     * Sets the x position of the target's center pixel
     * 
     * @param xPosition The new x position of the target's center pixel - it must be greater than or equal to 0.
     */
    public final void setXPosition(int xPosition)
    {
        if (xPosition < 0)
        {
            throw new IllegalArgumentException("xPosition value of " + xPosition + " is not greater than or equal to 0.");
        }
        
        this.xPosition = xPosition;
    }
    
    /**
     * Sets the y position of the target's center pixel
     * 
     * @param yPosition The new y position of the target's center pixel - it must be greater than or equal to 0.
     */
    public final void setYPosition(int yPosition)
    {
        if (yPosition < 0)
        {
            throw new IllegalArgumentException("yPosition value of " + yPosition + " is not greater than or equal to 0.");
        }
        
        this.yPosition = yPosition;
    }
}
