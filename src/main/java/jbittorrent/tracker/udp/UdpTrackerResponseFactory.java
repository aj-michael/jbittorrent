package jbittorrent.tracker.udp;

import akka.util.ByteString;

public interface UdpTrackerResponseFactory<E extends UdpTrackerResponse> {

  E fromByteString(ByteString bytes);

}
