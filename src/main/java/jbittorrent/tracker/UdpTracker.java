package jbittorrent.tracker;

import java.io.File;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.Random;

import jbittorrent.metainfo.Metainfo;
import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Props;
import akka.actor.UntypedActor;
import akka.io.UdpConnected;
import akka.io.UdpConnected.Command;
import akka.io.UdpConnectedMessage;
import akka.util.ByteString;

import com.google.common.base.Optional;
import com.google.common.base.Preconditions;
import com.google.common.base.Verify;
import com.google.inject.Inject;

public class UdpTracker extends UntypedActor implements Tracker {

  final InetSocketAddress remote;
  volatile TrackerState state;
  final int connectTransactionId;
  final int announceTransactionId;
  final byte[] infoHash;
  final byte[] peerId;
  long downloaded = 0;
  long left = 0;
  long uploaded = 0;
  Optional<ActorRef> conn = Optional.<ActorRef>absent();
  Optional<Long> connectionId = Optional.<Long>absent();
  
  enum TrackerState {
    INITIAL,
    CONNECTING,
    CONNECTED,
    ANNOUNCING,
    ANNOUNCED
  }
  
  
  @Inject
  public UdpTracker(InetSocketAddress remote) {
    System.out.println("Was created");
    this.remote = remote;
    this.state = TrackerState.INITIAL;
    Random r = new Random();
    this.connectTransactionId = r.nextInt();
    this.announceTransactionId = r.nextInt();

    final ActorRef mgr = UdpConnected.get(getContext().system()).getManager();
    mgr.tell(UdpConnectedMessage.connect(getSelf(),  remote), getSelf());
  }

  @Override
  public void onReceive(Object msg) {

    System.out.println("Received "+msg);
    conn = Optional.of(getSender());
    if (msg instanceof UdpConnected.Connected) {
      getContext().become(new UdpTrackerProcedure(this));
      sendConnect();
    }
  }

  void sendConnect() {
    Preconditions.checkArgument(conn.isPresent());
    int connectInputSize = 16;
    long connectionId = 0x41727101980L;
    int action = 0;
    ByteBuffer buffer = ByteBuffer.allocate(connectInputSize)
        .putLong(connectionId)
        .putInt(action)
        .putInt(this.connectTransactionId);
    buffer.flip();
    ByteString byteString = ByteString.fromByteBuffer(buffer);
    Command cmd = UdpConnectedMessage.send(byteString);
    conn.get().tell(cmd, getSelf());
    this.state = TrackerState.CONNECTING;
  }
  
  void receiveConnect(ByteBuffer data) {
    int action = data.getInt();
    int transactionId = data.getInt();
    long connectionId = data.getLong();
    Verify.verify(action == 0);
    Verify.verify(connectionId == this.connectTransactionId);
    this.connectionId = Optional.of(connectionId);
    this.state = TrackerState.CONNECTED;

    System.out.println("======== Received =======");
    System.out.println("action = "+action);
    System.out.println("transactionId = "+transactionId);
    System.out.println("connectionId = "+connectionId);
  }
  
  void sendAnnounce() {
    Preconditions.checkArgument(conn.isPresent());
  }
  
  ByteString announceInput() {
    Preconditions.checkArgument(connectionId.isPresent());
    int announceInputSize = 98;
    int action = 1;
    int ip = 0;
    int numWant = -1;
    ByteBuffer buffer = ByteBuffer.allocate(announceInputSize)
        .putLong(connectionId.get())
        .putInt(action)
        .putInt(announceTransactionId)
        .put(infoHash)
        .put(peerId)
        .putLong(uploaded)
        .putInt(event)
        .putInt(ip)
        .putInt(key)
        .putInt(numWant)
        .putShort(port);
    buffer.flip();
    return ByteString.fromByteBuffer(buffer);
  }
  
  public static void main(String[] args) throws IOException, URISyntaxException {
    ActorSystem system = ActorSystem.create();
    String filename = "boxing.torrent";
    File inputFile = new File(filename);
    Metainfo metainfo = Metainfo.fromFile(inputFile);
    System.out.println(new URI(metainfo.announce));
    URI u = new URI(metainfo.announce);
    String host = u.getHost();
    int port = u.getPort();
    System.out.println("hostname = "+host);
    System.out.println("port = "+port);
    
    //short port = 1337;
    InetSocketAddress socket = new InetSocketAddress(host, port);
    system.actorOf(Props.create(UdpTracker.class, socket));
  }
  
}
