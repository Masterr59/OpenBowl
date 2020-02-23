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

import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author Open Bowl <http://www.openbowlscoring.org/>
 */
public class FakePinSetter implements PinSetter{
    private String desc = "Virtual(Fake) Pinsetter";
    private boolean power = false;
    
    @Override
    public void configureDialog() {
        log("show configuration dialog");
    }

    @Override
    public String setConfiguration(Map<String, Object> configuration) {
        log("set configuration");
        return "";
    }

    @Override
    public Map<String, Object> getConfiguration() {
        Map<String, Object> map = new HashMap<>();
        map.put("Type", this.getClass().getName());
        log("get configuration");
        return map;
    }

    @Override
    public void setPower(boolean state) {
        log("set power state: " + state);
        power = state;
    }

    @Override
    public boolean getPowerState() {
        log("get power state: " + power);
        return power;
    }

    @Override
    public void cycle() {
        log("cycle");
    }

    @Override
    public String setup() {
        log("setup");
        return "";
    }

    @Override
    public void teardown() {
        log("teardown");
        
    }

    private void log(String s){
        System.out.println(desc + " - " + s);
    }
    
}
