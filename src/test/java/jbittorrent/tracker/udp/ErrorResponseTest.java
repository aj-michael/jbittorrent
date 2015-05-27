package jbittorrent.tracker.udp;

import static org.junit.Assert.assertEquals;

import java.nio.ByteBuffer;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import jbittorrent.tracker.udp.ErrorResponse.ErrorResponseFactory;
import akka.util.ByteString;

@RunWith(JUnit4.class)
public class ErrorResponseTest {

  @Test
  public void testNormalError() {
    int transactionId = 1422;
    String message = "Hello, world!";
    ByteString bytes = getErrorResponse(transactionId, message);
    ErrorResponse response = new ErrorResponseFactory().fromByteString(bytes);
    assertEquals(transactionId, response.transactionId);
    assertEquals(message, response.message);
  }

  private ByteString getErrorResponse(int transactionId, String message) {
    ByteBuffer buff = ByteBuffer.allocate(8 + message.getBytes().length)
        .putInt(UdpHandshakeActions.ERROR)
        .putInt(transactionId)
        .put(message.getBytes());
    buff.flip();
    return ByteString.fromByteBuffer(buff);
  }
}
