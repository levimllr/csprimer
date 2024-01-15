package org.odl;

import java.io.IOException;
import java.math.BigInteger;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;

public class BmpByteParser {

  private final byte[] fileBytes;
  private final String filePath;
  private final int offsetIndex;
  private final int width;
  private final int height;

  public BmpByteParser(String filePath) throws IOException {
    this.fileBytes = Files.readAllBytes(Path.of(filePath));
    this.filePath = filePath;
    this.offsetIndex = extractOffsetIndex();
    this.width = extractWidth();
    this.height = extractHeight();
  }

  public int extractOffsetIndex() {
    return new BigInteger(reverse(getOffset())).intValue();
  }

  /**
   * According to <a href="https://en.wikipedia.org/wiki/BMP_file_format">BMP file format</a>, the Bitmap image width is
   * specified in 4 bytes starting at hex offset 12 (i.e. decimal offset 18).
   *
   * @return the above
   */
  public int extractWidth() {
    return new BigInteger(reverse(copyBytes(18, 22))).intValue();
  }

  /**
   * According to <a href="https://en.wikipedia.org/wiki/BMP_file_format">BMP file format</a>, the Bitmap image width is
   * specified in 4 bytes starting at hex offset 16 (i.e. decimal offset 22).
   *
   * @return the above
   */
  public int extractHeight() {
    return new BigInteger(reverse(copyBytes(22, 26))).intValue();
  }

  public void outputBlack() throws IOException {
    int colorSize = fileBytes.length - offsetIndex;
    byte[] blackBytes = new byte[colorSize];
    Arrays.fill(blackBytes, (byte) 0x00);
    output(blackBytes, "./src/test/resources/out_black.bmp");
  }

  public void outputRotatedClockwise90() throws IOException {

    // 1. Copy image bytes
    byte[] pixelBytes = copyBytes(offsetIndex, fileBytes.length);
    // 2. Transform into 2D array of Pixels
    Pixel[][] pixelMatrix = transformByteLineToPixelArray(pixelBytes);
    // 3. Transpose matrix & 4. Reverse columns
    Pixel[][] columnReversedMatrix = reverseColumns(transpose(pixelMatrix));
    // 5. Flatten into 1D array
    int k = 0;
    for (Pixel[] reversedMatrix : columnReversedMatrix) {
      for (int i = 0; i < columnReversedMatrix[0].length; i++) {
        pixelBytes[k] = reversedMatrix[i].blue();
        pixelBytes[k + 1] = reversedMatrix[i].green();
        pixelBytes[k + 2] = reversedMatrix[i].red();
        k += 3;
      }
    }
    // 5 & 6. Output
    output(pixelBytes, "./src/test/resources/out_rotate-cw-90.bmp");
  }

  Pixel[][] transpose(Pixel[][] oldBytes) {
    Pixel[][] newBytes = new Pixel[oldBytes.length][oldBytes[0].length];
    for (int j = 0; j < newBytes.length; j++) {
      for (int i = 0; i < newBytes[0].length; i++) {
        newBytes[j][i] = oldBytes[i][j];
        newBytes[i][j] = oldBytes[j][i];
      }
    }
    return newBytes;
  }

  Pixel[][] reverseColumns(Pixel[][] oldBytes) {
    Pixel[][] newBytes = new Pixel[oldBytes.length][oldBytes[0].length];
    for (int j = 0; j < newBytes.length / 2; j++) {
      for (int i = 0; i < newBytes[0].length; i++) {
        newBytes[j][i] = oldBytes[newBytes.length - j - 1][i];
        newBytes[newBytes.length - j - 1][i] = oldBytes[j][i];
      }
    }
    return newBytes;
  }

  String printFirstN(int n) {
    if (n > fileBytes.length - 1) {
      throw new IllegalArgumentException("n is greater than number of bytes.");
    }

    StringBuilder byteString = new StringBuilder();
    for (int i = 0; i < n; i++) {
      /*
       * Source: https://stackoverflow.com/a/2817883. Formatter syntax: %[flags][width]conversion
       * Flag '0' - The result will be zero-padded. Width 2.
       * Conversion 'X' - The result is formatted as a hexadecimal integer, uppercase
       */
      byteString.append(String.format("%02X ", fileBytes[i]));
    }
    System.out.printf("First two bytes of %s: %s%n", filePath, byteString);
    return byteString.toString();
  }

  private Pixel[][] transformByteLineToPixelArray(byte[] pixelBytes) {
    Pixel[][] pixelMatrix = new Pixel[height][width];
    for (int k = 0; k < pixelBytes.length; k += 3) {
      int j = (k / 3) / width;
      int i = (k / 3) % width;
      pixelMatrix[j][i] = new Pixel(pixelBytes[k], pixelBytes[k + 1], pixelBytes[k + 2]);
    }
    return pixelMatrix;
  }

  private byte[] reverse(byte[] byteArray) {
    byte[] reverseArray = new byte[byteArray.length];
    for (int i = 0; i < byteArray.length / 2; i++) {
      reverseArray[i] = byteArray[byteArray.length - i - 1];
      reverseArray[byteArray.length - i - 1] = byteArray[i];
    }
    return reverseArray;
  }

  /**
   * According to <a href="https://en.wikipedia.org/wiki/BMP_file_format">BMP file format</a>, the offset, or starting
   * address, is located at byte indices 10-13.
   *
   * @return the above
   */
  private byte[] getOffset() {
    return copyBytes(10, 14);
  }

  private byte[] copyBytes(int start, int end) {
    return Arrays.copyOfRange(fileBytes, start, end);
  }

  private void output(byte[] bytes, String path) throws IOException {
    byte[] newFileBytes = new byte[fileBytes.length];
    System.arraycopy(fileBytes, 0, newFileBytes, 0, offsetIndex);
    System.arraycopy(bytes, 0, newFileBytes, offsetIndex, fileBytes.length - offsetIndex);
    Files.write(Paths.get(path), newFileBytes);
  }
}
