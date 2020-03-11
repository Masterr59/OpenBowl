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

/*
 * JNI EXPLANATIONS
 * 
 * 
 * https://www3.ntu.edu.sg/home/ehchua/programming/java/JavaNativeInterface.html is very useful for getting started with the JNI API.
 * 
 * It is also very useful to view jni.h and jni_md.h in a text editor so that you can view function signatures, typedefs, etc.
 * 
 * 
 * Function declaration/signature explanation:
 * 
 * JNIEXPORT sets the ABI-level visibility of the functions (platform-dependent)
 * JNICALL might modify the calling convention of the function (or possibly do nothing, platform-dependent)
 * 
 * The name format for functions is Java_packagename_ClassName_functionName.
 * 
 * For example, the function "Java_org_openbowl_multikeyboardlibrary_MultiKeyboardInputReader_getKeyboardIdentificationData"
 * is part of the package "org.openbowl.multikeyboardlibrary", is a native function of the Java class MultiKeyboardInputReader,
 * and the name of the function (as declared in MultiKeyboardInputReader.java) is getKeyboardIdentificationData.
 * 
 * JNIEnv *environment is the Java environment (which is also used to access all of the JNI-related functions)
 * jobject thisObject is the object equivalent to "this" in Java
 * 
 * 
 * Refer to the following links for explanation of GetMethodID
 *
 * Method description:
 * https://docs.oracle.com/javase/8/docs/technotes/guides/jni/spec/functions.html
 * 
 * Signature string format:
 * https://docs.oracle.com/javase/8/docs/technotes/guides/jni/spec/types.html
 * 
 * 
 * There are few JNI methods that don't always throw an error when returning NULL.
 * 
 * If any errors thrown by the JNI calls (which are generally serious enough to not catch) are caught,
 * the cascade of NULLs assigned to many values will inevitably error flood the user into submission.
 */

// Library version: Release

#include <errno.h>
#include <fcntl.h>
#include <poll.h>
#include <stdlib.h>
#include <unistd.h>
#include "org_openbowl_multikeyboardlibrary_MultiKeyboardInputReader.h"
#include "/usr/include/linux/input.h"

static unsigned int convertDecimalStringToUnsignedInteger(char inputString[], unsigned int inputStringLength);
static unsigned int convertHexStringToUnsignedInteger(char inputString[], unsigned int inputStringLength);
static jobject convertInputEventToKeyData(struct input_event inputEvent, JNIEnv *environment);

