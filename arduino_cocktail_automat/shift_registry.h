#ifndef SHIFT_REGISTRY_H
#define SHIFT_REGISTRY_H
/**
 * The ports for the shift registry
 */
int SH_CP = 8, ST_CP = 9, DS = 10;
/**
 * The output pins for the motors
 */
int MDR = 0, MDL = 1, MRR = 2, MRL = 3, MSR = 4, MSL = 5;
/**
 * The output pins for the pumps
 */
int PUMPS[] = {6, 7, 8, 9, 10, 11};
/**
 * The corresponding data array containing the information how the shift registry should be filled
 */
int input[] = {1, 1, 1, 1, 1, 1, 1, 1}; 

/**
 * Setup the pins
 */
void setupRegistry() {
  pinMode(SH_CP, OUTPUT);
  pinMode(ST_CP, OUTPUT);
  pinMode(DS, OUTPUT);
}

/**
 * Update the shift register's outputs
 */
void updateRegister() {
  for (int i = 7; i >= 0; i--) {
    // Initial step
    digitalWrite(DS, LOW);     
    digitalWrite(SH_CP, LOW);

    // Enter the information
    digitalWrite(DS, input[i]);
    digitalWrite(SH_CP, HIGH);
  }
  // Update the shift registry
  digitalWrite(ST_CP, 1);
}

/**
 * Activate a given output pin in the shift register
 */
void activatePin(int index) {
  input[index] = 0;
  updateRegister();
}

/**
 * Deactivate a given output pin in the shift register
 */
void deactivatePin(int index) {
  input[index] = 1;
  updateRegister();
}
#endif
