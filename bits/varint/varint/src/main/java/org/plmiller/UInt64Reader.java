package org.plmiller;

import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class UInt64Reader {

    public static long read(String pathToFile) throws IOException {
        Path path = Paths.get(pathToFile);
        byte[] fileContents = Files.readAllBytes(path);
        return ByteBuffer.wrap(fileContents).getLong();
    }
}
