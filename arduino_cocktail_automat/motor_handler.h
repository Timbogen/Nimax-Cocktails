#ifndef MOTOR_HANDLER_H
#define MOTOR_HANDLER_H

#include <Encoder.h>

/**
 * The output pins for the motors
 * 
 * SHIFT_UP = MDL, SHIFT_DOWN = MDR, ROUNDEL_LEFT = MRL, ROUNDEL_RIGHT = MRR,
 * CUP_LEFT = MSL, CUP_RIGHT = MSR
 */
int SHIFT_UP = 28, SHIFT_DOWN = 30, ROUNDEL_LEFT = 32, ROUNDEL_RIGHT = 34,
    CUP_LEFT = 36, CUP_RIGHT = 38;

/**
 * The output pins for the pumps
 */
int PUMPS[6] = {40, 42, 25, 27, 29, 31};

/**
 * Activate a motor
 */
void activateMotor(int motor) {
  digitalWrite(motor, LOW);
}

/**
 * Deactivate a motor
 */
void deactivateMotor(int motor) {
  digitalWrite(motor, HIGH);
}

/**
 * Move a motor until it hits the barrier
 * 
 * @param motor to be activated
 * @param barrier that the motor shall move to
 * @param threshold that signals the motor to stop
 */
void moveToBarrier(int motor, int barrier, int threshold) {
    // Start the motor (if necessary)
    int value = analogRead(barrier);
    if (value > threshold) {
      activateMotor(motor);
    }
  
    // Watch the barrier's value and wait till it falls below a certain value
    while (value > threshold) {
      value = analogRead(barrier);
    }
  
    // Stop the motor
    deactivateMotor(motor);
}

/**
 * Start the pump until a certain amount was pumped up
 * 
 * @param index of the pump
 * @param amount to be pumped up (in ml)
 */
void startPump(int index, int amount) {
  int timeout = amount * 60;
  activateMotor(PUMPS[index]);
  delay(timeout);
  deactivateMotor(PUMPS[index]);
  delay(200);
}

/**
 * The motor that moves the cup
 */
struct MotorCup {
  /**
   * The pin of the barriers  
   */
  int BARRIER_LEFT = A1, BARRIER_RIGHT = A0;
  /**
   * Array containing the thresholds for each nozzle
   */
  int NOZZLE_THRESHOLDS[6] = {925, 920, 910, 877, 860, 820};
  /**
   * The time, the motor needs to get to the middle position
   */
  int MIDDLE_FROM_RIGHT = 2650, MIDDLE_FROM_LEFT = 2800;
  /**
   * The encoder (GREEN = 26, YELLOW = 27)
   */
  Encoder ENCODER{26, 27};

  /**
   * Setup the motor
   */
  void setup() {
    pinMode(BARRIER_LEFT, INPUT);
    pinMode(BARRIER_RIGHT, INPUT);
    pinMode(CUP_LEFT, OUTPUT);
    pinMode(CUP_RIGHT, OUTPUT);
    deactivateMotor(CUP_LEFT);
    deactivateMotor(CUP_RIGHT);
  }
  
  /**
   * Move the cup to the right side and then to the middle
   */
  void toInitial(bool middle) {
    moveToBarrier(CUP_RIGHT, BARRIER_RIGHT, 886);
    ENCODER.write(0);

    // Move to the middle
    if (middle) {
      activateMotor(CUP_LEFT);
      delay(MIDDLE_FROM_RIGHT);
      deactivateMotor(CUP_LEFT);
    }
  }
  
  /**
   * Move the cup to the middle (from the left side)
   * 
   * @param position where the cup currently is
   */
  void toMiddle(int position) {
    activateMotor(CUP_RIGHT);
    delay(MIDDLE_FROM_LEFT - (5 - position) * 200);
    deactivateMotor(CUP_RIGHT);
  }

  /**
   * Move the cup to a nozzle
   * 
   * @param index of the nozzle
   */
  void moveToNozzle(int index) {
    // Start the motor (if necessary)
    int value = analogRead(BARRIER_LEFT);
    if (value > NOZZLE_THRESHOLDS[index]) {
      activateMotor(CUP_LEFT);
  
      // Watch the barrier's value and wait till it falls below a certain value
      while (value > NOZZLE_THRESHOLDS[index]) {
        value = analogRead(BARRIER_LEFT);
      }
    
      // Stop the motor
      deactivateMotor(CUP_LEFT);
    } else if (value < NOZZLE_THRESHOLDS[index]) {
      activateMotor(CUP_RIGHT);
  
      // Watch the barrier's value and wait till it falls below a certain value
      while (value < NOZZLE_THRESHOLDS[index]) {
        value = analogRead(BARRIER_LEFT);
      }
    
      // Stop the motor
      deactivateMotor(CUP_RIGHT);
    }
  }
};
  
struct MotorRoundel {
  /**
   * The pin of the barrier
   */
  int BARRIER = A3;
  /**
   * The start offset to the first bottle
   */
  int START_OFFSET = 140;
  int POS[6] = {140, 500, 850, 1186, 1528, 1881};
  /**
   * The encoder (GREEN = 2, WHITE = 3)
   */
  Encoder ENCODER{2, 3};
  
