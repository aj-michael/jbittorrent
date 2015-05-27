package jbittorrent.tracker.udp;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.util.List;

import jbittorrent.tracker.udp.AnnounceResponse;
import jbittorrent.tracker.udp.AnnounceResponse.AnnounceResponseFactory;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import com.google.common.collect.ImmutableList;

import akka.util.ByteString;

@RunWith(JUnit4.class)
public class AnnounceResponseTest {

  @Test
  public void testNoPeers() {
    int transactionId = 525324;
    int interval = 3000;
    int leechers = 532;
    int seeders = 2134;
    List<Integer> ips = ImmutableList.of();
    List<Short> ports = ImmutableList.of();
    ByteString bytes = getAnnounceResponse(transactionId, interval, leechers, seeders, ips, ports);
    AnnounceResponse response = new AnnounceResponseFactory().fromByteString(bytes);
    assertEquals(transactionId, response.transactionId);
    assertEquals(interval, response.interval);
    assertEquals(leechers, response.leechers);
    assertEquals(seeders, response.seeders);
    assertTrue(response.peers.isEmpty());
  }

  @Test
  public void testSomePeers() {
    int transactionId = 525324;
    int interval = 3000;
    int leechers = 532;
    int seeders = 2134;
    List<Integer> ips = ImmutableList.of(1412, 34142, 4, 99999);
    List<Short> ports = ImmutableList.of((short) 23, (short) 886, (short) 8080, (short) 5);
    ByteString bytes = getAnnounceResponse(transactionId, interval, leechers, seeders, ips, ports);
    AnnounceResponse response = new AnnounceResponseFactory().fromByteString(bytes);
    assertEquals(transactionId, response.transactionId);
    assertEquals(interval, response.interval);
    assertEquals(leechers, response.leechers);
    assertEquals(seeders, response.seeders);
    assertEquals(ips.size(), response.peers.size());
    assertEquals(ports.size(), response.peers.size());
    for (int i = 0; i < response.peers.size(); i++) {
      InetSocketAddress socket = response.peers.get(i);
      assertEquals((int) ports.get(i), socket.getPort());
      assertEquals((int) ips.get(i), ByteBuffer.wrap(socket.getAddress().getAddress()).getInt());
    }
  }

  private ByteString getAnnounceResponse(int transactionId, int interval, int leechers,
      int seeders, List<Integer> ips, List<Short> ports) {
    assertEquals(ips.size(), ports.size());
    ByteBuffer buff = ByteBuffer.allocate(20 + 6 * ips.size())
        .putInt(UdpHandshakeActions.ANNOUNCE)
        .putInt(transactionId)
        .putInt(interval)
        .putInt(leechers)
        .putInt(seeders);
    for (int i = 0; i < ips.size() && i < ports.size(); i++) {
      buff.putInt(ips.get(i));
      buff.putShort(ports.get(i));
    }
    buff.flip();
    return ByteString.fromByteBuffer(buff);
  }
}
