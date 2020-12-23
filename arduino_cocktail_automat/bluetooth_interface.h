#ifndef BLUETOOTH_INTERFACE_H
#define BLUETOOTH_INTERFACE_H

#include <SoftwareSerial.h>
#include "eeprom_handler.h"
#include "motor_handler.h"

/**
 * The amount of drinks that have been made without recalibrating the roundel
 */
int drinksSinceCalibration = 0;

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
  for (int i = 6; i < 12; i++) {
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
  // Save the result
  saveInt(position, id);
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
 * Get a drink from one of the pumps
 * 
 * @position The position of the drink
 * @amount The amount of the drink
 * @last True if it is the last drink
 */
void pumpDrink(int position, int amount, bool last) {
  // Move cup to the right nozzle
  motorCup.moveToNozzle(position);

  // Fill in the drink
  if (amount > 0) {
    delay(50);
    motorCup.moveToNozzle(position);
    startPump(position, amount);
  }

  // Check if it was the last run
  if (last) {
    motorCup.toMiddle(position);
  }
}

/**
 * Get a drink from the roundel
 * 
 * @position The position of the drink
 * @amount The amount of the drink
 * @last True if it is the last drink
 */
void roundelDrink(int position, int amount, bool last) {
  // Move cup to the right 
  motorCup.toInitial(false);

  // Fill in the drink
  motorRoundel.toBottle(position);
  while (amount > 0) {
    if (amount >= 20) {
      motorShift.fullShot();
      amount -= 20;
    } else {
      motorShift.halfShot();
      amount -= 10;
    }
    delay(50);
    if (amount > 0) {
      motorRoundel.shake(); 
    }
  }

  // Check if it was the last run
  if (last) {
    motorCup.toInitial(true);
  }
}

/**
 * Mix a drink
 */
void mixDrink() {
  // First get the amount of drinks
  int drinkCount = getCommand().toInt();

  // Fill in each ingredient
  for (int i = 0; i < drinkCount; i++) {
    // Get the necessary information
    int position = getCommand().toInt();
    int amount = getCommand().toInt();

    // Check if the drink is attached to the pumps or to the roundel
    if (position > 5) {
      roundelDrink(position - 6, amount, i == (drinkCount - 1));
    } else {
      pumpDrink(position, amount, i == (drinkCount - 1));
    }
  }

  // Check if the roundel has to recalibrate
  drinksSinceCalibration++;
  if (drinksSinceCalibration > 5) {
    motorRoundel.toInitial();
    drinksSinceCalibration = 0;  
  }
  
  sendData("FINISHED");
}

/**
 * Start the cleaning process for the pumps
 */
void startCleaning() {
  for (int index = 0; index < 5; index++) {
    if (index != 0) {
      delay(500); 
    }
    for (int i = 0; i < 6; i++) {
      activateMotor(PUMPS[i]);
    }
    delay(2000);
    for (int i = 0; i < 6; i++) {
      deactivateMotor(PUMPS[i]);
    }
  }
}

/**
 * Method to handle a command
 */
void handleCommand(String command) {
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
  else if (command == "CLEANING_PROGRAM") {
    startCleaning();
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
