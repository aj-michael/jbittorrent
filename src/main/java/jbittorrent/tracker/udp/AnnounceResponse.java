package jbittorrent.tracker.udp;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;
import java.util.LinkedList;
import java.util.List;

import com.google.common.base.Verify;

import akka.util.ByteString;

final class AnnounceResponse implements UdpTrackerResponse {

  public final int interval;
  public final List<InetSocketAddress> peers;
  public final int leechers;
  public final int seeders;
  public final int transactionId;

  private AnnounceResponse(
      int interval,
      int leechers,
      List<InetSocketAddress> peers,
      int seeders,
      int transactionId) {
    this.interval = interval;
    this.leechers = leechers;
    this.peers = peers;
    this.seeders = seeders;
    this.transactionId = transactionId;
  }

  static class AnnounceResponseFactory implements UdpTrackerResponseFactory<AnnounceResponse> {
    @Override
    public AnnounceResponse fromByteString(ByteString bytes) {
      ByteBuffer buff = bytes.toByteBuffer();
      int action = buff.getInt();
      int transactionId = buff.getInt();
      int interval = buff.getInt();
      int leechers = buff.getInt();
      int seeders = buff.getInt();

      List<InetSocketAddress> peers = new LinkedList<>();
      while (buff.hasRemaining()) {
        byte[] ip = ByteBuffer.allocate(4).putInt(buff.getInt()).array();
        try {
          InetAddress addr = InetAddress.getByAddress(ip);
          short port = buff.getShort();
          peers.add(new InetSocketAddress(addr, port));
        } catch (UnknownHostException e) {
          throw new IllegalArgumentException(e);
        }
      }

      Verify.verify(action == UdpHandshakeActions.ANNOUNCE);
      return new AnnounceResponse(interval, leechers, peers, seeders, transactionId);
    }
  }
}
