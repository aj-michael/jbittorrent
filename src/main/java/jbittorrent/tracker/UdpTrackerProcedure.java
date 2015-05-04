package jbittorrent.tracker;

import jbittorrent.tracker.UdpTracker.TrackerState;
import akka.io.UdpConnected;
import akka.japi.Procedure;
import akka.util.ByteString;

class UdpTrackerProcedure implements Procedure<Object> {
  
  final UdpTracker tracker;
  
  UdpTrackerProcedure(UdpTracker tracker) {
    this.tracker = tracker;
  }

  @Override
  public void apply(Object msg) throws Exception {
    if (msg instanceof UdpConnected.Received) {
      ByteString data = ((UdpConnected.Received) msg).data();
      if (tracker.state == TrackerState.CONNECTING) {
        tracker.receiveConnect(data.asByteBuffer());
      } else if (tracker.state == TrackerState.ANNOUNCING) {
        
      } else {
        System.out.println("Undefined transition function for tracker state: " + tracker.state);
      }
      System.out.println(data);
    } else {
      System.out.println("received other message");
      System.out.println(msg);
    }
  }

}
