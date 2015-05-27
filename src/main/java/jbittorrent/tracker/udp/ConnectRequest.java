package jbittorrent.tracker.udp;

import java.nio.ByteBuffer;

import akka.util.ByteString;

class ConnectRequest extends UdpTrackerRequest {

  private static final int CONNECT_INPUT_SIZE = 16;
  private static final long INITIAL_CONNECTION_ID = 0x41727101980L;

  private final int connectTransactionId;

  private ConnectRequest(Builder b) {
    this.connectTransactionId = b.connectTransactionId;
  }

  @Override
  public ByteString getByteString() {
    ByteBuffer buffer = ByteBuffer.allocate(CONNECT_INPUT_SIZE)
        .putLong(INITIAL_CONNECTION_ID)
        .putInt(UdpHandshakeActions.CONNECT)
        .putInt(this.connectTransactionId);
    buffer.flip();
    return ByteString.fromByteBuffer(buffer);
  }

  public static Builder newBuilder() {
    return new Builder();
  }

  public static class Builder {
    private int connectTransactionId;

    public ConnectRequest build() {
      return new ConnectRequest(this);
    }

    public Builder setConnectTransactionId(int connectTransactionId) {
      this.connectTransactionId = connectTransactionId;
      return this;
    }
  }
}
