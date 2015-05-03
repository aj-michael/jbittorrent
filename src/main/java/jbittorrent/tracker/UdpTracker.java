package jbittorrent.tracker;

import java.net.InetSocketAddress;

import com.google.inject.Inject;

import akka.actor.ActorRef;
import akka.actor.UntypedActor;
import akka.io.UdpConnected;
import akka.io.UdpConnectedMessage;
import akka.japi.Procedure;

public class UdpTracker extends UntypedActor implements Tracker {

  final InetSocketAddress remote;

  @Inject
  public UdpTracker(InetSocketAddress remote) {
    this.remote = remote;

    final ActorRef mgr = UdpConnected.get(getContext().system()).getManager();
    mgr.tell(UdpConnectedMessage.connect(getSelf(),  remote), getSelf());
  }
  
  @Override
  public void onReceive(Object msg) {
    if (msg instanceof UdpConnected.Connected) {
      getContext().become(ready(getSender()));
    }
  }

  private Procedure<Object> ready(final ActorRef connection) {
    return new Procedure<Object>() {
      @Override
      public void apply(Object arg0) throws Exception {
        // TODO Auto-generated method stub
        
      }
    };
  }
  
}
