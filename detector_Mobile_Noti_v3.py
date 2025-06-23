import get_mini_win_minimized as ss
import Firebase as fireRDB
import time
import pytesseract
import pygame
import ctypes
import os
import sys
import atexit

# Configure Tesseract path if necessary
# Replace this with the path to your Tesseract installation if needed
pytesseract.pytesseract.tesseract_cmd = r"C:\Program Files\Tesseract-OCR\tesseract.exe"

# Ringtone to play when text is found
RINGTONE_FILE = "bhag_bhag.mp3"
TIME_DELAY = 1
TARGET_TEXT = "FLINK"
TARGET_WINDOW = 'LDPlayer'
check_count = 0

# Initialize pygame mixer for sound playback
pygame.mixer.init()

def onClose():
    fireRDB.setData('FLINK',False)
    pygame.mixer.music.stop()

def play_ringtone():
    pygame.mixer.music.load(RINGTONE_FILE)
    pygame.mixer.music.play()
    fireRDB.attachValueListener('FLINK', onClose)
    while pygame.mixer.music.get_busy():
        time.sleep(1)

def check_text_on_screen(Window_name, find_text):
    # Capture the entire screen
    screen = ss.takescreen(Window_name)
    # screen.show()
    # Convert the image to text using pytesseract
    text = pytesseract.image_to_string(screen,lang='deu')
    print("######################################################################")
    print(f"Detected text: {text}")  # For debugging
    print("######################################################################")
    
    # Check if the target text is present
    if find_text in text:
        print(f"Target text '{find_text}' found on screen!")
        fireRDB.setData('FLINK',True)
        play_ringtone()
        return True
    else:
        print(f"Target text '{find_text[:3]+"....."}' not found.")
        return False

# Constants for the execution state
ES_CONTINUOUS = 0x80000000
ES_DISPLAY_REQUIRED = 0x00000002
ES_SYSTEM_REQUIRED = 0x00000001

def run_keep_screen_on():
    """
    Prevents the screen and system from entering sleep mode.
    """
    ctypes.windll.kernel32.SetThreadExecutionState(ES_CONTINUOUS | ES_DISPLAY_REQUIRED | ES_SYSTEM_REQUIRED)
    print("Screen and system sleep mode prevented. Press Ctrl+C to stop.")
    try:
        print("Starting screen text detection...")
        while True:
            found = check_text_on_screen(TARGET_WINDOW, TARGET_TEXT)
            
            if found:
                break
            print(f"Waiting '{TIME_DELAY}' seconds before the next check...")
            global check_count
            check_count+=1
            time.sleep(TIME_DELAY)
            os.system('cls')
            print(f"Test: {check_count}")
    except KeyboardInterrupt:
        # Restore default behavior
        ctypes.windll.kernel32.SetThreadExecutionState(ES_CONTINUOUS)
        print("Screen and system sleep mode restored.")

def main():
    atexit.register(onClose)
    fireRDB.setData('FLINK',False)
    run_keep_screen_on()

if __name__ == "__main__":
    main()
