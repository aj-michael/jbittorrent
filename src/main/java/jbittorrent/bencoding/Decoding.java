package jbittorrent.bencoding;

import java.util.List;
import java.util.Map;

import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

public final class Decoding {

  static Intermediary<String> readString(String encoded) {
    Preconditions.checkArgument(Character.isDigit(encoded.charAt(0)));
    int splitIndex = encoded.indexOf(':');
    int length = Integer.parseInt(encoded.substring(0, splitIndex));
    String valueAndRemainder = encoded.substring(splitIndex + 1);
    return Intermediary.of(
        valueAndRemainder.substring(0, length),
        valueAndRemainder.substring(length));
  }

  static Intermediary<Integer> readInteger(String encoded) {
    Preconditions.checkArgument(encoded.charAt(0) == 'i');
    int splitIndex = encoded.indexOf('e');
    return Intermediary.of(
        Integer.parseInt(encoded.substring(1, splitIndex)),
        encoded.substring(splitIndex + 1));
  }

  static Intermediary<List<Object>> readList(String encoded) {
    Preconditions.checkArgument(encoded.charAt(0) == 'l');
    encoded = encoded.substring(1);
    List<Object> list = Lists.newLinkedList();
    while (encoded.charAt(0) != 'e') {
      Intermediary<? extends Object> intermediary = readObject(encoded);
      list.add(intermediary.decodedValue);
      encoded = intermediary.encodedRemainder;
    }
    return Intermediary.of(list, encoded.substring(1));
  }

  public static Intermediary<Map<String, Object>> readDictionary(String encoded) {
    Preconditions.checkArgument(encoded.charAt(0) == 'd');
    encoded = encoded.substring(1);
    Map<String, Object> dictionary = Maps.newHashMap();
    while (encoded.charAt(0) != 'e') {
      Intermediary<String> intermediaryKey = readString(encoded);
      Intermediary<? extends Object> intermediaryValue =
          readObject(intermediaryKey.encodedRemainder);
      dictionary.put(intermediaryKey.decodedValue, intermediaryValue.decodedValue);
      encoded = intermediaryValue.encodedRemainder;
    }
    return Intermediary.of(dictionary, encoded.substring(1));
  }

  public static Intermediary<? extends Object> readObject(String encoded) {
    if (Character.isDigit(encoded.charAt(0))) {
      return readString(encoded);
    } else if (encoded.charAt(0) == 'i') {
      return readInteger(encoded);
    } else if (encoded.charAt(0) == 'l') {
      return readList(encoded);
    } else if (encoded.charAt(0) == 'd') {
      return readDictionary(encoded);
    } else {
      throw new InvalidEncodingException(encoded);
    }
  }

  static class InvalidEncodingException extends RuntimeException {
    public InvalidEncodingException(String invalidString) {
      super(invalidString);
    }
  };
}
