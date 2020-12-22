#ifndef BLUETOOTH_INTERFACE_H
#define BLUETOOTH_INTERFACE_H

#include <SoftwareSerial.h>
#include "eeprom_handler.h"
#include "motor_handler.h"

/**
 * The bluetooth serial
 */
SoftwareSerial btSerial(11, 10); // (TX, RX)

/**
 * Method to send data back to the phone
 */
void sendData(String data) {
  data = "<" + data + ">";
  btSerial.print(data);
}

/**
 * Method to put together a string
 */
String getCommand() {
  bool commandOK = false;
  String command = "";
  // Listen for input until command is complete
  while(!commandOK) {
    if (btSerial.available()) {
      char received = btSerial.read();
      // Check if the command begins
      if (received == '<') {
        command = "";
      } else if (received == '>') {
        commandOK = true;
      } else {
        command += received;
      }
    }
  }
  // Return the command
  Serial.println(command);
  return command;
}

/**
 * Method to send the setup of the cocktail machine
 */
void sendSetup() {
  // First send the non alcohlic drinks
  for (int i = 0; i < 6; i++) {
    sendData(String(readInt(i)));
  }
  // Then send the alcoholic drinks
  sendData("ROUNDEL");
  for (int i = 62; i < 12; i++) {
    sendData(String(readInt(i)));
  }
  sendData("END");
}

/**
 * Method to modify a drink
 */
void modifyDrink() {
  // First get the position
  int position = getCommand().toInt();
  // Then the drink id
  int id = getCommand().toInt();
  // And finally the amount
  int amount = getCommand().toInt();
  // Save the result
  saveInt(position * 2, id);
  saveInt(position * 2 + 1, amount);
}

/**
 * Start the pump manually
 */
void startPump() {
  int position = getCommand().toInt();
  activateMotor(PUMPS[position]);
}

/**
 * Stop the pump manually
 */
void stopPump() {
  int position = getCommand().toInt();
  deactivateMotor(PUMPS[position]);
}

/**
 * Mix a drink
 */
void mixDrink() {
  // The drink data
  int alc[6] = {0, 0, 0, 0, 0, 0}, antiAlc[6] = {0, 0, 0, 0, 0, 0};

  // Get the drink data
  for (int i = 0; i < 6; i++) {
    // Get the amount for the alc
    alc[i] = getCommand().toInt();
    
    // Update the amounts
    int newValue = readInt(i * 2 + 13) - alc[i];
    saveInt(i * 2 + 13, newValue);
  }
  for (int i = 0; i < 6; i++) {
    antiAlc[i] = getCommand().toInt();

    // Update the amounts
    int newValue = readInt(i * 2 + 1) - antiAlc[i];
    saveInt(i * 2 + 1, newValue);
  }

  // Check if the drink contains alcohol
  bool noAlc = true;
  for (int i = 0; i < 6; i++) {
    if (alc[i] > 0) {
      noAlc = false;
      break;
    }
  }

  if (!noAlc) {
    // Move cup to the right
    motorCup.toInitial(false);
    
    // Fill  in the alc
    for (int i = 0; i < 6; i++) {
      motorRoundel.toBottle(i);
      while (alc[i] > 0) {
        if (alc[i] >= 20) {
          motorShift.fullShot();
          alc[i] -= 20;
        } else {
          motorShift.halfShot();
          alc[i] -= 10;
        }
        delay(50);
        if (alc[i] > 0) {
          motorRoundel.shake(); 
        }
      }
    }

    // Move the roundel back to initial
    motorRoundel.toInitial();
    delay(1000);
  }

  // Fill in the anti alc
  int position = -1;
  for (int i = 0; i < 6; i++) {
    if (antiAlc[i] > 0) {
      delay(50);
      motorCup.moveToNozzle(i);
      position = i;
      startPump(i, antiAlc[i]);
    }
  }

  // Move to the middle
  if (position == -1) {
    motorCup.toInitial(true);
  } else {
    motorCup.toMiddle(position);
  }
  
  sendData("FINISHED");
}

/**
 * Method to handle a command
 */
void handleCommand(String command) {
  Serial.println("COMMAND");
  if(command == "SETUP") {
    sendSetup();
  }
  else if (command == "MODIFY") {
    modifyDrink();
  }
  else if (command == "START_PUMP") {
    startPump();
  }
  else if (command == "STOP_PUMP") {
    stopPump();
  }
  else if (command == "MANUAL_CONTROL") {
    sendData(changeMotorState(getCommand()));
  }
  else if (command == "MIX_DRINK") {
    mixDrink();
  }
  else if (command == "REQUEST_MIXING") {
    sendData("READY");
  }
}
 

/**
 * Wait for the next input and handle it
 */
void handleNextCommand() {
  if (btSerial.available()) {
    handleCommand(getCommand());
  }
}

#endif
