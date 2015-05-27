package jbittorrent.tracker.udp;

import java.nio.ByteBuffer;

import akka.util.ByteString;

class AnnounceRequest extends UdpTrackerRequest {

  private static final int ANNOUNCE_INPUT_SIZE = 98;
  // Setting ip to 0 uses source address.
  private static final int IP_ADDRESS = 0;
  // Unsigned number so -1 = MAX_INT.
  private static final int NUM_CLIENTS_WANTED = -1;

  private final long connectionId;
  private final int announceTransactionId;
  private final byte[] infoHash;
  private final byte[] peerId;
  private final long uploaded;
  private final int key;
  private final short port;

  private AnnounceRequest(Builder b) {
    this.connectionId = b.connectionId;
    this.announceTransactionId = b.announceTransactionId;
    this.infoHash = b.infoHash;
    this.peerId = b.peerId;
    this.uploaded = b.uploaded;
    this.key = b.key;
    this.port = b.port;
  }

  @Override
  public ByteString getByteString() {
    ByteBuffer buffer = ByteBuffer.allocate(ANNOUNCE_INPUT_SIZE)
        .putLong(this.connectionId)
        .putInt(UdpHandshakeActions.ANNOUNCE)
        .putInt(this.announceTransactionId)
        .put(this.infoHash)
        .put(this.peerId)
        .putLong(this.uploaded)
        .putInt(IP_ADDRESS)
        .putInt(this.key)
        .putInt(NUM_CLIENTS_WANTED)
        .putShort(this.port);
    buffer.flip();
    return ByteString.fromByteBuffer(buffer);
  }

  public static Builder newBuilder() {
    return new Builder();
  }

  public static class Builder {
    private long connectionId;
    private int announceTransactionId;
    private byte[] infoHash;
    private byte[] peerId;
    private long uploaded;
    private int key;
    private short port;

    public AnnounceRequest build() {
      return new AnnounceRequest(this);
    }

    public Builder setConnectionId(long connectionId) {
      this.connectionId = connectionId;
      return this;
    }

    public Builder setAnnounceTransactionId(int announceTransactionId) {
      this.announceTransactionId = announceTransactionId;
      return this;
    }

    public Builder setInfoHash(byte[] infoHash) {
      this.infoHash = infoHash;
      return this;
    }

    public Builder setPeerId(byte[] peerId) {
      this.peerId = peerId;
      return this;
    }

    public Builder setUploaded(long uploaded) {
      this.uploaded = uploaded;
      return this;
    }

    public Builder setKey(int key) {
      this.key = key;
      return this;
    }

    public Builder setPort(short port) {
      this.port = port;
      return this;
    }
  }
}
