package jbittorrent.metainfo;

import static com.google.common.base.Verify.verify;

import java.util.Map;
import java.util.Optional;

final class SingleFileInfoDictionary extends InfoDictionary {

  public final String name;
  public final int length;
  public final Optional<String> md5sum;

  private SingleFileInfoDictionary(
      int pieceLength,
      String pieces,
      Optional<Integer> privateBit,
      String name,
      int length,
      Optional<String> md5sum) {
    super(pieceLength, pieces, privateBit);
    this.name = name;
    this.length = length;
    this.md5sum = md5sum;
  }

  static SingleFileInfoDictionary fromRaw(
      int pieceLength,
      String pieces,
      Optional<Integer> privateBit,
      Map<String, Object> map) {
    verify(map.containsKey("name"), "Name was missing from info dictionary.");
    verify(map.get("name") instanceof String, "Name has the wrong type.");
    String name = (String) map.get("name");

    verify(map.containsKey("length"), "Length was missing from info dictionary.");
    verify(map.get("length") instanceof Integer, "Length has the wrong type.");
    int length = (Integer) map.get("name");

    Optional<String> md5sum = Optional.empty();
    if (map.containsKey("md5sum")) {
      verify(map.get("md5sum") instanceof String, "md5sum has the wrong type.");
      md5sum = Optional.of((String) map.get("md5sum"));
    }

    return new SingleFileInfoDictionary(pieceLength, pieces, privateBit, name, length, md5sum);
  }
}