// jobjectArray is an array of KeyboardIdentificationData objects
JNIEXPORT jobjectArray JNICALL Java_org_openbowl_multikeyboardlibrary_MultiKeyboardInputReader_getKeyboardIdentificationData(JNIEnv *environment, jobject thisObject)
{	
	// Open /proc/bus/input/devices (to later get device entry data from) and throw an exception if something goes wrong
	FILE *procBusInputDevicesFile = NULL;
	
	if ((procBusInputDevicesFile = fopen("/proc/bus/input/devices", "r")) == NULL)
	{
		if ((*environment)->ThrowNew(environment, (*environment)->FindClass(environment, "java/io/IOException"), "Unable to open /proc/bus/input/devices for reading device data from") < 0)
		{
			return NULL;
		}
	}
	
	// Read all data (up to a maximum of 9999 chars) from /proc/bus/input/devices
	char deviceData[10000];
	size_t charsRead = fread(deviceData, 1, 9999, procBusInputDevicesFile); // Theoretical bug: If /proc/bus/input/devices has more than 9999 bytes of data, then the rest of the data after byte 9999 will be ignored
	
	deviceData[charsRead] = '\0';
	
	unsigned int deviceEntryCount = 0;
	
	// Count the number of device entries
	for (unsigned int i = 0; i < charsRead; i++)
	{
		if ((i + 5) < charsRead && deviceData[i] == 'B')
		{
			if (deviceData[i + 1] == ':')
			{
				if (deviceData[i + 2] == ' ')
				{
					if (deviceData[i + 3] == 'E')
					{
						if (deviceData[i + 4] == 'V')
						{
							if (deviceData[i + 5] == '=')
							{
								deviceEntryCount++;
							}
						}
					}
				}
			}
		}
	}
	
	unsigned int *deviceEventNumberArray = malloc(sizeof(int) * deviceEntryCount);
	
	if (deviceEventNumberArray == NULL)
	{
		if ((*environment)->ThrowNew(environment, (*environment)->FindClass(environment, "org/openbowl/multikeyboardlibrary/JNIException"), "Could not allocate memory for the device event number array") < 0)
		{
			return NULL;
		}
	}
	
	char **deviceNameArray = malloc(sizeof(char *) * deviceEntryCount);
	
	if (deviceNameArray == NULL)
	{
		if ((*environment)->ThrowNew(environment, (*environment)->FindClass(environment, "org/openbowl/multikeyboardlibrary/JNIException"), "Could not allocate memory for the device name array") < 0)
		{
			return NULL;
		}
	}
	
	char **devicePhysArray = malloc(sizeof(char *) * deviceEntryCount);
	
	if (devicePhysArray == NULL)
	{
		if ((*environment)->ThrowNew(environment, (*environment)->FindClass(environment, "org/openbowl/multikeyboardlibrary/JNIException"), "Could not allocate memory for the device phys array") < 0)
		{
			return NULL;
		}
	}
	
	for (unsigned int i = 0; i < deviceEntryCount; i++)
	{
		deviceNameArray[i] = malloc(sizeof(char) * 1000); // Theoretical bug: Name strings longer than 1000 characters will crash the program
		
		if (deviceNameArray[i] == NULL)
		{
			char exceptionMessageString[54]; // It is assumed that integers will take up to a maximum of 11 characters (10 digits and 1 negative sign)
			
			if (sprintf(exceptionMessageString, "Could not allocate memory for device name %i", i) < 0)
			{
				if ((*environment)->ThrowNew(environment, (*environment)->FindClass(environment, "org/openbowl/multikeyboardlibrary/JNIException"), "Could not generate the exception message string for device name memory allocation") < 0)
				{
					return NULL;
				}
			}
			
			if ((*environment)->ThrowNew(environment, (*environment)->FindClass(environment, "org/openbowl/multikeyboardlibrary/JNIException"), exceptionMessageString) < 0)
			{
				return NULL;
			}
		}
	}
	
	for (unsigned int i = 0; i < deviceEntryCount; i++)
	{
		devicePhysArray[i] = malloc(sizeof(char) * 1000); // Theoretical bug: Phys strings longer than 1000 characters will crash the program

		if (devicePhysArray[i] == NULL)
		{
			char exceptionMessageString[54]; // It is assumed that integers will take up to a maximum of 11 characters (10 digits and 1 negative sign)
			
			if (sprintf(exceptionMessageString, "Could not allocate memory for device phys %i", i) < 0)
			{
				if ((*environment)->ThrowNew(environment, (*environment)->FindClass(environment, "org/openbowl/multikeyboardlibrary/JNIException"), "Could not generate the exception message string for device phys memory allocation") < 0)
				{
					return NULL;
				}
			}
			
			if ((*environment)->ThrowNew(environment, (*environment)->FindClass(environment, "org/openbowl/multikeyboardlibrary/JNIException"), exceptionMessageString) < 0)
			{
				return NULL;
			}
		}
	}
	
	unsigned int keyboardEntryCount = 0;
	
	for (unsigned int i = 0; i < charsRead; i++)
	{
		// Look for string sequences that indicate the "Name" field in a device entry
		if ((i + 5) < charsRead && deviceData[i] == 'N')
		{
			if (deviceData[i + 1] == 'a')
			{
				if (deviceData[i + 2] == 'm')
				{
					if (deviceData[i + 3] == 'e')
					{
						if (deviceData[i + 4] == '=')
						{
							if (deviceData[i + 5] == '"')
							{
								// Store the "Name" field of the current device entry
								for (unsigned int j = 0; j + i + 6 < charsRead; j++)
								{
									if (deviceData[j + i + 6] == '"')
									{
										deviceNameArray[keyboardEntryCount][j] = '\0';
										break;
									}
									else
									{
										deviceNameArray[keyboardEntryCount][j] = deviceData[j + i + 6];
									}
								}
							}
						}
					}
				}
			}
		}
		
		// Look for string sequences that indicate the "Phys" field in a device entry
		if ((i + 4) < charsRead && deviceData[i] == 'P')
		{
			if (deviceData[i + 1] == 'h')
			{
				if (deviceData[i + 2] == 'y')
				{
					if (deviceData[i + 3] == 's')
					{
						if (deviceData[i + 4] == '=')
						{
							/*
							 * Store the "Phys" field of the current device entry
							 * 
							 * For my personal RPi4 setup, the phys strings are of the following format:
							 * usb-0000:01:00.0-<uniquePortNumber>/<inputNumber>
							 * 
							 * <inputNumber> can be one of the following values (experimentally obtained, may be incomplete)
							 * input0
							 * input1
							 * 
							 * As far as I know, the value of <inputNumber> isn't relevant for port identification.
							 * 
							 * <uniquePortNumber> is equal to one of the following values (experimentally obtained, may be incomplete)
							 * 1.1
							 * 1.1.1
							 * 1.2
							 * 1.2.1
							 * 1.3
							 * 1.3.1
							 * 1.4
							 * 1.4.1
							 * 
							 * <uniquePortNumber> can be used to uniquely identify what USB port is being used (as far as I know).
							 * 
							 * The 1.X.1 numbers are only used by certain devices (such as the RPi official keyboard) for reasons unknown to me,
							 * and are equivalent to the 1.X numbers (i.e. they identify the same USB port).
							 * 
							 * Port number to physical port location mapping
							 * 1.1: Top right USB 3.0 (blue) port
							 * 1.2: Bottom right USB 3.0 (blue) port
							 * 1.3: Top left USB 2.0 (black) port
							 * 1.4: Bottom left USB 2.0 (black) port
							 * 
							 * Top, bottom, right, and left assume that the USB ports are facing you and the RPi is facing "up" (the fan is pointing up, and the chassis feet are pointing down)
							 */
							for (unsigned int j = 0; j + i + 5 < charsRead; j++)
							{
								if (deviceData[j + i + 5] == '\n')
								{
									devicePhysArray[keyboardEntryCount][j] = '\0';
									break;
								}
								else
								{
									devicePhysArray[keyboardEntryCount][j] = deviceData[j + i + 5];
								}
							}
						}
					}
				}
			}
		}
		
		// Look for string sequences that indicate the event number of a device entry
		if ((i + 5) < charsRead && deviceData[i] == 'e')
		{
			if (deviceData[i + 1] == 'v')
			{
				if (deviceData[i + 2] == 'e')
				{
					if (deviceData[i + 3] == 'n')
					{
						if (deviceData[i + 4] == 't')
						{
							// Store the event number of the current device entry
							deviceEventNumberArray[keyboardEntryCount] = convertDecimalStringToUnsignedInteger(deviceData + i + 5, 1);
						}
					}
				}
			}
		}
		
		// Look for string sequences that indicate the "EV" field in a device entry
		if ((i + 5) < charsRead && deviceData[i] == 'B')
		{
			if (deviceData[i + 1] == ':')
			{
				if (deviceData[i + 2] == ' ')
				{
					if (deviceData[i + 3] == 'E')
					{
						if (deviceData[i + 4] == 'V')
						{
							if (deviceData[i + 5] == '=')
							{
								unsigned int tempDeviceEventFlags;
								char tempDeviceEventFlagsString[1000]; // Theoretical bug: EV strings longer than 1000 characters will crash the program
								
								// Temporarily store the "EV" field of the current device entry
								for (unsigned int j = 0; j + i + 6 < charsRead; j++)
								{
									if (deviceData[j + i + 6] == '\n')
									{
										tempDeviceEventFlags = convertHexStringToUnsignedInteger(tempDeviceEventFlagsString, j);
										break;
									}
									else
									{
										tempDeviceEventFlagsString[j] = deviceData[j + i + 6];
									}
								}
								
								/*
								 * Check if the EV value has the bit flags set that indicate keyboard-like functionality
								 * 
								 * An explanation for the magic number 0x120013 can be found at https://unix.stackexchange.com/questions/74903/explain-ev-in-proc-bus-input-devices-data/74907#74907.
								 * These flags aren't actually 100% indicative of a keyboard, as some non-keyboard devices (such as mice) will use the same flags.
								 * Because of that, some hard-coding (such as checking returned device names) should still be done on a per-hardware-setup basis.
								 * 
								 * See the following links for more details
								 * https://www.kernel.org/doc/Documentation/input/input.txt
								 * https://www.kernel.org/doc/Documentation/input/event-codes.txt
								 */
								if ((tempDeviceEventFlags & 0x120013) == 0x120013)
								{
									// If the device is not a keyboard, the next outer for-loop iteration will overwrite the current iteration's stored data (or just ignore it if this is the final device)
									keyboardEntryCount++;
								}
							}
						}
					}
				}
			}
		}
	}
	
	jclass keyboardIdentificationDataClass = (*environment)->FindClass(environment, "org/openbowl/multikeyboardlibrary/KeyboardIdentificationData");
	jmethodID keyboardIdentificationDataConstructor = (*environment)->GetMethodID(environment, keyboardIdentificationDataClass, "<init>", "(ILjava/lang/String;Ljava/lang/String;)V");
	jobjectArray identificationDataArray = (*environment)->NewObjectArray(environment, keyboardEntryCount, keyboardIdentificationDataClass, NULL);
	
	if (identificationDataArray == NULL)
	{
		if ((*environment)->ThrowNew(environment, (*environment)->FindClass(environment, "org/openbowl/multikeyboardlibrary/JNIException"), "Could not create jobjectArray identificationDataArray") < 0)
		{
			return NULL;
		}
	}
	
	jstring tempDeviceName;
	jstring tempDevicePhys;
	
	// Store all device entries with keyboard-related EV flags in the soon-to-be-returned array
	for (unsigned int i = 0; i < keyboardEntryCount; i++)
	{
		tempDeviceName = (*environment)->NewStringUTF(environment, deviceNameArray[i]);
		
		if (tempDeviceName == NULL)
		{
			if ((*environment)->ThrowNew(environment, (*environment)->FindClass(environment, "org/openbowl/multikeyboardlibrary/JNIException"), "Could not create a jstring out of a deviceNameArray element") < 0)
			{
				return NULL;
			}
		}
		
		tempDevicePhys = (*environment)->NewStringUTF(environment, devicePhysArray[i]);
		
		if (tempDevicePhys == NULL)
		{
			if ((*environment)->ThrowNew(environment, (*environment)->FindClass(environment, "org/openbowl/multikeyboardlibrary/JNIException"), "Could not create a jstring out of a devicePhysArray element") < 0)
			{
				return NULL;
			}
		}
		
		(*environment)->SetObjectArrayElement(environment, identificationDataArray, i, (*environment)->NewObject(environment, keyboardIdentificationDataClass, keyboardIdentificationDataConstructor, deviceEventNumberArray[i], tempDeviceName, tempDevicePhys));
	}
	
	for (unsigned int i = 0; i < deviceEntryCount; i++)
	{
		free(devicePhysArray[i]);
	}
	
	for (unsigned int i = 0; i < deviceEntryCount; i++)
	{
		free(deviceNameArray[i]);
	}
	
	free(devicePhysArray);
	free(deviceNameArray);
	free(deviceEventNumberArray);
	
	return identificationDataArray;
}

