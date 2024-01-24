package org.odl;

import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class ColorCodeConverterTests {

    private final ColorCodeConverter converter = new ColorCodeConverter();

    @Test
    public void convertSimpleFile() throws IOException {
        // given & when
        String actual = converter.convertFile("src/test/resources/simple.css");
        // then
        String expected = converter.loadFile("src/test/resources/simple_expected.css");
        assertEquals(expected, actual);
    }

    @Test
    public void convertAdvancedFile() throws IOException {
        // given & when
        String actual = converter.convertFile("src/test/resources/advanced.css");
        // then
        String expected = converter.loadFile("src/test/resources/advanced_expected.css");
        assertEquals(expected, actual);
    }

    @Test
    public void convert0() {
        // given
        byte zeroByte = (byte) 0x00;
        // when
        int actual = converter.convertHexToRgb(zeroByte);
        // then
        assertEquals(0, actual);
    }

    @Test
    public void convertComplicated() {
        // given
        byte zeroByte = (byte) 0xa8;
        // when
        int actual = converter.convertHexToRgb(zeroByte);
        // then
        assertEquals(168, actual);
    }

    @Test void convert255() {
        // given
        byte zeroByte = (byte) 0xff;
        // when
        int actual = converter.convertHexToRgb(zeroByte);
        // then
        assertEquals(255, actual);
    }

    @Test
    public void loadValidCss() throws IOException {
        // given & when
        String cssFile = converter.loadFile("src/test/resources/simple.css");
        // then
        assertTrue(cssFile.contains("color: #fe030a;"));
    }
}
