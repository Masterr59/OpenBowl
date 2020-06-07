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
import java.io.IOException;
import java.util.ArrayList;

/**
 *
 * @author Open Bowl <http://www.openbowlscoring.org/>
 */
public abstract class SystemHandler extends CommonHandler {

    private final String LINUX_REBOOT_CHECK = "/var/run/reboot-required";
    private final String LINUX_UPDATE_CHECK_COMMAND = "apt list --upgradeable";
    private final String LINUX_NO_UPDATE = "Listing... Done";
    private final String PI_UNDERVOLT_COMMAND = "cat /var/log/syslog | grep voltage";
    private final String PI_TEMP_COMMAND = "vcgencmd measure_temp";

    protected abstract void onQuit();

    protected ArrayList<SystemStatus> getSystemStatus() {
        ArrayList<SystemStatus> status = new ArrayList<>();
        status.add(SystemStatus.ONLINE);
        if (System.getProperty("os.name").equals("Linux")) {
            File rebootCheck = new File(LINUX_REBOOT_CHECK);
            if (rebootCheck.exists()) {
                status.add(SystemStatus.REBOOT_REQUIRED);
            }//end reboot required
            try {
                Process p = Runtime.getRuntime().exec(LINUX_UPDATE_CHECK_COMMAND);
                int exitValue = p.waitFor();
                if (exitValue == 0) {
                    String stdout = new String(p.getInputStream().readAllBytes());
                    if (!stdout.trim().equals(LINUX_NO_UPDATE)) {
                        status.add(SystemStatus.UPDATE_AVAILABLE);
                    }
                }
            } catch (IOException | InterruptedException ex) {
                System.out.println("Get System Status ERROR - Update Check");
                status.add(SystemStatus.ERROR);
                status.add(SystemStatus.CRASH_DETECTED);
            }//end update check
            if (isPi()) {
                try {
                    Process p = Runtime.getRuntime().exec(PI_UNDERVOLT_COMMAND);
                    int exitValue = p.waitFor();
                    if (exitValue == 0) {
                        String stdout = new String(p.getInputStream().readAllBytes());
                        if (!stdout.trim().isEmpty()) {
                            status.add(SystemStatus.ERROR);
                            status.add(SystemStatus.UNDERVOLT);
                        }
                    }
                } catch (IOException | InterruptedException ex) {
                    System.out.println("Get System Status ERROR - Undervolt Check");
                    status.add(SystemStatus.ERROR);
                    status.add(SystemStatus.CRASH_DETECTED);
                }//end undervolt check
                try {
                    Process p = Runtime.getRuntime().exec(PI_TEMP_COMMAND);
                    int exitValue = p.waitFor();
                    if (exitValue == 0) {
                        String stdout = new String(p.getInputStream().readAllBytes());
                        if (stdout.trim().startsWith("temp=") && stdout.trim().endsWith("'C")) {
                            String stringTemp = stdout.trim().substring(5, stdout.trim().length() - 2);
                            //System.out.println(stringTemp);
                            double temp = Double.parseDouble(stringTemp);
                            if (temp > 75) {
                                status.add(SystemStatus.ERROR);
                                status.add(SystemStatus.OVERHEAT);
                            }
                        }
                    }
                } catch (IOException | InterruptedException | NumberFormatException ex) {
                    System.out.println("Get System Status ERROR - OverHeat Check");
                    status.add(SystemStatus.ERROR);
                    status.add(SystemStatus.CRASH_DETECTED);
                }//end temp check
            }//end is pi

        }//end linux os
        return status;
    }

    private boolean isPi() {
        if (System.getProperty("os.name").equals("Linux")) {
            if (System.getProperty("os.arch").equals("x86") || System.getProperty("os.arch").equals("amd64")) {
                return false;
            }
            return true;
        }
        return false;
    }

}
