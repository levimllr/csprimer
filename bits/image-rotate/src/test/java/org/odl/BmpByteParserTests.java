package org.odl;

import org.junit.jupiter.api.Test;
import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class BmpByteParserTests {

  private final BmpByteParser parser = new BmpByteParser("src/test/resources/teapot.bmp");

  public BmpByteParserTests() throws IOException {
  }

  @Test
  public void printsCorrectBytes() {
    // given, when, & then
    String expected = "42 4D ";
    String actual = parser.printFirstN(2);

    assertEquals(expected, actual);
  }

  @Test
  public void determinesCorrectOffset() {
    // given, when, & then
    int expected = 138;
    int actual = parser.extractOffsetIndex();
    assertEquals(expected, actual);
  }

  @Test
  public void determinesCorrectWidth() {
    // given, when, & then
    int expected = 420;
    int actual = parser.extractWidth();
    assertEquals(expected, actual);
  }

  @Test
  public void determinesCorrectHeight() {
    // given, when, & then
    int expected = 420;
    int actual = parser.extractHeight();
    assertEquals(expected, actual);
  }

  @Test
  public void outputsBlackBmp() throws IOException {
    parser.outputBlack();
  }

  @Test
  public void outputsRotatedClockwise90() throws IOException {
    parser.outputRotatedClockwise90();
  }

  @Test void transpose() {
    // given
    Pixel one = new Pixel((byte) 0x01, (byte) 0x01, (byte) 0x01);
    Pixel two = new Pixel((byte) 0x01, (byte) 0x01, (byte) 0x01);
    Pixel three = new Pixel((byte) 0x01, (byte) 0x01, (byte) 0x01);
    Pixel four = new Pixel((byte) 0x01, (byte) 0x01, (byte) 0x01);

    Pixel[][] original = {{one, two}, {three, four}};

    // when
    Pixel[][] actual = parser.transpose(original);

    // then
    Pixel[][] expected = {{one, three}, {two, four}};
    for (int j = 0; j < (expected.length/2 + 1); j++) {
      for (int i = 0; i < (expected[0].length/2 + 1); i++) {
        assertEquals(expected[j][i], actual[j][i]);
      }
    }
  }

  @Test void reverseColumn() {
    // given
    Pixel one = new Pixel((byte) 0x01, (byte) 0x01, (byte) 0x01);
    Pixel two = new Pixel((byte) 0x01, (byte) 0x01, (byte) 0x01);
    Pixel three = new Pixel((byte) 0x01, (byte) 0x01, (byte) 0x01);
    Pixel four = new Pixel((byte) 0x01, (byte) 0x01, (byte) 0x01);

    Pixel[][] original = {{one, two}, {three, four}};

    // when
    Pixel[][] actual = parser.reverseColumns(original);

    // then
    Pixel[][] expected = {{three, four}, {one, two}};
    for (int j = 0; j < (expected.length/2 + 1); j++) {
      for (int i = 0; i < (expected[0].length/2 + 1); i++) {
        assertEquals(expected[j][i], actual[j][i]);
      }
    }
  }
}
