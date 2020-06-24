
#include "shift_register.h"
#include "bluetooth_interface.h"

/**
 * Setup routine
 */
void setup() {
  // Initialize the serial interface
  Serial.begin(9600);
  // Initialize the bluetooth interface
  btSerial.begin(9600);
  // Setup the shift registry
  setupRegister();
}

/**
 * Wait for commands and execute them
 */
void loop() {
  handleNextCommand();
}
