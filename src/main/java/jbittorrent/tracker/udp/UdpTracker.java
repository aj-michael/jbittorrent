package jbittorrent.tracker.udp;

import java.io.File;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.ByteBuffer;

import java.util.Random;

import jbittorrent.metainfo.Metainfo;
import jbittorrent.tracker.Tracker;
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

  static final int DEFAULT_IP_ADDRESS = 0;
  static final int DEFAULT_NUM_WANT = -1;
  static final int DEFAULT_KEY = 0;
  
  final InetSocketAddress remote;
  volatile TrackerState state;
  final int connectTransactionId;
  final int announceTransactionId;
  final int key;
  final byte[] infoHash;
  final byte[] peerId;
  final short port;
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
  public UdpTracker(InetSocketAddress remote, byte[] peerId, byte[] infoHash, short port, Optional<Integer> key) {
    System.out.println("Was created");
    this.infoHash = infoHash;
    this.key = key.or(DEFAULT_KEY);
    this.peerId = peerId;
    this.port = port;
    this.remote = remote;

    Random r = new Random();
    this.connectTransactionId = r.nextInt();
    this.announceTransactionId = r.nextInt();
    this.state = TrackerState.INITIAL;

    final ActorRef mgr = UdpConnected.get(getContext().system()).getManager();
    mgr.tell(UdpConnectedMessage.connect(getSelf(),  remote), getSelf());
  }

  @Override
  public void onReceive(Object msg) {
    conn = Optional.of(getSender());
    if (msg instanceof UdpConnected.Connected) {
      getContext().become(new UdpTrackerProcedure(this));
      connectRequest();
    }
  }

  void connectRequest() {
    Preconditions.checkArgument(conn.isPresent());
    int connectInputSize = 16;
    long connectionId = 0x41727101980L;
    ByteBuffer buffer = ByteBuffer.allocate(connectInputSize)
        .putLong(connectionId)
        .putInt(UdpHandshakeActions.CONNECT)
        .putInt(this.connectTransactionId);
    buffer.flip();
    ByteString byteString = ByteString.fromByteBuffer(buffer);
    Command cmd = UdpConnectedMessage.send(byteString);
    conn.get().tell(cmd, getSelf());
    this.state = TrackerState.CONNECTING;
  }
  
  void connectResponse(ByteBuffer data) {
    int action = data.getInt();
    int transactionId = data.getInt();
    long connectionId = data.getLong();
    Verify.verify(action == 0);
    Verify.verify(transactionId == this.connectTransactionId);
    this.connectionId = Optional.of(connectionId);
    this.state = TrackerState.CONNECTED;
  }
  
  void announceRequest() {
    Preconditions.checkArgument(conn.isPresent());
  }
  
  ByteString announceInput() {
    Preconditions.checkArgument(connectionId.isPresent());
    int announceInputSize = 98;
    ByteBuffer buffer = ByteBuffer.allocate(announceInputSize)
        .putLong(connectionId.get())
        .putInt(UdpHandshakeActions.ANNOUNCE)
        .putInt(announceTransactionId)
        .put(infoHash)
        .put(peerId)
        .putLong(uploaded)
        .putInt(UdpHandshakeEvents.NONE)
        .putInt(DEFAULT_IP_ADDRESS)
        .putInt(key)
        .putInt(DEFAULT_NUM_WANT)
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

    InetSocketAddress socket = new InetSocketAddress(host, port);
    system.actorOf(Props.create(UdpTracker.class, socket));
  }
}
