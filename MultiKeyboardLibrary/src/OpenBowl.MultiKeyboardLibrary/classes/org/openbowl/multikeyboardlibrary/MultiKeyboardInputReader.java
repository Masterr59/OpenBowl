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
package org.openbowl.multikeyboardlibrary;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Queue;

/**
 * Provides methods for obtaining input from multiple keyboard devices and
 * distinguishing which keyboard the input came from.
 * 
 * @author Open Bowl <http://www.openbowlscoring.org/>
 */
public class MultiKeyboardInputReader implements Runnable
{
    private boolean isInputBeingPolled;
    private final ArrayList<Keyboard> polledKeyboardList;
    
    /**
      * Gets a list of KeyboardIdentificationData objects for each device that is
      * most likely a keyboard (as determined by /proc/input/bus/devices EV flags).
      *
      * Some hard-coding (checking returned device names) should still be done on
      * a per-hardware basis to verify whether or not a returned device is
      * actually a keyboard. Each returned string contains the device name and
      * the device's corresponding event number in /dev/input
      *
      * @return a list of KeyboardIdentificationData objects for each device
      * that is most likely a keyboard, or null if an issue occurred but an
      * exception could not be thrown
      * 
      * @throws IOException if /proc/bus/input/devices is unable to be opened
      * @throws JNIException if an issue has occurred in native code pertaining to JNI API calls or other native functions
      */
    public static native KeyboardIdentificationData[] getKeyboardIdentificationData();
    
    private native boolean pollInputs();

    public MultiKeyboardInputReader()
    {
        /*
         * Very important note
	 * 
	 * When told to load "X", it will look for "libX.so" on Unix-like platforms.
	 * 
	 * If it does not find "libX.so", it will throw an UnsatisfiedLinkError
	 * and tell you that it couldn't find "X" (not that it couldn't find "libX.so").
         */
        System.loadLibrary("multikeyboardinputreader");

        isInputBeingPolled = false;
        polledKeyboardList = new ArrayList<>(0);
    }

    /**
     * Runs a demo of basic MultiKeyboardInputReader functionality.
     * 
     * This can be executed with the "runDemo" script (contained in the same directory)
     * 
     * @param args Unused
     * 
     * @throws InterruptedException if Thread.sleep(long millis) fails
     */
    public static void main(String[] args) throws InterruptedException
    {
        MultiKeyboardInputReader testMultiKeyboardInputReader = new MultiKeyboardInputReader();
        KeyboardIdentificationData[] testIdentArray = MultiKeyboardInputReader.getKeyboardIdentificationData();

        System.out.println("Keyboard identification data\n");

        for (KeyboardIdentificationData keyboardIdentificationData : testIdentArray)
        {
            System.out.println("Name: " + keyboardIdentificationData.getName() + "\nDevice event number: " + keyboardIdentificationData.getDeviceEventNumber() + "\nPhys field: " + keyboardIdentificationData.getPhys() + "\n");
            testMultiKeyboardInputReader.addPolledKeyboard(keyboardIdentificationData.getDeviceEventNumber());
        }
        
        int[] testPolledDeviceEventNumberList = testMultiKeyboardInputReader.getPolledKeyboardDeviceEventNumberList();
        
        System.out.println("\nDevice event numbers to be polled:\n");

        for (int i = 0; i < testPolledDeviceEventNumberList.length; i++)
        {
            System.out.println(testPolledDeviceEventNumberList[i]);
        }

        System.out.println();

        testMultiKeyboardInputReader.startPollingInputs();

        KeyData currentKeyData;

        for (int i = 0; i < 40; i++)
        {
            for (int j = 0; j < testPolledDeviceEventNumberList.length; j++)
            {
                while (true)
                {
                    currentKeyData = testMultiKeyboardInputReader.polledKeyboardList.get(j).getInputQueue().poll();

                    if (currentKeyData == null)
                    {
                        break;
                    }

                    System.out.println("Device element j = " + j + " (event " + testPolledDeviceEventNumberList[j] + "): Key " + currentKeyData.getKeyCode() + " was " + currentKeyData.getKeyState() + "'ed!");
                }

            }

            Thread.sleep(250);
        }

        testMultiKeyboardInputReader.stopPollingInputs();
    }

