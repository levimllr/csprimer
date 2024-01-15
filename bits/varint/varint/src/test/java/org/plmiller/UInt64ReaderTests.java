package org.plmiller;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class UInt64ReaderTests {

    private final Map<String, Long> testParams = Map.of(
            "src/test/resources/1.uint64", 1L,
            "src/test/resources/150.uint64", 150L,
            "src/test/resources/maxint.uint64", -1L
    );

    @Test
    public void read() throws IOException {
        for (Map.Entry<String, Long> testParam : testParams.entrySet()) {
            long expected = testParam.getValue();
            long actual = UInt64Reader.read(testParam.getKey());
            assertEquals(expected, actual);
        }
    }
}
