package jbittorrent.metainfo;

import static com.google.common.base.Verify.verify;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public final class FileDictionary {
  public final int length;
  public final Optional<String> md5sum;
  public final List<String> path;

  private FileDictionary(int length, Optional<String> md5sum, List<String> path) {
    this.length = length;
    this.md5sum = md5sum;
    this.path = path;
  }

  @SuppressWarnings("unchecked")
  public static FileDictionary fromRaw(Map<String,Object> rawFile) {
    verify(rawFile.containsKey("length"), "Length was missing from file dictionary.");
    verify(rawFile.get("length") instanceof Integer, "Length has the wrong type.");
    int length = (Integer) rawFile.get("length");

    Optional<String> md5sum = Optional.empty();
    if (rawFile.containsKey("md5sum")) {
      verify(rawFile.get("md5sum") instanceof String, "md5sum has the wrong type.");
      md5sum = Optional.of((String) rawFile.get("md5sum"));
    }

    verify(rawFile.containsKey("path"), "Path was missing from file dictionary.");
    verify(rawFile.get("path") instanceof List, "Path has the wrong type.");
    List<String> path = (List<String>) rawFile.get("path");

    return new FileDictionary(length, md5sum, path);
  }
}
