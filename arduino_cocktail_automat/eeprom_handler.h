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

#endif
