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

package org.openbowl.multikeyboardlibrarydemo;

import org.openbowl.multikeyboardlibrary.MultiKeyboardInputReader;
import org.openbowl.multikeyboardlibrary.KeyboardIdentificationData;
import org.openbowl.multikeyboardlibrary.KeyData;

/**
 * @author Open Bowl <http://www.openbowlscoring.org/>
 */
public class MultiKeyboardLibraryDemo
{
    /**
     * Runs a demo of basic MultiKeyboardInputReader functionality.
     * 
     * This can be executed with the "runDemo" script (contained in the same directory)
     * 
     * @param args A string (required, either "true" or "false") indicating whether or not non-key input data should be returned to device queues, and a list of integers (optional) representing the event numbers to poll. If there are no integer arguments, all devices returned from getKeyboardIdentificationData will all be polled by default.
     * 
     * @throws InterruptedException if Thread.sleep(long millis) fails
     */
    public static void main(String[] args) throws InterruptedException
    {
        MultiKeyboardInputReader testMultiKeyboardInputReader = new MultiKeyboardInputReader();
        KeyboardIdentificationData[] testIdentArray = MultiKeyboardInputReader.getKeyboardIdentificationData();

        boolean isEventNumberManuallySelected = false;
        int[] commandLineEventNumberArray = new int[1]; // Initialized so that the compiler doesn't complain

        // Determine if the user gave correct command-line arguments or not
        if (args.length == 1)
        {
            if (args[0].equals("true"))
            {
                testMultiKeyboardInputReader.setNonKeyInputReturned(true);
            }
            else if (args[0].equals("false"))
            {
                testMultiKeyboardInputReader.setNonKeyInputReturned(false);
            }
            else
            {
                System.out.println("Error: The first command-line argument must be \"true\" or \"false\"");
                return;
            }
        }
        else if (args.length >= 2)
        {
            isEventNumberManuallySelected = true;

            if (args[0].equals("true"))
            {
                testMultiKeyboardInputReader.setNonKeyInputReturned(true);
            }
            else if (args[0].equals("false"))
            {
                testMultiKeyboardInputReader.setNonKeyInputReturned(false);
            }
            else
            {
                System.out.println("Error: The first command-line argument must be \"true\" or \"false\"");
                return;
            }

            commandLineEventNumberArray = new int[args.length - 1];

            for (int i = 1; i < args.length; i++)
            {
                try
                {
                    commandLineEventNumberArray[i - 1] = Integer.parseInt(args[i]);
                }
                catch (NumberFormatException e)
                {
                    System.out.println("Error: Command-line argument number " + (i + 1) + " (\"" + args[i] + "\") was not an integer");
                    return;
                }
            }
        }
        else
        {
            System.out.println("Error: One or more command-line arguments are required\nThe first must be \"true\" or \"false\", and any arguments after that (if any) must be integers");
            return;
        }

        if (testIdentArray == null)
        {
            System.out.println("Error: getKeyboardIdentificationData returned null");
            return;
        }

        System.out.println("Keyboard identification data\n");

        if (isEventNumberManuallySelected)
        {
            for (KeyboardIdentificationData keyboardIdentificationData : testIdentArray)
            {
                System.out.println("Name: " + keyboardIdentificationData.getName() + "\nDevice event number: " + keyboardIdentificationData.getDeviceEventNumber() + "\nPhys field: " + keyboardIdentificationData.getPhys() + "\n");
            }
            
            for (int eventNumber : commandLineEventNumberArray)
            {
                testMultiKeyboardInputReader.addPolledKeyboard(eventNumber);
            }
        }
        else
        {
            for (KeyboardIdentificationData keyboardIdentificationData : testIdentArray)
            {
                System.out.println("Name: " + keyboardIdentificationData.getName() + "\nDevice event number: " + keyboardIdentificationData.getDeviceEventNumber() + "\nPhys field: " + keyboardIdentificationData.getPhys() + "\n");
                testMultiKeyboardInputReader.addPolledKeyboard(keyboardIdentificationData.getDeviceEventNumber());
            }
        }

        int[] testPolledDeviceEventNumberList = testMultiKeyboardInputReader.getPolledKeyboardDeviceEventNumberList();

        System.out.println("\nDevice event numbers to be polled:\n");

        for (int deviceEventNumber : testPolledDeviceEventNumberList)
        {
            System.out.println(deviceEventNumber);
        }

        System.out.println();

        testMultiKeyboardInputReader.startPollingInputs();

        KeyData currentKeyData;

        for (int i = 0; i < 40; i++)
        {
            for (int j = 0; j < testPolledDeviceEventNumberList.length; j++)
            {
                while (!testMultiKeyboardInputReader.isDeviceInputQueueEmpty(testPolledDeviceEventNumberList[j]))
                {
                    currentKeyData = testMultiKeyboardInputReader.removeDeviceEventQueueElement(testPolledDeviceEventNumberList[j]);
                    
                    System.out.println("Received input event from device element j = " + j + " (event " + testPolledDeviceEventNumberList[j] + ", " + testIdentArray[j].getName() + ")\nKey " + currentKeyData.getKeyCode() + " was " + currentKeyData.getKeyState() + "'ed!\n");
                }

            }

            Thread.sleep(250);
        }

        testMultiKeyboardInputReader.stopPollingInputs();
    }
}