  /**
   * Setup the motor
   */
  void setup() {
    pinMode(BARRIER, INPUT);
    pinMode(ROUNDEL_RIGHT, OUTPUT);
    pinMode(ROUNDEL_LEFT, OUTPUT);
    deactivateMotor(ROUNDEL_RIGHT);
    deactivateMotor(ROUNDEL_LEFT);
  }

  /**
   * Shake the roundel to make sure
   * that the spender is refilled
   */
  void shake() {
    int startValue = ENCODER.read();
    int delayTime = 300;
    // Move roundel to the right
    activateMotor(ROUNDEL_RIGHT);
    int current = ENCODER.read();
    while (current < startValue + 100) current = ENCODER.read();
    deactivateMotor(ROUNDEL_RIGHT);
    delay(delayTime);

    // Move roundel to the left
    activateMotor(ROUNDEL_LEFT);
    current = ENCODER.read();
    while (current > startValue - 100) current = ENCODER.read();
    deactivateMotor(ROUNDEL_LEFT);
    delay(delayTime);

    // Move roundel to the start
    activateMotor(ROUNDEL_RIGHT);
    current = ENCODER.read();
    while (current < startValue - 15) current = ENCODER.read();
    deactivateMotor(ROUNDEL_RIGHT);
    delay(delayTime);
  }

  /**
   * Move the roundel to a certain bottle
   * 
   * @param index of the bottle
   */
  void toBottle(int index) {
    // Get the threshold
    int threshold = POS[index];

    // Start the motor (if necessary)
    int value = ENCODER.read();
    if (value < threshold - 15) {
      activateMotor(ROUNDEL_RIGHT);
  
      // Watch the barrier's value and wait till it falls below a certain value
      while (value < threshold - 15) {
        value = ENCODER.read();
      }
  
      // Stop the motor
      deactivateMotor(ROUNDEL_RIGHT);
    } else if (value > threshold + 15) {
      activateMotor(ROUNDEL_LEFT);
  
      // Watch the barrier's value and wait till it falls below a certain value
      while (value > threshold + 15) {
        value = ENCODER.read();
      }
  
      // Stop the motor
      deactivateMotor(ROUNDEL_LEFT);
    }
  }
  
  /**
   * Move the roundel to the start position
   */
  void toInitial() {
    // Move to inital pose (including offset)
    moveToBarrier(ROUNDEL_RIGHT, BARRIER, 700);
    ENCODER.write(0);
    activateMotor(ROUNDEL_RIGHT);
    int value = ENCODER.read();
    while(value < 67) {
      value = ENCODER.read();
    }
    deactivateMotor(ROUNDEL_RIGHT);
    ENCODER.write(0);
    toBottle(0);
  }
};
  
struct MotorShift {
  /**
   * The pin of the barrier
   */
  int BARRIER = A2;
  /**
   * The encoder (GREEN = 4, WHITE = 5)
   */
  Encoder ENCODER{22, 23};
  /**
   * The shift motor's safety variables
   */
  boolean shiftDown = false, shiftUp = false;

  /**
   * Setup the motor
   */
  void setup() {
    pinMode(BARRIER, INPUT);
    pinMode(SHIFT_DOWN, OUTPUT);
    pinMode(SHIFT_UP, OUTPUT);
    deactivateMotor(SHIFT_DOWN);
    deactivateMotor(SHIFT_UP);
  }
  
  /**
   * Move the shift motor down
   */
  void toInitial() {
    moveToBarrier(SHIFT_DOWN, BARRIER, 800);
    ENCODER.write(0);
  }

  /**
   * Output one shot
   */
  void halfShot() {
    activateMotor(SHIFT_UP);
    delay(1050);
    deactivateMotor(SHIFT_UP);
    delay(1000);
    toInitial();
  }

  /**
   * Output one shot
   */
  void fullShot() {
    activateMotor(SHIFT_UP);
    delay(1050);
    deactivateMotor(SHIFT_UP);
    delay(3000);
    activateMotor(SHIFT_DOWN);
    toInitial();
  }
};

/**
 * The roundel motor
 */
MotorShift motorShift;
/**
 * The roundel motor
 */
MotorRoundel motorRoundel;
/**
 * The motor for moving the cup
 */
MotorCup motorCup;

/**
 * Activate or deactivate a certain motor
 */
String changeMotorState(String action) {
  if (action == "shift_up") {
    motorShift.fullShot();
  }
  else if (action == "shift_down") {
    motorShift.toInitial();
  }
  else if (action == "roundel_left") {
    if (digitalRead(ROUNDEL_LEFT) == HIGH) {
      activateMotor(ROUNDEL_LEFT);
      return "PAUSE_AVAILABLE";
    } else {
      deactivateMotor(ROUNDEL_LEFT);
    }
  }
  else if (action == "roundel_initial") {
    motorRoundel.toInitial();
  }
  else if (action == "roundel_right") {
    if (digitalRead(ROUNDEL_RIGHT) == HIGH) {
      activateMotor(ROUNDEL_RIGHT);
      return "PAUSE_AVAILABLE";
    } else {
      deactivateMotor(ROUNDEL_RIGHT);
    }
  }
  else if (action == "cup_left") {
    motorCup.moveToNozzle(5);
  }
  else if (action == "cup_middle") {
    motorCup.toInitial(true);
  }
  else if (action == "cup_right") {
    motorCup.toInitial(false);
  }
  return "";
}

#endif
