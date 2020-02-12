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
import com.pi4j.io.gpio.GpioPinDigitalOutput;
import com.pi4j.io.gpio.PinState;
import com.pi4j.io.gpio.RaspiPin;
import java.io.IOException;
import java.util.Map;
import java.util.prefs.Preferences;

/**
 *
 * @author Open Bowl <http://www.openbowlscoring.org/>
 */
public class BasicPinSetter implements PinSetter {

    private GpioController gpioController;
    private GpioPinDigitalOutput powerPin, cyclePin;
    private PinState defaultPowerState, defaultCycleState;
    private final boolean isPi;
    private final String name;
    private final Preferences prefs;

    public BasicPinSetter(String name) {
        this.name = name;
        isPi = RaspberryPiDetect.isPi();
        prefs = Preferences.userNodeForPackage(this.getClass());
        String powerPinState = prefs.get(name + "PowerState", "HIGH");
        defaultPowerState = PinState.valueOf(powerPinState);
        String cyclePinState = prefs.get(name + "cycleState", "HIGH");
        defaultCycleState = PinState.valueOf(cyclePinState);
        setup();

    }

    @Override
    public void configureDialog() {
        try {
            RaspberryPiDetect rpi = new RaspberryPiDetect();
            rpi.onShowPinout("Raspberry Pi", 3);
            BasicPinSetterOptionsController dialog = new BasicPinSetterOptionsController(name, this);
            dialog.setTitle(name);
            dialog.showAndWait();
            rpi.closeAlert();

        } catch (IOException e) {
            System.out.println("Error showing dialog " + e.toString());
        }
    }

    @Override
    public void setConfiguration(Map<String, Object> configuration) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Map<String, Object> getConfiguration() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void setPower(boolean state) {
        //System.out.println("Power OldState: "  + powerPin.getState().getName() + " New State: " + PinState.getInverseState(defaultPowerState).toString());
        if (state) {
            powerPin.setState(PinState.getInverseState(defaultPowerState));
        } else {
            powerPin.setState(defaultPowerState);
        }
    }

    @Override
    public boolean getPowerState() {
        if (isPi) {
            return powerPin.getState() != defaultPowerState;
        }
        return false;
    }

    @Override
    public void cycle() {
        //default 1/10 second
        long delay = prefs.getLong(name + "CycleDelay", 100);

        //seperate thread for sleep
        Thread t = new Thread() {
            @Override
            public void run() {
                try {
                    PinState onState = (defaultCycleState == PinState.HIGH) ? PinState.LOW : PinState.HIGH;
                    if (isPi) {
                        cyclePin.setState(onState);
                    }
                    Thread.sleep(delay);
                    if (isPi) {
                        cyclePin.setState(defaultCycleState);
                    }

                } catch (InterruptedException e) {
                    cyclePin.setState(defaultCycleState);
                }
            }
        };
        t.start();

    }

    @Override
    public String setup() {
        String ret = "";
        if (isPi) {
            gpioController = GpioFactory.getInstance();

            String powerPinName = prefs.get(name + "PowerName", "GPIO 7");
            try {
                powerPin = gpioController.provisionDigitalOutputPin(RaspiPin.getPinByName(powerPinName), "PowerPin", defaultPowerState);

                String cyclePinName = prefs.get(name + "cycleName", "GPIO 0");

                cyclePin = gpioController.provisionDigitalOutputPin(RaspiPin.getPinByName(cyclePinName), "CyclePin", defaultCycleState);
            } catch (Exception e) {
                ret = e.toString();
                System.out.println(e.toString());
            }
        } else {
            ret = "Not a pi";
        }
        return ret;
    }

    @Override
    public void teardown() {
        if (isPi && powerPin != null) {
            gpioController.unprovisionPin(powerPin);
        }
        if (isPi && cyclePin != null) {
            gpioController.unprovisionPin(cyclePin);
        }
    }

}
