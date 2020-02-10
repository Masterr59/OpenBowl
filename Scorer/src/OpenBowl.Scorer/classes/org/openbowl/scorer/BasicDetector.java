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

import com.pi4j.io.gpio.GpioController;
import com.pi4j.io.gpio.GpioFactory;
import com.pi4j.io.gpio.GpioPinDigitalInput;
import com.pi4j.io.gpio.Pin;
import com.pi4j.io.gpio.PinPullResistance;
import com.pi4j.io.gpio.RaspiPin;
import java.util.Map;
import java.util.prefs.Preferences;

/**
 *
 * @author Open Bowl <http://www.openbowlscoring.org/>
 */
public class BasicDetector extends Detector {

    private GpioController gpio;
    private GpioPinDigitalInput pin;
    private boolean isPi;
    private String name;
    private Preferences prefs;

    public BasicDetector(String detectorName) {
        isPi = RaspberryPiDetect.isPi();
        name = detectorName;
        prefs = Preferences.userNodeForPackage(this.getClass());
        if (isPi) {
            gpio = GpioFactory.getInstance();

            String gpioPinName = prefs.get(name + "PinName", "GPIO 31");
            Pin gpioPinNumber = RaspiPin.getPinByName(gpioPinName);

            String gpioResistance = prefs.get(name + "PinResistance", "PULL_UP");
            PinPullResistance resistance = PinPullResistance.valueOf(gpioResistance);
            try {
                pin = gpio.provisionDigitalInputPin(gpioPinNumber, resistance);
            } catch (Exception e) {
                System.out.println(e.toString());
            }
        } else {
            gpio = null;
            pin = null;
        }

    }

    @Override
    public void configureDialog() {
        fireDetectedEvent();
        //throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void setConfiguration(Map<String, Object> configuration) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Map<String, Object> getConfiguration() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

}
