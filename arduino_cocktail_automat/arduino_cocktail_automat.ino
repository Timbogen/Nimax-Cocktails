#include "bluetooth_interface.h"
#include "motor_handler.h"

/**
   Setup routine
*/
void setup() {
  // Initialize the serial interface
  Serial.begin(9600);
  // Initialize the bluetooth interface
  btSerial.begin(9600);
  // Setup the machine
  setupMachine();
  // Move to the initial position
  moveToInitial();
}

/**
   Wait for commands and execute them
*/
void loop() {
  handleNextCommand();
}

/**
   Setup the pins etc.
*/
void setupMachine() {
  // Initialize the pumps
  for (int i = 0; i < 6; i++) {
    pinMode(PUMPS[i], OUTPUT);
    deactivateMotor(PUMPS[i]);
  }
  
  // Setup the motors
  motorShift.setup();
  motorRoundel.setup();
  motorCup.setup();
  delay(2000);
}

/**
   Move everything to the initial position
*/
void moveToInitial() {
  motorCup.toInitial(true);
  motorShift.toInitial();
  motorRoundel.toInitial();
}
