# Beep Beep Boop

## Prompt

In this problem, you will make your terminal beep. It should also convince you that even ASCII is a binary encoding, and incidentally expose you to some concepts related to the tty interface.

You know you have succeeded if you can run a program that you wrote, and when you press a number key (e.g. "3") your terminal should beep that many times. Other inputs should have no result. Ideally you should be able to terminate your program with a SIGINT (^C for most people) and your terminal should not be borked afterwards.

If you run into surprising issues and are using a fancy terminal emulator, you may want to try using another, such as Terminal.app on MacOS.

Some minor hints:

- ASCII has a bell character 🔔
- You may need to change your terminal control mode in your program, in order to read keystrokes directly

Some MAJOR spoilers:

- The bell character in ASCII is `0x07` 
- An ideal terminal control mode is cbreak as it will allow direct reading of keystrokes while also correctly interpreting keystrokes that correspond to signals

As usual, please comment below or on Discord if you have questions. Good luck!

## Instruction

To build: `mvn clean install`. To run: `java -jar ./target/beep-beep-1.0-SNAPSHOT.jar`.