    /**
     * Adds a device event number to the polled keyboard list.
     * 
     * Adding event numbers not returned by getKeyboardIdentificationData() will result in unspecified behavior.
     * Event numbers added while input is being polled will only go into effect on the next successful call to startPollingInputs().
     *
     * @param deviceEventNumber The new device event number in /dev/input to be
     * polled for input
     *
     * @return true if the event number was added to the list, false if it was
     * not (usually due to already being in the list).
     * 
     * @throws <a href="">https://docs.oracle.com/javase/8/docs/api/java/lang/IllegalArgumentException.html</a> if deviceEventNumber is less than 0
     */
    public boolean addPolledKeyboard(int deviceEventNumber)
    {
        if (deviceEventNumber < 0)
        {
            throw new IllegalArgumentException("deviceEventNumber must be greater than or equal to 0");
        }

        Keyboard newKeyboard = new Keyboard(deviceEventNumber);

        if (polledKeyboardList.contains(newKeyboard))
        {
            return false;
        }
        else
        {
            return polledKeyboardList.add(newKeyboard);
        }
    }

    /**
     * Gets the list of device event numbers from the keyboard list that will be polled for input.
     *
     * @return The list of device event numbers that will be polled for input
     */
    public int[] getPolledKeyboardDeviceEventNumberList()
    {
        int[] deviceEventNumberList = new int[polledKeyboardList.size()];

        for (int i = 0; i < polledKeyboardList.size(); i++)
        {
            deviceEventNumberList[i] = polledKeyboardList.get(i).getDeviceEventNumber();
        }

        return deviceEventNumberList;
    }

    /**
     * Gets whether or not input is currently being polled.
     *
     * @return true if input is currently being polled, false if not
     */
    public boolean isInputBeingPolled()
    {
        return isInputBeingPolled;
    }

    /**
     * Removes a device event number from the keyboard list.
     *
     * @param deviceEventNumber The device event number to no longer be polled
     *
     * @return true if the event number was removed from the list, false if it
     * was not (usually due to not being in the list) or if input is currently
     * being polled
     */
    public boolean removePolledKeyboard(int deviceEventNumber)
    {
        if (isInputBeingPolled())
        {
            return false;
        }

        return polledKeyboardList.remove(new Keyboard(deviceEventNumber));
    }

    /**
     * Used by startPollingInputs (not intended to be publicly called)
     */
    @Override
    public void run()
    {
        pollInputs();
    }

    /**
     * Spawns a Java thread that begins polling for and returning inputs received
     * from all devices with event numbers in the keyboard list.
     * 
     * @return true if the thread was successfully spawned, false if input is
     * currently being polled
     */
    public boolean startPollingInputs()
    {
        if (isInputBeingPolled())
        {
            return false;
        }

        isInputBeingPolled = true;
        Thread inputPollingThread = new Thread(this);

        inputPollingThread.start();

        return true;
    }

    /**
     * Tells the thread spawned by startPollingInputs() to stop polling for and
     * returning inputs received from all devices with event numbers in the
     * keyboard list.
     *
     * If input is already not being polled, then nothing will happen (input
     * will still not be polled).
     */
    public void stopPollingInputs()
    {
        isInputBeingPolled = false;
    }

    // There isn't much reason to javadoc this internally-used nested class
    public class Keyboard
    {
        private final int deviceEventNumber;
        private final Queue<KeyData> inputQueue;

        public Keyboard(int deviceEventNumber)
        {
            if (deviceEventNumber < 0)
            {
                throw new IllegalArgumentException("deviceEventNumber must be greater than or equal to 0");
            }

            this.deviceEventNumber = deviceEventNumber;
            inputQueue = new LinkedList<>();
        }

        @Override
        public boolean equals(Object keyboard)
        {
            // Many Java library functions call this specific equals method, so there isn't a reasonable way out of instanceof
            if (keyboard instanceof Keyboard)
            {
                Keyboard castedKeyboard = (Keyboard) keyboard;
                return this.deviceEventNumber == castedKeyboard.getDeviceEventNumber();
            }

            return false;
        }

        public int getDeviceEventNumber()
        {
            return deviceEventNumber;
        }

        public Queue<KeyData> getInputQueue()
        {
            return inputQueue;
        }

        @Override
        public int hashCode()
        {
            return this.deviceEventNumber;
        }
    }
}