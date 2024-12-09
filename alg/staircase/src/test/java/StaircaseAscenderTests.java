import org.junit.jupiter.api.Test;
import org.odl.StaircaseAscender;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class StaircaseAscenderTests {

  private final StaircaseAscender ascender = new StaircaseAscender();

  @Test
  public void test_1_3() {
    int numWays = ascender.countWays(0, 1, 3);
    assertEquals(1, numWays);
  }

  @Test
  public void test_2_3() {
    int numWays = ascender.countWays(0, 2, 3);
    assertEquals(2, numWays);
  }

  @Test
  public void test_3_3() {
    int numWays = ascender.countWays(0, 3, 3);
    assertEquals(4, numWays);
  }

  @Test
  public void test_4_3() {
    int numWays = ascender.countWays(0, 4, 3);
    assertEquals(7, numWays);
  }

  @Test
  public void test_5_3() {
    int numWays = ascender.countWays(0, 5, 3);
    assertEquals(13, numWays);
  }

  @Test
  public void test_10000_5() {
    int numWays = ascender.countWays(0, 10000, 5);
    assertEquals(1069936800, numWays);
  }
}
