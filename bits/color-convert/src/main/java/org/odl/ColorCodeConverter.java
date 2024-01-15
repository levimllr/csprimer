package org.odl;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HexFormat;
import java.util.List;
import java.util.regex.MatchResult;
import java.util.regex.Pattern;

public class ColorCodeConverter {

    private final HexFormat hexFormat = HexFormat.of();

    /**
     * Converts hex color codes to RGBA color codes in .css file.
     * @param path to .css file
     * @return String of converted file
     * @throws IOException if path to file is incorrect
     */
    public String convertFile(String path) throws IOException {
        try {
            // Load file into String and normalize to lower case chars.
            String cssFile = loadFile(path).toLowerCase();

            // Regex for matching CSS color hex patterns. Source: https://stackoverflow.com/a/63856391
            Pattern pattern = Pattern.compile("#[a-f\\d]{3}(?:[a-f\\d]?|(?:[a-f\\d]{3}(?:[a-f\\d]{2})?)?)\\b");

            // Replace matches with converted values
            String convertedFile = pattern.matcher(cssFile).replaceAll(this::convertHexToRgba);

            System.out.println(convertedFile);

            return convertedFile;
        } catch (Exception e) {
            System.out.println("ERROR: " + e.getMessage());
            throw e;
        }
    }

    /**
     * Converts hex color code string to RGBA string For use as lambda to operate on MatchResult collection.
     * @param matchResult of CSS color hex regex pattern. Ex: "#43ff64d9"
     * @return RGBA pattern. Ex: "rgba(67 255 100 / 0.85);"
     */
    String convertHexToRgba(MatchResult matchResult) {
        String hexString = matchResult.group();
        int n = hexString.length() - 1;
        boolean isAlpha = n == 4 || n == 8;

        if (n == 3 || n == 4) {
            return convertHexBytesToString(hexFormat.parseHex(expandHexString(hexString.substring(1))), isAlpha);
        } else {
            return convertHexBytesToString(hexFormat.parseHex(hexString.substring(1)), isAlpha);
        }
    }

    int convertHexToRgb(byte hexByte) {
        return Byte.toUnsignedInt(hexByte);
    }

    String loadFile(String path) throws IOException {
        if (path.endsWith(".css")) {
            return Files.readString(Path.of(path));
        } else {
            throw new IllegalArgumentException("Path must be to .css file");
        }
    }

    /**
     * Expands compacted hex notation.
     * @param hexString Ex: F48
     * @return Ex: FF4488
     */
    private String expandHexString(String hexString) {
        StringBuilder sb = new StringBuilder();
        for (char hexChar : hexString.toCharArray()) {
            sb.append(hexChar);
            sb.append(hexChar);
        }
        return sb.toString();
    }

    /**
     * Converts array of bytes representing hex color values to RGBA string
     * @param hexBytes Ex: [0xFF, 0xFF, 0xFF]
     * @param isAlpha Whether the byte array includes a value for Alpha. In the previous example, this would be false.
     * @return Ex: "rgb(255 255 255);"
     */
    private String convertHexBytesToString(byte[] hexBytes, boolean isAlpha) {

        List<String> rgbValues = new ArrayList<>();
        for (byte hexByte : hexBytes) {
            rgbValues.add(String.valueOf(convertHexToRgb(hexByte)));
        }
        if (isAlpha) {
            int lastIndex = rgbValues.size() - 1;
            DecimalFormat format = new DecimalFormat("0.00000");
            return "rgba(" +
                    String.join(" ", rgbValues.subList(0, lastIndex)) +
                    " / " +
                    format.format(Float.parseFloat(rgbValues.get(lastIndex)) / 255F) +
                    ")";
        } else {
            return "rgb(" +
                    String.join(" ", rgbValues) +
                    ")";
        }
    }
}
