package org.odl;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Utf8Truncator {

  private static final byte NEW_LINE = (byte) '\n';
  private static final byte UTF8_CONT_PATTERN = (byte) 0b1000_0000;
  private static final byte UTF8_CONT_MASK = (byte) 0b1100_0000;

  public static byte[] truncate(String pathToCases) throws IOException {
    List<Case> cases = parseCases(Files.readAllBytes(Paths.get(pathToCases)));

    List<byte[]> output = new ArrayList<>();
    for (Case c : cases) {
      int smoothTruncLength = c.truncatedLength;
      if (smoothTruncLength >= c.bytes.length) {
        smoothTruncLength = c.bytes.length;
      } else {
        // Back up if the current byte is a continuation byte.
        while (smoothTruncLength > 0
            && (c.bytes[smoothTruncLength] & UTF8_CONT_MASK) == UTF8_CONT_PATTERN) {
          smoothTruncLength -= 1;
        }
      }
      output.add(Arrays.copyOfRange(c.bytes, 0, smoothTruncLength));
    }
    return flattenWithNewline(output);
  }

  private static  List<Case> parseCases(byte[] rawFile) {
    List<Case> cases = new ArrayList<>();
    ByteArrayOutputStream stream = new ByteArrayOutputStream();
    for (byte b : rawFile) {
      if (b == NEW_LINE) {
        cases.add(new Case(stream.toByteArray()));
        stream = new ByteArrayOutputStream();
      } else {
        stream.write(b);
      }
    }
    return cases;
  }

  private static byte[] flattenWithNewline(List<byte[]> chunks) {
    ByteArrayOutputStream stream = new ByteArrayOutputStream();
    for (byte[] chunk : chunks) {
      stream.writeBytes(chunk);
      stream.write('\n');
    }
    return stream.toByteArray();
  }

  public static class Case {
    private final int truncatedLength;
    private final byte[] bytes;

    private Case(byte[] rawBytes) {
      this.truncatedLength = Byte.toUnsignedInt(rawBytes[0]);
      this.bytes = Arrays.copyOfRange(rawBytes, 1, rawBytes.length);
    }
  }
}
