package jbittorrent.tracker.udp;

/**
 * Values from the unofficial documentation.
 * See <a href="http://xbtt.sourceforge.net/udp_tracker_protocol.html">documentation.</a>
 */
final class UdpHandshakeActions {

  private UdpHandshakeActions() {}

  static final int CONNECT = 0;
  static final int ANNOUNCE = 1;
  static final int SCRAPE = 2;
  static final int ERROR = 3;
}
