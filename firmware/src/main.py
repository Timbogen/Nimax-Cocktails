import os
import time
from src.test import whatever, whatever1

# Path to control the built-in LED (typically led0 or act)
LED_PATH = "/sys/class/leds/ACT"  # Adjust if necessary for your Pi model


# Function to write to a file
def write_led_file(file_name, value):
    with open(os.path.join(LED_PATH, file_name), "w") as f:
        f.write(value)


def blink_led(delay=0.2, count=100):
    """
    Blinks the built-in LED on the Raspberry Pi.

    Args:
        delay (float): Time in seconds between blinks.
        count (int): Number of times to blink the LED.
    """
    try:
        # Set LED trigger to none for manual control
        write_led_file("trigger", "none")
        for _ in range(count):
            # Turn the LED on
            write_led_file("brightness", "1")
            time.sleep(delay)
            # Turn the LED off
            write_led_file("brightness", "0")
            time.sleep(delay)
    finally:
        # Restore default trigger
        write_led_file("trigger", "mmc0")  # mmc0 is the default for most Pi boards


def start():
    """Let the LED blink"""
    whatever()
    whatever()
    whatever()
    whatever1()
    blink_led()
