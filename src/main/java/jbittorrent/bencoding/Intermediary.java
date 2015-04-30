package jbittorrent.bencoding;

import com.google.common.base.Preconditions;

public final class Intermediary<T> {
  public final T decodedValue;
  public final String encodedRemainder;

  private Intermediary(T decodedValue, String encodedRemainder) {
    this.decodedValue = decodedValue;
    this.encodedRemainder = encodedRemainder;
  }

  public static <T> Intermediary<T> of(T decodedValue, String encodedRemainder) {
    Preconditions.checkNotNull(decodedValue);
    Preconditions.checkNotNull(encodedRemainder);
    return new Intermediary<T>(decodedValue, encodedRemainder);
  }
}
