package jbittorrent.tracker.udp;

import java.nio.ByteBuffer;
import java.util.LinkedList;
import java.util.List;

import com.google.common.base.Verify;

import akka.util.ByteString;

class ScrapeResponse implements UdpTrackerResponse {

  public final int transactionId;
  public final List<ScrapeData> scrapeDatum;

  private ScrapeResponse(int transactionId, List<ScrapeData> scrapeDatum) {
    this.transactionId = transactionId;
    this.scrapeDatum = scrapeDatum;
  }

  static class ScrapeResponseFactory implements UdpTrackerResponseFactory<ScrapeResponse> {
    @Override
    public ScrapeResponse fromByteString(ByteString bytes) {
      ByteBuffer buff = bytes.toByteBuffer();
      int action = buff.getInt();
      int transactionId = buff.getInt();

      List<ScrapeData> scrapeDatum = new LinkedList<>();
      while (buff.hasRemaining()) {
        scrapeDatum.add(new ScrapeData(buff.getInt(), buff.getInt(), buff.getInt()));
      }

      Verify.verify(action == UdpHandshakeActions.SCRAPE);
      return new ScrapeResponse(transactionId, scrapeDatum);
    }
  }

  static class ScrapeData {
    public final int completed;
    public final int leechers;
    public final int seeders;

    private ScrapeData(int completed, int leechers, int seeders) {
      this.completed = completed;
      this.leechers = leechers;
      this.seeders = seeders;
    }
  }
}
