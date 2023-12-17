package org.plmiller;

import org.junit.jupiter.api.Test;
import org.opentest4j.AssertionFailedError;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

public class ProtobufEncoderTests {

    private static final long maxLong = 0xFFFFFFFFFFFFFFFFL;

    private final ProtobufEncoder encoder = new ProtobufEncoder();

    private final Map<String, Long> testParams = Map.of(
            "src/test/resources/1.uint64", 1L,
            "src/test/resources/150.uint64", 150L,
            "src/test/resources/maxint.uint64", -1L
    );

    @Test
    public void encode1() {
        byte[] actual = encoder.encode(1);
        byte[] expected = new byte[] { (byte) 0x01 };
        assertArrayEquals(expected, actual);
    }

    @Test
    public void encode150() {
        byte[] actual = encoder.encode(150);
        byte[] expected = new byte[] { (byte) 0x96, (byte) 0x01 };
        assertArrayEquals(expected, actual);
    }

    @Test
    public void encode256() {
        byte[] actual = encoder.encode(256);
        byte[] expected = new byte[] { (byte) 0x80, (byte) 0x02 };
        assertArrayEquals(expected, actual);
    }

    @Test
    public void encodeMax() {
        // Long.MIN_VALUE is maxed because it's signed in Java :\
        byte[] actual = encoder.encode(maxLong);
        byte[] expected = new byte[]{
                (byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF,
                (byte) 0xFF, (byte) 0xFF, (byte) 0x01 };
        assertArrayEquals(expected, actual);
    }

    @Test
    public void encodeDecode1() {
        assertEquals(1, encoder.decode(encoder.encode(1)));
    }

    @Test
    public void encodeDecode150() {
        assertEquals(150, encoder.decode(encoder.encode(150)));
    }

    @Test
    public void encodeDecode256() {
        assertEquals(256, encoder.decode(encoder.encode(256)));
    }

    @Test
    public void encodeDecodeMax() {
        assertEquals(maxLong, encoder.decode(encoder.encode(maxLong)));
    }


    @Test
    public void encodeDecodeAll() {
        List<Long> failedLongs = new ArrayList<>();
        for (long l = 0; l != 2056; l++) {
            try {
                assertEquals(l, encoder.decode(encoder.encode(l)));
            } catch (AssertionFailedError e) {
                failedLongs.add(l);
            }
        }
        System.out.println(failedLongs);
    }
}

