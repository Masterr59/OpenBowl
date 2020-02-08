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
import javafx.stage.Modality;

/**
 *
 * @author Open Bowl <http://www.openbowlscoring.org/>
 */
public class BasicPinSetter implements PinSetter {

    private GpioController gpioController;
    private GpioPinDigitalOutput powerPin, cyclePin;
    private PinState defaultPowerState, defaultCycleState;
    private boolean isPi;
    private String name;
    private Preferences prefs;

    public BasicPinSetter(String name) {
        this.name = name;
        isPi = RaspberryPiDetect.isPi();
        prefs = Preferences.userNodeForPackage(this.getClass());

        if (isPi) {
            gpioController = GpioFactory.getInstance();

            String powerPinName = prefs.get(name + "PinName", "GPIO_07");
            String powerPinState = prefs.get(name + "PowerState", "HIGH");
            defaultPowerState = PinState.valueOf(powerPinState);

            powerPin = gpioController.provisionDigitalOutputPin(RaspiPin.getPinByName(powerPinName), "PowerPin", defaultPowerState);

            String cyclePinName = prefs.get(name + "cycleName", "GPIO_00");
            String cyclePinState = prefs.get(name + "cycleState", "HIGH");
            defaultCycleState = PinState.valueOf(cyclePinState);

            cyclePin = gpioController.provisionDigitalOutputPin(RaspiPin.getPinByName(cyclePinName), "CyclePin", defaultCycleState);

        } else {

        }

    }

    @Override
    public void configureDialog() {
        try {
            BasicPinSetterOptionsController dialog = new BasicPinSetterOptionsController();
            dialog.showAndWait();
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
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public boolean getPowerState() {
        return powerPin.getState() != defaultPowerState;
    }

    @Override
    public void cycle() {
        //default 1/10 second
        long delay = prefs.getLong(name + "CycleDelay", 100);

        //seperate thread for sleep
        Thread t = new Thread() {
            public void run() {
                try {
                    PinState onState = (defaultCycleState == PinState.HIGH) ? PinState.LOW : PinState.HIGH;
                    cyclePin.setState(onState);
                    Thread.sleep(delay);
                    cyclePin.setState(defaultCycleState);

                } catch (InterruptedException e) {
                    cyclePin.setState(defaultCycleState);
                }
            }
        };
        t.start();

    }

}
