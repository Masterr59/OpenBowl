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
import com.pi4j.io.gpio.PinState;
import com.pi4j.io.gpio.RaspiPin;
import com.pi4j.io.gpio.event.GpioPinDigitalStateChangeEvent;
import com.pi4j.io.gpio.event.GpioPinListenerDigital;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.prefs.Preferences;

/**
 *
 * @author Open Bowl <http://www.openbowlscoring.org/>
 */
public class BasicDetector extends Detector {

    public final String PIN_SETTING = "PinName";
    public final String RESISTANCE_SETTING = "PinResistance";
    public final String TRIGGER_SETTING = "TriggerState";

    public final String DEFAULT_PIN = "GPIO 31";
    public final String DEFAULT_RESISTANCE = "PULL_UP";
    public final String DEFAULT_TRIGGER = "HIGH";

    private GpioController gpio;
    private GpioPinDigitalInput pin;
    private final boolean isPi;
    private final String name;
    private final Preferences prefs;

    public BasicDetector(String detectorName) {
        isPi = RaspberryPiDetect.isPi();
        name = detectorName;
        prefs = Preferences.userNodeForPackage(this.getClass());
        setup();

    }

    @Override
    public void configureDialog() {
        try {
            RaspberryPiDetect rpi = new RaspberryPiDetect();
            rpi.onShowPinout("Raspberry Pi", 3);
            BasicDetectorOptionsController dialog = new BasicDetectorOptionsController(name, this);
            dialog.setTitle(name);
            dialog.showAndWait();
            rpi.closeAlert();

        } catch (IOException e) {
            System.out.println("Error showing dialog " + e.toString());
        }
    }

    @Override
    public String setConfiguration(Map<String, Object> configuration) {
        String results = "";
        try {
            String type = (String) configuration.get("Type");
            String newpin = (String) configuration.get(PIN_SETTING);
            String resist = (String) configuration.get(RESISTANCE_SETTING);
            String trigger = (String) configuration.get(TRIGGER_SETTING);

            if (type.equals(this.getClass().getName())) {
                teardown();
                prefs.put(name + PIN_SETTING, newpin);
                prefs.put(name + RESISTANCE_SETTING, resist);
                prefs.put(name + TRIGGER_SETTING, trigger);
                results += setup();
            } else {
                results += "Incorrect device type";
            }
        } catch (ClassCastException e) {
            results += e.getMessage();
        } catch (NullPointerException e) {
            results += "NullPointException: " + e.getMessage();
        }
        log("Set configuration: " + results);
        return results;
    }

    @Override
    public Map<String, Object> getConfiguration() {
        log("Get Configuration called");
        Map<String, Object> ret = new HashMap<>();
        ret.put("Type", this.getClass().getName());
        ret.put(PIN_SETTING, prefs.get(name + PIN_SETTING, DEFAULT_PIN));
        ret.put(RESISTANCE_SETTING, prefs.get(name + RESISTANCE_SETTING, DEFAULT_RESISTANCE));
        ret.put(TRIGGER_SETTING, prefs.get(name + TRIGGER_SETTING, DEFAULT_TRIGGER));
        return ret;
    }

    private void onPinStateChange(GpioPinDigitalStateChangeEvent event) {
        String PinTrigger = prefs.get(name + TRIGGER_SETTING, DEFAULT_TRIGGER);
        PinState pinState = PinState.valueOf(PinTrigger);
        if (pinState == event.getState()) {
            fireDetectedEvent();
            //System.out.println(" --> GPIO PIN STATE CHANGE: " + event.getPin() + " = " + event.getState());
            //System.out.println("Edge " + event.getEdge().getName());
        }

    }

    @Override
    public String setup() {
        String ret = "";
        if (isPi) {
            gpio = GpioFactory.getInstance();

            String gpioPinName = prefs.get(name + PIN_SETTING, DEFAULT_PIN);
            Pin gpioPinNumber = RaspiPin.getPinByName(gpioPinName);

            String gpioResistance = prefs.get(name + RESISTANCE_SETTING, DEFAULT_RESISTANCE);
            PinPullResistance resistance = PinPullResistance.valueOf(gpioResistance);
            try {
                pin = gpio.provisionDigitalInputPin(gpioPinNumber, resistance);
                pin.addListener((GpioPinListenerDigital) (GpioPinDigitalStateChangeEvent event) -> {
                    onPinStateChange(event);
                });
            } catch (Exception e) {
                ret = e.toString();
                System.out.println(e.toString());
            }
        } else {
            ret = "Not a pi";
            gpio = null;
            pin = null;
        }
        return ret;
    }

    @Override
    public void teardown() {
        if (isPi && pin != null) {
            gpio.unprovisionPin(pin);
        }
    }
}