JNIEXPORT jboolean JNICALL Java_org_openbowl_multikeyboardlibrary_MultiKeyboardInputReader_pollInputs(JNIEnv *environment, jobject thisObject)
{
	// Get all the necessary classes
	jclass arrayListClass = (*environment)->FindClass(environment, "java/util/ArrayList");
	jclass keyboardClass = (*environment)->FindClass(environment, "org/openbowl/multikeyboardlibrary/MultiKeyboardInputReader$Keyboard");
	jclass multiKeyboardInputReaderClass = (*environment)->GetObjectClass(environment, thisObject);
	jclass queueClass = (*environment)->FindClass(environment, "java/util/Queue");
	
	// Get the necessary fields from each class
	jfieldID fieldDeviceEventNumber = (*environment)->GetFieldID(environment, keyboardClass, "deviceEventNumber", "I");
	jfieldID fieldInputQueue = (*environment)->GetFieldID(environment, keyboardClass, "inputQueue", "Ljava/util/Queue;");
	jfieldID fieldIsInputBeingPolled = (*environment)->GetFieldID(environment, multiKeyboardInputReaderClass, "isInputBeingPolled", "Z");
	jfieldID fieldPolledKeyboardList = (*environment)->GetFieldID(environment, multiKeyboardInputReaderClass, "polledKeyboardList", "Ljava/util/ArrayList;");
	
	// Get all of the necessary methods from each class
	jmethodID arrayListGetMethod = (*environment)->GetMethodID(environment, arrayListClass, "get", "(I)Ljava/lang/Object;");
	jmethodID arrayListSizeMethod = (*environment)->GetMethodID(environment, arrayListClass, "size", "()I");
	jmethodID queueAddMethod = (*environment)->GetMethodID(environment, queueClass, "add", "(Ljava/lang/Object;)Z");
	
	// Get the necessary objects/fields of each type
	jobject polledKeyboardList = (*environment)->GetObjectField(environment, thisObject, fieldPolledKeyboardList);
	jint polledKeyboardListSize = (*environment)->CallIntMethod(environment, polledKeyboardList, arrayListSizeMethod);
	
	char devicePath[20]; // The path "/dev/input/eventXXX" uses 20 characters (enough space for three digits after event)
	
	jobject currentKeyboard;
	jint deviceEventNumberArray[polledKeyboardListSize];
	jobject inputQueueArray[polledKeyboardListSize];
	int fileDescriptorArray[polledKeyboardListSize];
	
	// Get the device event number and input queue fields from each keyboard that polling was requested for
	for (unsigned int i = 0; i < polledKeyboardListSize; i++)
	{
		currentKeyboard = (*environment)->CallObjectMethod(environment, polledKeyboardList, arrayListGetMethod, i);

		deviceEventNumberArray[i] = (*environment)->GetIntField(environment, currentKeyboard, fieldDeviceEventNumber);
		
		inputQueueArray[i] = (*environment)->GetObjectField(environment, currentKeyboard, fieldInputQueue);
		
		if (sprintf(devicePath, "/dev/input/event%i", deviceEventNumberArray[i]) < 0)
		{
			if ((*environment)->ThrowNew(environment, (*environment)->FindClass(environment, "org/openbowl/multikeyboardlibrary/JNIException"), "Could not generate the event file path string") < 0)
			{
				return JNI_FALSE;
			}
		}
		
		if ((fileDescriptorArray[i] = open(devicePath, O_RDONLY)) == -1)
		{
			char exceptionMessageString[112]; // It is assumed that integers will take up to a maximum of 11 characters (10 digits and 1 negative sign)

			if (sprintf(exceptionMessageString, "Could not open element %i (descriptor %i) of the file descriptor array - errno is %i", i, deviceEventNumberArray[i], errno) < 0)
			{
				if ((*environment)->ThrowNew(environment, (*environment)->FindClass(environment, "org/openbowl/multikeyboardlibrary/JNIException"), "Could not generate the exception message string for file descriptor array element open failing") < 0)
				{
					return JNI_FALSE;
				}
			}
			
			if ((*environment)->ThrowNew(environment, (*environment)->FindClass(environment, "org/openbowl/multikeyboardlibrary/JNIException"), exceptionMessageString) < 0)
			{
				return JNI_FALSE;
			}
		}
	}
	
	struct pollfd pollfdArray[polledKeyboardListSize];
	
	// Poll for input until the user calls the stopPollingInputs() Java method
	while ((*environment)->GetBooleanField(environment, thisObject, fieldIsInputBeingPolled) == JNI_TRUE)
	{
		// Initialize the pollfdArray structs
		for (unsigned int i = 0; i < polledKeyboardListSize; i++)
		{
			pollfdArray[i].fd = fileDescriptorArray[i];
			pollfdArray[i].events = POLLIN;
		}
		
		if (poll(pollfdArray, polledKeyboardListSize, 100) == -1)
		{
			char exceptionMessageString[42]; // It is assumed that integers will take up to a maximum of 11 characters (10 digits and 1 negative sign)
			
			if (sprintf(exceptionMessageString, "Poll returned -1, and errno is %i", errno) < 0)
			{
				if ((*environment)->ThrowNew(environment, (*environment)->FindClass(environment, "org/openbowl/multikeyboardlibrary/JNIException"), "Could not generate the exception message string for poll returning -1") < 0)
				{
					return JNI_FALSE;
				}
			}
			
			if ((*environment)->ThrowNew(environment, (*environment)->FindClass(environment, "org/openbowl/multikeyboardlibrary/JNIException"), exceptionMessageString) < 0)
			{
				return JNI_FALSE;
			}
		}
		
		// Check for input from each file descriptor in pollfdArray
		for (unsigned int i = 0; i < polledKeyboardListSize; i++)
		{
			// If there was input from keyboard i
			if (pollfdArray[i].revents & POLLIN)
			{
				char readBuffer[10000];
				readBuffer[10000] = '\0';
				int bytesRead = read(fileDescriptorArray[i], &readBuffer, 9999);
				
				if (bytesRead == -1)
				{
					char exceptionMessageString[42]; // It is assumed that integers will take up to a maximum of 11 characters (10 digits and 1 negative sign)
			
					if (sprintf(exceptionMessageString, "Read returned -1, and errno is %i", errno) < 0)
					{
						if ((*environment)->ThrowNew(environment, (*environment)->FindClass(environment, "org/openbowl/multikeyboardlibrary/JNIException"), "Could not generate the exception message string for read returning -1") < 0)
						{
							return JNI_FALSE;
						}
					}
			
					if ((*environment)->ThrowNew(environment, (*environment)->FindClass(environment, "org/openbowl/multikeyboardlibrary/JNIException"), exceptionMessageString) < 0)
					{
						return JNI_FALSE;
					}
				}
				
				unsigned int inputEventSize = sizeof(struct timeval) + (2 * sizeof(unsigned short)) + sizeof(unsigned int);
				
				struct input_event *currentInputEvent;
				
				// Get the necessary data from each input_event read from the current keyboard event file
				for (unsigned int j = 0; j < bytesRead / inputEventSize; j++)
				{
					currentInputEvent = (struct input_event *) (readBuffer + (j * inputEventSize));
					
					if (currentInputEvent->type == EV_KEY)
					{
						(*environment)->CallBooleanMethod(environment, inputQueueArray[i], queueAddMethod, convertInputEventToKeyData(*currentInputEvent, environment));
					}
				}
			}
		}
	}
	
	return JNI_TRUE;
}

