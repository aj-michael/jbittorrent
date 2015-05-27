package jbittorrent.tracker.udp;

import java.nio.ByteBuffer;

import com.google.common.base.Charsets;
import com.google.common.base.Verify;

import akka.util.ByteString;

class ErrorResponse implements UdpTrackerResponse {

  public final int transactionId;
  public final String message;

  private ErrorResponse(int transactionId, String message) {
    this.transactionId = transactionId;
    this.message = message;
  }

  static class ErrorResponseFactory implements UdpTrackerResponseFactory<ErrorResponse> {

    @Override
    public ErrorResponse fromByteString(ByteString bytes) {
      ByteBuffer buff = bytes.toByteBuffer();
      int action = buff.getInt();
      int transactionId = buff.getInt();
      byte[] messageBytes = new byte[buff.remaining()];
      buff.get(messageBytes);
      String message = new String(messageBytes, Charsets.UTF_8);
      Verify.verify(action == UdpHandshakeActions.ERROR);
      return new ErrorResponse(transactionId, message);
    }
  }
}
