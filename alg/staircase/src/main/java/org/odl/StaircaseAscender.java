package org.odl;

import java.util.HashMap;
import java.util.Map;

public class StaircaseAscender {

  private final Map<Integer, Integer> memo = new HashMap<>();

  public int countWays(int currentStep, int totalSteps, int maxStep) {
    if (memo.containsKey(currentStep)) {
      return memo.get(currentStep);
    } else if (currentStep > totalSteps) {
      memo.put(currentStep, 0);
      return 0;
    } else if (currentStep == totalSteps) {
      memo.put(currentStep, 1);
      return 1;
    } else {
      int numWays = 0;
      for (int stepSize = 1; stepSize <= maxStep; stepSize++) {
        numWays += countWays(currentStep + stepSize, totalSteps, maxStep);
      }
      memo.put(currentStep, numWays);
      return numWays;
    }
  }
}
