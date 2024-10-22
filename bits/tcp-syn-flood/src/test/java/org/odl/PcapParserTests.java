package org.odl;

import java.io.IOException;
import java.util.List;
import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class PcapParserTests {

  @Test
  public void parsePacketHeaders() throws IOException {
    // GIVEN & WHEN (if successful initialization, validation passes)
    PcapParser parser = new PcapParser("src/test/resources/synflood.pcap");

    // THEN
    List<PcapParser.Packet> packets = parser.getPackets();
    assertFalse(packets.isEmpty());
    assertTrue(packets.size() > 90000);
    assertTrue(parser.getAckPercentage() < 71);
    assertTrue(parser.getAckPercentage() > 69);
  }
}
