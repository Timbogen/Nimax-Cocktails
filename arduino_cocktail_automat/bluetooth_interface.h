#ifndef BLUETOOTH_INTERFACE_H
#define BLUETOOTH_INTERFACE_H

#include <SoftwareSerial.h>
#include "eeprom_handler.h"

/**
 * The bluetooth serial
 */
SoftwareSerial btSerial(11, 12); // (TX, RX)

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
  for (int i = 0; i < 12; i++) {
    sendData(String(readInt(i)));
  }
  // Then send the alcoholic drinks
  sendData("ALC");
  for (int i = 12; i < 24; i++) {
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
  saveInt(position, id);
  saveInt(position + 1, amount);
}

/**
 * Method to handle a command
 */
void handleCommand(String command) {
  switch(command) {
    case "SETUP":
      sendSetup();
      break;
    case "MODIFY":
      modifySetup();
      break;
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
