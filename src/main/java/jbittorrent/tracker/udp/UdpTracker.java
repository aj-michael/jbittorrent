package jbittorrent.tracker.udp;

import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.util.Random;

import jbittorrent.tracker.Tracker;
import akka.actor.ActorRef;
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
  int announceTransactionId;
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
    ByteString byteString = ConnectRequest.newBuilder()
        .setConnectTransactionId(this.connectTransactionId)
        .build()
        .getByteString();
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

  AnnounceRequest newAnnounceInput() {
    Preconditions.checkArgument(connectionId.isPresent());
    return AnnounceRequest.newBuilder()
        .setConnectionId(connectionId.get())
        .setAnnounceTransactionId(announceTransactionId)
        .setInfoHash(infoHash)
        .setKey(key)
        .setPeerId(peerId)
        .setPort(port)
        .setUploaded(uploaded)
        .build();
  }
}
