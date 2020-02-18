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
import java.util.HashMap;
import java.util.Map;
import java.util.prefs.Preferences;

/**
 *
 * @author Open Bowl <http://www.openbowlscoring.org/>
 */
public class BasicPinSetter implements PinSetter {

    final static String POWER_PIN_SETTING_NAME = "PowerName";
    final static String CYCLE_PIN_SETTING_NAME = "CycleName";
    final static String POWER_STATE_SETTING_NAME = "PowerState";
    final static String CYCLE_STATE_SETTING_NAME = "CycleState";
    final static String CYCLE_DELAY_SETTING_NAME = "CycleDelay";
    final static String DEFAULT_SETTING_POWER_PIN = "GPIO 7";
    final static String DEFAULT_SETTING_CYCLE_PIN = "GPIO 0";
    final static String DEFAULT_SETTING_POWER_STATE = "HIGH";
    final static String DEFAULT_SETTING_CYCLE_STATE = "HIGH";
    final static long DEFAULT_SETTING_CYCLE_DELAY = 100;

    private GpioController gpioController;
    private GpioPinDigitalOutput powerPin, cyclePin;
    private final PinState defaultPowerState;
    private final PinState defaultCycleState;
    private final boolean isPi;
    private final String name;
    private final Preferences prefs;

    public BasicPinSetter(String name) {
        this.name = name;
        isPi = RaspberryPiDetect.isPi();
        prefs = Preferences.userNodeForPackage(this.getClass());
        String powerPinState = prefs.get(name + POWER_STATE_SETTING_NAME, DEFAULT_SETTING_POWER_STATE);
        defaultPowerState = PinState.valueOf(powerPinState);
        String cyclePinState = prefs.get(name + CYCLE_STATE_SETTING_NAME, DEFAULT_SETTING_CYCLE_STATE);
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
    public String setConfiguration(Map<String, Object> newConfig) {
        String results = "";
        Map<String, Object> oldConfig = getConfiguration();
        try {
            String type = (String) newConfig.get("Type");
            String powerPinName = (String) newConfig.get(POWER_PIN_SETTING_NAME);
            String cyclePinName = (String) newConfig.get(CYCLE_PIN_SETTING_NAME);
            String powerPinState = (String) newConfig.get(POWER_STATE_SETTING_NAME);
            String cyclePinState = (String) newConfig.get(CYCLE_STATE_SETTING_NAME);
            long delay = (long) newConfig.get(CYCLE_DELAY_SETTING_NAME);

            if (type.equals(this.getClass().getName())) {
                teardown();
                prefs.put(name + BasicPinSetter.POWER_PIN_SETTING_NAME, powerPinName);
                prefs.put(name + BasicPinSetter.POWER_STATE_SETTING_NAME, powerPinState);
                prefs.put(name + BasicPinSetter.CYCLE_PIN_SETTING_NAME, cyclePinName);
                prefs.put(name + BasicPinSetter.CYCLE_STATE_SETTING_NAME, cyclePinState);
                prefs.putLong(name + BasicPinSetter.CYCLE_DELAY_SETTING_NAME, delay);
                results += setup();
            } else {
                results += "Incorrect pin setter type";
            }
        } catch (ClassCastException e) {
            results += e.getMessage();
        }

        return results;
    }

    @Override
    public Map<String, Object> getConfiguration() {
        String powerPinName = prefs.get(name + POWER_PIN_SETTING_NAME, DEFAULT_SETTING_POWER_PIN);
        String cyclePinName = prefs.get(name + CYCLE_PIN_SETTING_NAME, DEFAULT_SETTING_CYCLE_PIN);
        String powerPinState = prefs.get(name + POWER_STATE_SETTING_NAME, DEFAULT_SETTING_POWER_STATE);
        String cyclePinState = prefs.get(name + CYCLE_STATE_SETTING_NAME, DEFAULT_SETTING_CYCLE_STATE);
        long delay = prefs.getLong(name + CYCLE_DELAY_SETTING_NAME, DEFAULT_SETTING_CYCLE_DELAY);

        Map<String, Object> ret = new HashMap<>();
        ret.put("Type", this.getClass().getName());
        ret.put(POWER_PIN_SETTING_NAME, powerPinName);
        ret.put(POWER_STATE_SETTING_NAME, powerPinState);
        ret.put(CYCLE_PIN_SETTING_NAME, cyclePinName);
        ret.put(CYCLE_STATE_SETTING_NAME, cyclePinState);
        ret.put(CYCLE_DELAY_SETTING_NAME, delay);

        return ret;
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
        long delay = prefs.getLong(name + CYCLE_DELAY_SETTING_NAME, DEFAULT_SETTING_CYCLE_DELAY);

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

            String powerPinName = prefs.get(name + POWER_PIN_SETTING_NAME, DEFAULT_SETTING_POWER_PIN);
            try {
                powerPin = gpioController.provisionDigitalOutputPin(RaspiPin.getPinByName(powerPinName), POWER_PIN_SETTING_NAME, defaultPowerState);

                String cyclePinName = prefs.get(name + CYCLE_PIN_SETTING_NAME, DEFAULT_SETTING_CYCLE_PIN);

                cyclePin = gpioController.provisionDigitalOutputPin(RaspiPin.getPinByName(cyclePinName), CYCLE_PIN_SETTING_NAME, defaultCycleState);
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
