package jbittorrent.tracker.udp;

import akka.util.ByteString;

/**
 * Value classes with builders for the messages of the UDP tracker protocol described at
 * {@link http://xbtt.sourceforge.net/udp_tracker_protocol.html}.
 */
public abstract class UdpTrackerRequest {

  public abstract ByteString getByteString();
}
