package jbittorrent.bencoding;

import com.google.common.base.Preconditions;

public final class Decoding {

  public static Intermediary<String> readString(String encoded) {
    Preconditions.checkArgument(Character.isDigit(encoded.charAt(0)));
    int splitIndex = encoded.indexOf(':');
    int length = Integer.parseInt(encoded.substring(0, splitIndex));
    String valueAndRemainder = encoded.substring(splitIndex + 1);
    return Intermediary.of(
        valueAndRemainder.substring(0, length),
        valueAndRemainder.substring(length));
  }

  public static Intermediary<Integer> readInteger(String encoded) {
    Preconditions.checkArgument(encoded.charAt(0) == 'i');
    int splitIndex = encoded.indexOf('e');
    return Intermediary.of(
        Integer.parseInt(encoded.substring(1, splitIndex)),
        encoded.substring(splitIndex + 1));
  }

  public static class Intermediary<T> {
    final T decodedValue;
    final String encodedRemainder;

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
}
