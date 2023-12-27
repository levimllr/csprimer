package org.plmiller;

import java.nio.ByteBuffer;
import java.util.BitSet;

public class ProtobufEncoder {

    public byte[] encode(long l) {
        // Add 1 to account for actual highest-order bit
        int highestBit = Long.numberOfTrailingZeros(Long.highestOneBit(l)) + 1;
        // There are 7 data bits per MSB/metadata bit
        int msbBits = Math.ceilDiv(highestBit, 7);
        // There are 8 bits in a byte
        int highestByte = Math.ceilDiv(highestBit + msbBits, 8);

        byte[] encodedLong = new byte[highestByte];
        for (int i = 0; i < highestByte; i++) {
            if (i == highestByte - 1) {
                // MSB is 0 for last byte, so don't set
                encodedLong[i] = (byte) ((l >>> i * 7));
            } else {
                // | 0x80 sets the MSB to 1
                encodedLong[i] = (byte) ((l >>> i * 7) | 0x80);
            }
        }
        return encodedLong;
    }

    public long decode(byte[] bs) {

        BitSet decodedBitSet = new BitSet();

        // Create BitSet excluding MSBs.
        int l = 0;
        for (byte b : bs) {
            BitSet byteBitSet = BitSet.valueOf(new byte[]{b});
            for (int j = 0; j < 7; j++) {
                decodedBitSet.set(l, byteBitSet.get(j));
                l++;
            }
        }

        // Create big-endian byte array from BitSet.
        byte[] decodedByteArray = decodedBitSet.toByteArray();
        // Add padding for "missing" bytes. There are 8 bytes in a Java long.
        byte[] finalDecodedByteArray = new byte[8];
        int k = 0;
        while (k < finalDecodedByteArray.length - decodedByteArray.length) {
            finalDecodedByteArray[k] = (byte) 0x00;
            k++;
        }
        // Add data bytes in reverse order so they're big-endian.
        for (int j = decodedByteArray.length - 1; j >=0; j--) {
            finalDecodedByteArray[k] = decodedByteArray[j];
            k++;
        }

        return ByteBuffer.wrap(finalDecodedByteArray).getLong();
    }
}
