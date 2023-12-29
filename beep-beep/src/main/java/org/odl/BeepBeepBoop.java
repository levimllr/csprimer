package org.odl;

import java.util.InputMismatchException;
import java.util.Scanner;
import java.util.concurrent.TimeUnit;

public class BeepBeepBoop {

  public static void main(String[] args) throws InterruptedException {

    int min = 0;
    int max = 100;
    int timesToBeep = 0;
    boolean isInputInvalid = true;

    while (isInputInvalid) {
      try {
        System.out.printf("Beep boop! Please enter an integer between %s and %s%n", min, max);

        Scanner sc = new Scanner(System.in);
        int i = sc.nextInt();

        if (i >= min && i <= max) {
          isInputInvalid = false;
          timesToBeep = i;
        }
      } catch (InputMismatchException ignored) {
        // Stay in the while loop.
      } finally {
        System.out.print("\n");
      }
    }

    for (int j = 1; j <= timesToBeep; j++) {
      if (j % 3 == 0) {
        System.out.print("boop");
      } else {
        System.out.print("beep");
      }
      // Ensures ASCII character is output, which triggers "bell".
      System.out.println("\07");
      // Sleep, so separate beeps are discernible.
      TimeUnit.MILLISECONDS.sleep(250);
    }

    System.out.print("\nAll done!\n");
  }
}