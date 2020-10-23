#ifndef EEPROM_HANDLER_H
#define EEPROM_HANDLER_H

#include <EEPROM.h>

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

/**
 * Method to setup the eeprom on a new arduino
 * 
 * Layout
 * -------------------------------------
 * Anti Alc:      1 | 2 | 3 | 4 | 5 | 6
 * -------------------------------------
 * ID Index:      0 | 2 | 4 | 6 | 8 | 10
 * Amount Index:  1 | 3 | 5 | 7 | 9 | 11
 * -------------------------------------
 * Anti Alc:      1 | 2 | 3 | 4 | 5 | 6
 * -------------------------------------
 * ID Index:      12| 14| 16| 18| 20| 22
 * Amount Index:  13| 15| 17| 19| 21| 23
 */
 void setupEEPROM() {
  // Set the non alc drinks
  for (int i = 0; i < 12; i++) {
    if (i % 2 == 0) {
      saveInt(i, i/2);
    } else {
      saveInt(i, 1000);
    }
  }
  // Set the alc drinks
  for (int i = 0; i < 12; i++) {
    if (i % 2 == 0) {
      saveInt(i + 12, i/2);
    } else {
      saveInt(i + 12, 1000);
    }
  }
 }

#endif
