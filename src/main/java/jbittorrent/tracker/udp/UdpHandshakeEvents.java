package jbittorrent.tracker.udp;

/**
 * Values from the unofficial documentation.
 * See <a href="http://xbtt.sourceforge.net/udp_tracker_protocol.html">documentation.</a>
 */
final class UdpHandshakeEvents {

  private UdpHandshakeEvents() {}

  static final int NONE = 0;
  static final int COMPLETED = 1;
  static final int STARTED = 2;
  static final int STOPPED = 3;
}
