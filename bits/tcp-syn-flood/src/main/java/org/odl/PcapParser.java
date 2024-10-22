package org.odl;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class PcapParser {

  public static final byte[] expectedMagicNumber =
      new byte[] {(byte) 0xa1, (byte) 0xb2, (byte) 0xc3, (byte) 0xd4};
  public static final int pcapMajorVersion = 2;
  public static final int pcapMinorVersion = 4;
  public static final int timezoneOffset = 0;
  public static final int timestampAccuracy = 0;
  public static final int loopbackLinkLayerHeaderType = 0;
  public static final int savefileHeaderLength = 24;
  public static final int minimumIpv4HeaderLength = 20;
  public static final int linkLayerHeaderLength = 4;
  public static final int packetHeaderPropertyLength = 4;

  private final byte[] fileBytes;
  private final List<Packet> packets = new ArrayList<>();
  private final float ackPercentage;

  private ByteOrder byteOrder;

  public PcapParser(String filePath) throws IOException {
    this.fileBytes = Files.readAllBytes(Paths.get(filePath));
    validateSavefileHeader();
    parsePacketHeaders();
    ackPercentage = determineSynAckPercentage();
  }

  public List<Packet> getPackets() {
    return packets;
  }

  public float getAckPercentage() {
    return ackPercentage;
  }

  public float determineSynAckPercentage() {
    long initiated = packets.stream().filter(p -> p.destinationPort == 80 && p.hasSyn).count();
    long acked = packets.stream().filter(p -> p.sourcePort == 80 && p.hasAck).count();
    return (float) acked / initiated * 100;
  }

  /**
   * Validate that file adheres to specification in man pcap-savefile:
   * <br>
   * "The per-file header length is 24 octets."
   * - 4-byte magic number with value 0xa1b2c3d4 (actually reversed here since little-endian)
   * - 2-byte file format major version number (should be 2)
   * - 2-byte file format minor version number (should be 4)
   * - 4-byte timezone offset; always 0
   * - 14-byte number giving accuracy of timestamps in file; always 0
   * - 24-byte number giving link-layer header type for packets in the capture
   */
  public void validateSavefileHeader() {
    byte[] actualMagicNumber = Arrays.copyOf(fileBytes, 4);
    if (Arrays.equals(actualMagicNumber, expectedMagicNumber)) {
      byteOrder = ByteOrder.BIG_ENDIAN;
    } else if (ByteBuffer.wrap(actualMagicNumber, 0, 4).getInt() ==
        ByteBuffer.wrap(expectedMagicNumber, 0, 4).order(ByteOrder.LITTLE_ENDIAN).getInt()) {
      byteOrder = ByteOrder.LITTLE_ENDIAN;
    } else {
      throw new IllegalArgumentException("PCAP savefile does not start with magic number.");
    }

    if (Short.toUnsignedInt(ByteBuffer.wrap(fileBytes, 4, 2).order(byteOrder).getShort())
        != pcapMajorVersion) {
      throw new IllegalArgumentException(
          String.format("PCAP savefile must use major version %s.", pcapMajorVersion));
    }
    if (Short.toUnsignedInt(ByteBuffer.wrap(fileBytes, 6, 2).order(byteOrder).getShort())
        != pcapMinorVersion) {
      throw new IllegalArgumentException(
          String.format("PCAP savefile must use minor version %s.", pcapMinorVersion));
    }
    if (ByteBuffer.wrap(fileBytes, 8, 4).order(byteOrder).getInt() != timezoneOffset) {
      throw new IllegalArgumentException(
          String.format("PCAP savefile must use time zone offset %s.", timezoneOffset));
    }
    if (ByteBuffer.wrap(fileBytes, 12, 4).order(byteOrder).getInt() != timestampAccuracy) {
      throw new IllegalArgumentException(
          String.format("PCAP savefile must use timestamp accuracy %s.", timestampAccuracy));
    }
    if (ByteBuffer.wrap(fileBytes, 20, 4).order(byteOrder).getInt()
        != loopbackLinkLayerHeaderType) {
      throw new IllegalArgumentException(
          String.format("PCAP savefile must use link layer header type %s.",
              loopbackLinkLayerHeaderType));
    }
  }

  /**
   * Determine the time and size of each packet.
   */
  public void parsePacketHeaders() {
    int offset = savefileHeaderLength;
    boolean isLastPacket = false;
    while (!isLastPacket) {
      // We only need to grab one timestamp from the packet header.
      Instant timestamp = Instant.ofEpochSecond(
          ByteBuffer.wrap(fileBytes,
                  offset,
                  packetHeaderPropertyLength)
              .order(byteOrder)
              .getInt());
      offset += packetHeaderPropertyLength * 2; // 2 timestamp properties
      int capturedDataLength =
          ByteBuffer.wrap(fileBytes,
                  offset,
                  packetHeaderPropertyLength)
              .order(byteOrder)
              .getInt();
      offset += packetHeaderPropertyLength; // 1 length property
      int untruncatedDataLength =
          ByteBuffer.wrap(fileBytes,
                  offset,
                  packetHeaderPropertyLength)
              .order(byteOrder)
              .getInt();
      offset += packetHeaderPropertyLength; // 1 length property
      if (capturedDataLength != untruncatedDataLength) {
        throw new IllegalArgumentException("Packet data must be untruncated.");
      }
      validateLinkLayerHeader(offset);
      validateIpv4Header(offset);
      packets.add(
          new Packet(
              timestamp,
              parseSourcePort(offset),
              parseDestinationPort(offset),
              parseAck(offset),
              parseSyn(offset))
      );
      offset += capturedDataLength;
      if (offset + 1 >= fileBytes.length) {
        isLastPacket = true;
      }
    }
  }

  private int parseSourcePort(int offset) {
    return ByteBuffer.wrap(fileBytes,
            offset + linkLayerHeaderLength + minimumIpv4HeaderLength,
            2)
        .order(ByteOrder.BIG_ENDIAN) // TCP network byte order is big-endian
        .getShort();
  }

  private int parseDestinationPort(int offset) {
    return ByteBuffer.wrap(fileBytes,
            // Offset + other headers + source port
            offset + linkLayerHeaderLength + minimumIpv4HeaderLength + 2,
            2)
        .order(ByteOrder.BIG_ENDIAN) // TCP network byte order is big-endian
        .getShort();
  }

  private boolean parseAck(int offset) {
    return (ByteBuffer.wrap(fileBytes,
            // Offset + other headers + source, dest ports + seq, ack numbers + data offset and res
            offset + linkLayerHeaderLength + minimumIpv4HeaderLength + 2 + 2 + 4 + 4 + 1,
            1)
        .order(ByteOrder.BIG_ENDIAN) // TCP network byte order is big-endian
        .get() & 0x10) == 0x10; // i.e. 0001 0000
  }

  private boolean parseSyn(int offset) {
    return (ByteBuffer.wrap(fileBytes,
            // Offset + other headers + source, dest ports + seq, ack numbers + data offset and res
            offset + linkLayerHeaderLength + minimumIpv4HeaderLength + 2 + 2 + 4 + 4 + 1,
            1)
        .order(ByteOrder.BIG_ENDIAN) // TCP network byte order is big-endian
        .get() & 0x02) == 0x02; // i.e. 0000 0010
  }

  /**
   * Validate that packets captured over loopback interface are IPv4.
   */
  public void validateLinkLayerHeader(int offset) {
    int linkLayerHeader =
        ByteBuffer.wrap(fileBytes,
                offset,
                linkLayerHeaderLength)
            .order(byteOrder)
            .getInt();
    // 2 indicates payload is IPv4 packet. See https://www.tcpdump.org/linktypes/LINKTYPE_NULL.html.
    if (linkLayerHeader != 2) {
      throw new IllegalArgumentException("PCAP link layer header must be 2 (IPv4).");
    }
  }

  /**
   * Validate IPV4 header.
   */
  public void validateIpv4Header(int offset) {
    int ipv4Ihl =
        (ByteBuffer.wrap(fileBytes,
                offset + linkLayerHeaderLength,
                1)
            .order(ByteOrder.BIG_ENDIAN) // IPv4 network byte order is big-endian
            /*
             * Take the last 4 bits (the IHL value). See https://en.wikipedia.org/wiki/IPv4#Header
             * Since IHL value represents length of header in 32-bit (i.e. 4-byte) words,
             * multiply by 4 (i.e. bit shift by 2 since 2^2 = 4) to convert to bytes.
             */
            .get() & 0x0F) << 2;
    // Assume there are no IPv4 options
    if (ipv4Ihl != minimumIpv4HeaderLength) {
      throw new IllegalArgumentException("PCAP IPv4 header must be minimum length.");
    }
  }

  public record Packet(
      Instant timestamp,
      int sourcePort,
      int destinationPort,
      boolean hasAck,
      boolean hasSyn) {
  }
}