static unsigned int convertDecimalStringToUnsignedInteger(char inputString[], unsigned int inputStringLength)
{
	unsigned int result = 0;
	
	for (unsigned int i = 0; i < inputStringLength; i++)
	{
		result *= 10;
		
		switch(inputString[i])
		{
			case '1':
				result += 1;
				break;
				
			case '2':
				result += 2;
				break;
				
			case '3':
				result += 3;
				break;
				
			case '4':
				result += 4;
				break;
				
			case '5':
				result += 5;
				break;
				
			case '6':
				result += 6;
				break;
				
			case '7':
				result += 7;
				break;
				
			case '8':
				result += 8;
				break;
				
			case '9':
				result += 9;
				break;
		}
	}
	
	return result;
}

static unsigned int convertHexStringToUnsignedInteger(char inputString[], unsigned int inputStringLength)
{
	unsigned int result = 0;
	
	for (unsigned int i = 0; i < inputStringLength; i++)
	{
		result *= 16;
		
		switch(inputString[i])
		{
			case '1':
				result += 1;
				break;
				
			case '2':
				result += 2;
				break;
				
			case '3':
				result += 3;
				break;
				
			case '4':
				result += 4;
				break;
				
			case '5':
				result += 5;
				break;
				
			case '6':
				result += 6;
				break;
				
			case '7':
				result += 7;
				break;
				
			case '8':
				result += 8;
				break;
				
			case '9':
				result += 9;
				break;
				
			case 'a':
				result += 10;
				break;
				
			case 'b':
				result += 11;
				break;
				
			case 'c':
				result += 12;
				break;
				
			case 'd':
				result += 13;
				break;
				
			case 'e':
				result += 14;
				break;
				
			case 'f':
				result += 15;
				break;
		}
	}
	
	return result;
}

