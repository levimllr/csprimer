import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import org.junit.jupiter.api.Test;
import org.odl.Utf8Truncator;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;

public class Utf8TruncatorTest {

  @Test
  void testTruncate() throws IOException {
    // GIVEN & WHEN
    byte[] actual = Utf8Truncator.truncate("src/test/resources/cases");

    // THEN
    byte[] expected = Files.readAllBytes(Paths.get("src/test/resources/expected"));
    assertArrayEquals(expected, actual);
  }
}
