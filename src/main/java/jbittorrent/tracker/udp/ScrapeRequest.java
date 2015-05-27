package jbittorrent.tracker.udp;

import java.nio.ByteBuffer;
import java.util.LinkedList;
import java.util.List;

import com.google.common.base.Preconditions;

import akka.util.ByteString;

class ScrapeRequest extends UdpTrackerRequest {

  private static final int MAX_TORRENTS_PER_SCRAPE = 74;

  private final long connectionId;
  private final int transactionId;
  private final List<byte[]> infoHashes;

  private ScrapeRequest(Builder b) {
    this.connectionId = b.connectionId;
    this.transactionId = b.transactionId;
    this.infoHashes = b.infoHashes;
  }

  @Override
  public ByteString getByteString() {
    ByteBuffer buffer = ByteBuffer.allocate(16 + 20 * infoHashes.size())
        .putLong(this.connectionId)
        .putInt(this.transactionId);
    for (byte[] infoHash : infoHashes) {
      buffer.put(infoHash);
    }
    buffer.flip();
    return ByteString.fromByteBuffer(buffer);
  }

  public static Builder newBuilder() {
    return new Builder();
  }

  public static class Builder {
    private long connectionId;
    private int transactionId;
    private List<byte[]> infoHashes;

    public Builder() {
      this.infoHashes = new LinkedList<>();
    }

    public ScrapeRequest build() {
      Preconditions.checkNotNull(this.infoHashes);
      return new ScrapeRequest(this);
    }

    public Builder setConnectionId(long connectionId) {
      this.connectionId = connectionId;
      return this;
    }

    public Builder setTransactionId(int transactionId) {
      this.transactionId = transactionId;
      return this;
    }

    public Builder setInfoHashes(List<byte[]> infoHashes) {
      Preconditions.checkArgument(infoHashes.size() < MAX_TORRENTS_PER_SCRAPE);
      this.infoHashes = infoHashes;
      return this;
    }

    public Builder addInfoHash(byte[] infoHash) {
      Preconditions.checkArgument(infoHashes.size() + 1 < MAX_TORRENTS_PER_SCRAPE);
      this.infoHashes.add(infoHash);
      return this;
    }
  }
}