static jobject convertInputEventToKeyData(struct input_event inputEvent, JNIEnv *environment)
{
	// Get all of the necessary classes
	jclass keyDataClass = (*environment)->FindClass(environment, "org/openbowl/multikeyboardlibrary/KeyData");
	jclass keyCodeEnum = (*environment)->FindClass(environment, "org/openbowl/multikeyboardlibrary/KeyCode");
	jclass keyStateEnum = (*environment)->FindClass(environment, "org/openbowl/multikeyboardlibrary/KeyState");
	
	// Declare the necessary fields from each class
	jfieldID fieldKeyCode;
	jfieldID fieldKeyState;
	
	// Get all of the necessary methods from each class
	jmethodID keyDataConstructorMethod = (*environment)->GetMethodID(environment, keyDataClass, "<init>", "(Lorg/openbowl/multikeyboardlibrary/KeyCode;Lorg/openbowl/multikeyboardlibrary/KeyState;)V");
	
	// Declare the necessary objects from each class
	jobject keyCode;
	jobject keyState;
	
	// See input.h for a list of all input_event codes (here, only a subset of them is mapped to a Java enum)
	switch(inputEvent.code)
	{
		case KEY_ESC:
			fieldKeyCode = (*environment)->GetStaticFieldID(environment, keyCodeEnum, "KEYCODE_ESC", "Lorg/openbowl/multikeyboardlibrary/KeyCode;");
			break;
		
		case KEY_F1:
			fieldKeyCode = (*environment)->GetStaticFieldID(environment, keyCodeEnum, "KEYCODE_F1", "Lorg/openbowl/multikeyboardlibrary/KeyCode;");
			break;
		
		case KEY_F2:
			fieldKeyCode = (*environment)->GetStaticFieldID(environment, keyCodeEnum, "KEYCODE_F2", "Lorg/openbowl/multikeyboardlibrary/KeyCode;");
			break;
		
		case KEY_F3:
			fieldKeyCode = (*environment)->GetStaticFieldID(environment, keyCodeEnum, "KEYCODE_F3", "Lorg/openbowl/multikeyboardlibrary/KeyCode;");
			break;
		
		case KEY_F4:
			fieldKeyCode = (*environment)->GetStaticFieldID(environment, keyCodeEnum, "KEYCODE_F4", "Lorg/openbowl/multikeyboardlibrary/KeyCode;");
			break;
		
		case KEY_F5:
			fieldKeyCode = (*environment)->GetStaticFieldID(environment, keyCodeEnum, "KEYCODE_F5", "Lorg/openbowl/multikeyboardlibrary/KeyCode;");
			break;
		
		case KEY_F6:
			fieldKeyCode = (*environment)->GetStaticFieldID(environment, keyCodeEnum, "KEYCODE_F6", "Lorg/openbowl/multikeyboardlibrary/KeyCode;");
			break;
		
		case KEY_F7:
			fieldKeyCode = (*environment)->GetStaticFieldID(environment, keyCodeEnum, "KEYCODE_F7", "Lorg/openbowl/multikeyboardlibrary/KeyCode;");
			break;
		
		case KEY_F8:
			fieldKeyCode = (*environment)->GetStaticFieldID(environment, keyCodeEnum, "KEYCODE_F8", "Lorg/openbowl/multikeyboardlibrary/KeyCode;");
			break;
		
		case KEY_F9:
			fieldKeyCode = (*environment)->GetStaticFieldID(environment, keyCodeEnum, "KEYCODE_F9", "Lorg/openbowl/multikeyboardlibrary/KeyCode;");
			break;
		
		case KEY_F10:
			fieldKeyCode = (*environment)->GetStaticFieldID(environment, keyCodeEnum, "KEYCODE_F10", "Lorg/openbowl/multikeyboardlibrary/KeyCode;");
			break;
		
		case KEY_NUMLOCK:
			fieldKeyCode = (*environment)->GetStaticFieldID(environment, keyCodeEnum, "KEYCODE_NUMLOCK", "Lorg/openbowl/multikeyboardlibrary/KeyCode;");
			break;
		
		case KEY_SYSRQ:
			fieldKeyCode = (*environment)->GetStaticFieldID(environment, keyCodeEnum, "KEYCODE_PRTSCN", "Lorg/openbowl/multikeyboardlibrary/KeyCode;");
			break;
		
		case KEY_DELETE:
			fieldKeyCode = (*environment)->GetStaticFieldID(environment, keyCodeEnum, "KEYCODE_DELETE", "Lorg/openbowl/multikeyboardlibrary/KeyCode;");
			break;
		
		case KEY_1:
			fieldKeyCode = (*environment)->GetStaticFieldID(environment, keyCodeEnum, "KEYCODE_1", "Lorg/openbowl/multikeyboardlibrary/KeyCode;");
			break;
		
		case KEY_2:
			fieldKeyCode = (*environment)->GetStaticFieldID(environment, keyCodeEnum, "KEYCODE_2", "Lorg/openbowl/multikeyboardlibrary/KeyCode;");
			break;
		
		case KEY_3:
			fieldKeyCode = (*environment)->GetStaticFieldID(environment, keyCodeEnum, "KEYCODE_3", "Lorg/openbowl/multikeyboardlibrary/KeyCode;");
			break;
		
		case KEY_4:
			fieldKeyCode = (*environment)->GetStaticFieldID(environment, keyCodeEnum, "KEYCODE_4", "Lorg/openbowl/multikeyboardlibrary/KeyCode;");
			break;
		
		case KEY_5:
			fieldKeyCode = (*environment)->GetStaticFieldID(environment, keyCodeEnum, "KEYCODE_5", "Lorg/openbowl/multikeyboardlibrary/KeyCode;");
			break;
		
		case KEY_6:
			fieldKeyCode = (*environment)->GetStaticFieldID(environment, keyCodeEnum, "KEYCODE_6", "Lorg/openbowl/multikeyboardlibrary/KeyCode;");
			break;
		
		case KEY_7:
			fieldKeyCode = (*environment)->GetStaticFieldID(environment, keyCodeEnum, "KEYCODE_7", "Lorg/openbowl/multikeyboardlibrary/KeyCode;");
			break;
		
		case KEY_8:
			fieldKeyCode = (*environment)->GetStaticFieldID(environment, keyCodeEnum, "KEYCODE_8", "Lorg/openbowl/multikeyboardlibrary/KeyCode;");
			break;
		
		case KEY_9:
			fieldKeyCode = (*environment)->GetStaticFieldID(environment, keyCodeEnum, "KEYCODE_9", "Lorg/openbowl/multikeyboardlibrary/KeyCode;");
			break;
		
		case KEY_0:
			fieldKeyCode = (*environment)->GetStaticFieldID(environment, keyCodeEnum, "KEYCODE_0", "Lorg/openbowl/multikeyboardlibrary/KeyCode;");
			break;
		
		case KEY_A:
			fieldKeyCode = (*environment)->GetStaticFieldID(environment, keyCodeEnum, "KEYCODE_A", "Lorg/openbowl/multikeyboardlibrary/KeyCode;");
			break;
		
		case KEY_B:
			fieldKeyCode = (*environment)->GetStaticFieldID(environment, keyCodeEnum, "KEYCODE_B", "Lorg/openbowl/multikeyboardlibrary/KeyCode;");
			break;
		
		case KEY_C:
			fieldKeyCode = (*environment)->GetStaticFieldID(environment, keyCodeEnum, "KEYCODE_C", "Lorg/openbowl/multikeyboardlibrary/KeyCode;");
			break;
		
		case KEY_D:
			fieldKeyCode = (*environment)->GetStaticFieldID(environment, keyCodeEnum, "KEYCODE_D", "Lorg/openbowl/multikeyboardlibrary/KeyCode;");
			break;
		
		case KEY_E:
			fieldKeyCode = (*environment)->GetStaticFieldID(environment, keyCodeEnum, "KEYCODE_E", "Lorg/openbowl/multikeyboardlibrary/KeyCode;");
			break;
		
		case KEY_F:
			fieldKeyCode = (*environment)->GetStaticFieldID(environment, keyCodeEnum, "KEYCODE_F", "Lorg/openbowl/multikeyboardlibrary/KeyCode;");
			break;
		
		case KEY_G:
			fieldKeyCode = (*environment)->GetStaticFieldID(environment, keyCodeEnum, "KEYCODE_G", "Lorg/openbowl/multikeyboardlibrary/KeyCode;");
			break;
		
		case KEY_H:
			fieldKeyCode = (*environment)->GetStaticFieldID(environment, keyCodeEnum, "KEYCODE_H", "Lorg/openbowl/multikeyboardlibrary/KeyCode;");
			break;
		
		case KEY_I:
			fieldKeyCode = (*environment)->GetStaticFieldID(environment, keyCodeEnum, "KEYCODE_I", "Lorg/openbowl/multikeyboardlibrary/KeyCode;");
			break;
		
		case KEY_J:
			fieldKeyCode = (*environment)->GetStaticFieldID(environment, keyCodeEnum, "KEYCODE_J", "Lorg/openbowl/multikeyboardlibrary/KeyCode;");
			break;
		
		case KEY_K:
			fieldKeyCode = (*environment)->GetStaticFieldID(environment, keyCodeEnum, "KEYCODE_K", "Lorg/openbowl/multikeyboardlibrary/KeyCode;");
			break;
		
		case KEY_L:
			fieldKeyCode = (*environment)->GetStaticFieldID(environment, keyCodeEnum, "KEYCODE_L", "Lorg/openbowl/multikeyboardlibrary/KeyCode;");
			break;
		
		case KEY_M:
			fieldKeyCode = (*environment)->GetStaticFieldID(environment, keyCodeEnum, "KEYCODE_M", "Lorg/openbowl/multikeyboardlibrary/KeyCode;");
			break;
		
		case KEY_N:
			fieldKeyCode = (*environment)->GetStaticFieldID(environment, keyCodeEnum, "KEYCODE_N", "Lorg/openbowl/multikeyboardlibrary/KeyCode;");
			break;
		
		case KEY_O:
			fieldKeyCode = (*environment)->GetStaticFieldID(environment, keyCodeEnum, "KEYCODE_O", "Lorg/openbowl/multikeyboardlibrary/KeyCode;");
			break;
		
		case KEY_P:
			fieldKeyCode = (*environment)->GetStaticFieldID(environment, keyCodeEnum, "KEYCODE_P", "Lorg/openbowl/multikeyboardlibrary/KeyCode;");
			break;
		
		case KEY_Q:
			fieldKeyCode = (*environment)->GetStaticFieldID(environment, keyCodeEnum, "KEYCODE_Q", "Lorg/openbowl/multikeyboardlibrary/KeyCode;");
			break;
		
		case KEY_R:
			fieldKeyCode = (*environment)->GetStaticFieldID(environment, keyCodeEnum, "KEYCODE_R", "Lorg/openbowl/multikeyboardlibrary/KeyCode;");
			break;
		
		case KEY_S:
			fieldKeyCode = (*environment)->GetStaticFieldID(environment, keyCodeEnum, "KEYCODE_S", "Lorg/openbowl/multikeyboardlibrary/KeyCode;");
			break;
		
		case KEY_T:
			fieldKeyCode = (*environment)->GetStaticFieldID(environment, keyCodeEnum, "KEYCODE_T", "Lorg/openbowl/multikeyboardlibrary/KeyCode;");
			break;
		
		case KEY_U:
			fieldKeyCode = (*environment)->GetStaticFieldID(environment, keyCodeEnum, "KEYCODE_U", "Lorg/openbowl/multikeyboardlibrary/KeyCode;");
			break;
		
		case KEY_V:
			fieldKeyCode = (*environment)->GetStaticFieldID(environment, keyCodeEnum, "KEYCODE_V", "Lorg/openbowl/multikeyboardlibrary/KeyCode;");
			break;
		
		case KEY_W:
			fieldKeyCode = (*environment)->GetStaticFieldID(environment, keyCodeEnum, "KEYCODE_W", "Lorg/openbowl/multikeyboardlibrary/KeyCode;");
			break;
		
		case KEY_X:
			fieldKeyCode = (*environment)->GetStaticFieldID(environment, keyCodeEnum, "KEYCODE_X", "Lorg/openbowl/multikeyboardlibrary/KeyCode;");
			break;
		
		case KEY_Y:
			fieldKeyCode = (*environment)->GetStaticFieldID(environment, keyCodeEnum, "KEYCODE_Y", "Lorg/openbowl/multikeyboardlibrary/KeyCode;");
			break;
		
		case KEY_Z:
			fieldKeyCode = (*environment)->GetStaticFieldID(environment, keyCodeEnum, "KEYCODE_Z", "Lorg/openbowl/multikeyboardlibrary/KeyCode;");
			break;
		
		case KEY_GRAVE:
			fieldKeyCode = (*environment)->GetStaticFieldID(environment, keyCodeEnum, "KEYCODE_GRAVE", "Lorg/openbowl/multikeyboardlibrary/KeyCode;");
			break;
		
		case KEY_MINUS:
			fieldKeyCode = (*environment)->GetStaticFieldID(environment, keyCodeEnum, "KEYCODE_MINUS", "Lorg/openbowl/multikeyboardlibrary/KeyCode;");
			break;
		
		case KEY_EQUAL:
			fieldKeyCode = (*environment)->GetStaticFieldID(environment, keyCodeEnum, "KEYCODE_EQUAL", "Lorg/openbowl/multikeyboardlibrary/KeyCode;");
			break;
		
		case KEY_BACKSPACE:
			fieldKeyCode = (*environment)->GetStaticFieldID(environment, keyCodeEnum, "KEYCODE_BACKSPACE", "Lorg/openbowl/multikeyboardlibrary/KeyCode;");
			break;
		
		case KEY_TAB:
			fieldKeyCode = (*environment)->GetStaticFieldID(environment, keyCodeEnum, "KEYCODE_TAB", "Lorg/openbowl/multikeyboardlibrary/KeyCode;");
			break;
		
		case KEY_LEFTBRACE:
			fieldKeyCode = (*environment)->GetStaticFieldID(environment, keyCodeEnum, "KEYCODE_LEFTBRACE", "Lorg/openbowl/multikeyboardlibrary/KeyCode;");
			break;
		
		case KEY_RIGHTBRACE:
			fieldKeyCode = (*environment)->GetStaticFieldID(environment, keyCodeEnum, "KEYCODE_RIGHTBRACE", "Lorg/openbowl/multikeyboardlibrary/KeyCode;");
			break;
		
		case KEY_BACKSLASH:
			fieldKeyCode = (*environment)->GetStaticFieldID(environment, keyCodeEnum, "KEYCODE_BACKSLASH", "Lorg/openbowl/multikeyboardlibrary/KeyCode;");
			break;
		
		case KEY_CAPSLOCK:
			fieldKeyCode = (*environment)->GetStaticFieldID(environment, keyCodeEnum, "KEYCODE_CAPSLOCK", "Lorg/openbowl/multikeyboardlibrary/KeyCode;");
			break;
		
		case KEY_SEMICOLON:
			fieldKeyCode = (*environment)->GetStaticFieldID(environment, keyCodeEnum, "KEYCODE_SEMICOLON", "Lorg/openbowl/multikeyboardlibrary/KeyCode;");
			break;
		
		case KEY_APOSTROPHE:
			fieldKeyCode = (*environment)->GetStaticFieldID(environment, keyCodeEnum, "KEYCODE_APOSTROPHE", "Lorg/openbowl/multikeyboardlibrary/KeyCode;");
			break;
		
		case KEY_ENTER:
			fieldKeyCode = (*environment)->GetStaticFieldID(environment, keyCodeEnum, "KEYCODE_ENTER", "Lorg/openbowl/multikeyboardlibrary/KeyCode;");
			break;
		
		case KEY_LEFTSHIFT:
			fieldKeyCode = (*environment)->GetStaticFieldID(environment, keyCodeEnum, "KEYCODE_LEFTSHIFT", "Lorg/openbowl/multikeyboardlibrary/KeyCode;");
			break;
		
		case KEY_COMMA:
			fieldKeyCode = (*environment)->GetStaticFieldID(environment, keyCodeEnum, "KEYCODE_COMMA", "Lorg/openbowl/multikeyboardlibrary/KeyCode;");
			break;
		
		case KEY_DOT:
			fieldKeyCode = (*environment)->GetStaticFieldID(environment, keyCodeEnum, "KEYCODE_DOT", "Lorg/openbowl/multikeyboardlibrary/KeyCode;");
			break;
		
		case KEY_SLASH:
			fieldKeyCode = (*environment)->GetStaticFieldID(environment, keyCodeEnum, "KEYCODE_SLASH", "Lorg/openbowl/multikeyboardlibrary/KeyCode;");
			break;
		
		case KEY_RIGHTSHIFT:
			fieldKeyCode = (*environment)->GetStaticFieldID(environment, keyCodeEnum, "KEYCODE_RIGHTSHIFT", "Lorg/openbowl/multikeyboardlibrary/KeyCode;");
			break;
		
		case KEY_LEFTCTRL:
			fieldKeyCode = (*environment)->GetStaticFieldID(environment, keyCodeEnum, "KEYCODE_LEFTCTRL", "Lorg/openbowl/multikeyboardlibrary/KeyCode;");
			break;
		
		case KEY_LEFTALT:
			fieldKeyCode = (*environment)->GetStaticFieldID(environment, keyCodeEnum, "KEYCODE_LEFTALT", "Lorg/openbowl/multikeyboardlibrary/KeyCode;");
			break;
		
		case KEY_SPACE:
			fieldKeyCode = (*environment)->GetStaticFieldID(environment, keyCodeEnum, "KEYCODE_SPACE", "Lorg/openbowl/multikeyboardlibrary/KeyCode;");
			break;
		
		case KEY_RIGHTALT:
			fieldKeyCode = (*environment)->GetStaticFieldID(environment, keyCodeEnum, "KEYCODE_RIGHTALT", "Lorg/openbowl/multikeyboardlibrary/KeyCode;");
			break;
		
		case KEY_RIGHTCTRL:
			fieldKeyCode = (*environment)->GetStaticFieldID(environment, keyCodeEnum, "KEYCODE_RIGHTCTRL", "Lorg/openbowl/multikeyboardlibrary/KeyCode;");
			break;
		
		case KEY_UP:
			fieldKeyCode = (*environment)->GetStaticFieldID(environment, keyCodeEnum, "KEYCODE_UP", "Lorg/openbowl/multikeyboardlibrary/KeyCode;");
			break;
		
		case KEY_RIGHT:
			fieldKeyCode = (*environment)->GetStaticFieldID(environment, keyCodeEnum, "KEYCODE_RIGHT", "Lorg/openbowl/multikeyboardlibrary/KeyCode;");
			break;
		
		case KEY_DOWN:
			fieldKeyCode = (*environment)->GetStaticFieldID(environment, keyCodeEnum, "KEYCODE_DOWN", "Lorg/openbowl/multikeyboardlibrary/KeyCode;");
			break;
		
		case KEY_LEFT:
			fieldKeyCode = (*environment)->GetStaticFieldID(environment, keyCodeEnum, "KEYCODE_LEFT", "Lorg/openbowl/multikeyboardlibrary/KeyCode;");
			break;
		
		default:
			fieldKeyCode = (*environment)->GetStaticFieldID(environment, keyCodeEnum, "KEYCODE_UNKNOWN", "Lorg/openbowl/multikeyboardlibrary/KeyCode;");
	}
	
	// See https://www.kernel.org/doc/Documentation/input/input.txt section 5 (Event Interface) to verify the identities of 0, 1, and 2
	switch(inputEvent.value)
	{
		case 0:
			fieldKeyState = (*environment)->GetStaticFieldID(environment, keyStateEnum, "KEYSTATE_RELEASE", "Lorg/openbowl/multikeyboardlibrary/KeyState;");
			break;
			
		case 1:
			fieldKeyState = (*environment)->GetStaticFieldID(environment, keyStateEnum, "KEYSTATE_PRESS", "Lorg/openbowl/multikeyboardlibrary/KeyState;");
			break;
			
		case 2:
			fieldKeyState = (*environment)->GetStaticFieldID(environment, keyStateEnum, "KEYSTATE_AUTOREPEAT", "Lorg/openbowl/multikeyboardlibrary/KeyState;");
			break;
		
		default:
			fieldKeyState = (*environment)->GetStaticFieldID(environment, keyStateEnum, "KEYSTATE_UNKNOWN", "Lorg/openbowl/multikeyboardlibrary/KeyState;");
	}
	
	keyCode = (*environment)->GetStaticObjectField(environment, keyCodeEnum, fieldKeyCode);
	keyState = (*environment)->GetStaticObjectField(environment, keyStateEnum, fieldKeyState);
	
	return (*environment)->NewObject(environment, keyDataClass, keyDataConstructorMethod, keyCode, keyState);
}
