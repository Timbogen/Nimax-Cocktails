#include <SoftwareSerial.h>
#include <EEPROM.h>

/**
 * The bluetooth serial
 */
SoftwareSerial btSerial(10, 11); // (TX, RX)


/**
 * Setup routine
 */
void setup() {
  // Initialize the serial interface
  Serial.begin(9600);
  // Initialize the bluetooth interface
  btSerial.begin(9600);
}

/**
 * Main logic
 */
void loop() {
  // Check if there is data available
  if (btSerial.available()) {
    // Handle the incoming command
    handleCommand(getCommand());
  }
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
 * Method to handle a command
 */
void handleCommand(String command) {
  if(command == "SETUP") {
    sendSetup();
  } else if (command == "MODIFY") {
    modifyDrink();
  }
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
 * Method to send data back to the phone
 */
void sendData(String data) {
  data = "<" + data + ">";
  btSerial.print(data);
}

/**
 * Method to write an integer into the eeprom
 */
void saveInt(int address, int value) {
  // Modify the address
  address *= 2;
  // Split the integer into two bytes
  byte two = (value & 0xFF);
  byte one = ((value >> 8) & 0xFF);
  // Write it into the storage
  EEPROM.update(address, two);
  EEPROM.update(address + 1, one);
}

/**
 * Method to read an integer out of the eeprom
 */
int readInt(int address) {
  // Modify the address
  address *= 2;
  // Read in the bytes at the specified adress
  long two = EEPROM.read(address);
  long one = EEPROM.read(address + 1);
  // Put both bytes together
  return (two & 0xFFFF) + ((one << 8) & 0xFFFF);
}




