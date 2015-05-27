package jbittorrent.tracker.udp;

import java.nio.ByteBuffer;

import com.google.common.base.Verify;

import akka.util.ByteString;

final class ConnectResponse implements UdpTrackerResponse {

  public final int connectTransactionId;
  public final long connectionId;

  private ConnectResponse(int connectTransactionId, long connectionId) {
    this.connectTransactionId = connectTransactionId;
    this.connectionId = connectionId;
  }

  static class ConnectResponseFactory implements UdpTrackerResponseFactory<ConnectResponse> {
    @Override
    public ConnectResponse fromByteString(ByteString bytes) {
      ByteBuffer buff = bytes.toByteBuffer();
      int action = buff.getInt();
      int connectTransactionId = buff.getInt();
      long connectionId = buff.getLong();
      Verify.verify(action == UdpHandshakeActions.CONNECT);
      return new ConnectResponse(connectTransactionId, connectionId);
    }
  }
}
