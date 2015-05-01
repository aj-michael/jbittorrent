package jbittorrent.metainfo;

import static com.google.common.base.Verify.verify;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

final class MultipleFileInfoDictionary extends InfoDictionary {

  public final String name;
  public final List<FileDictionary> files;

  private MultipleFileInfoDictionary(
      int pieceLength,
      String pieces,
      Optional<Integer> privateBit,
      String name,
      List<FileDictionary> files) {
    super(pieceLength, pieces, privateBit);
    this.name = name;
    this.files = files;
  }

  @SuppressWarnings("unchecked")
  static MultipleFileInfoDictionary fromRaw(
      int pieceLength,
      String pieces,
      Optional<Integer> privateBit,
      Map<String, Object> map) {
    verify(map.containsKey("name"), "Name was missing from info dictionary.");
    verify(map.get("name") instanceof String, "Name has the wrong type.");
    String name = (String) map.get("name");

    verify(map.containsKey("files"), "Files was missing from info dictionary.");
    verify(map.get("files") instanceof List, "Files has the wrong type.");
    List<Map<String, Object>> rawFiles = (List<Map<String, Object>>) map.get("files");
    List<FileDictionary> files = rawFiles.stream()
        .map(rawFile -> FileDictionary.fromRaw(rawFile))
        .collect(Collectors.toList());

    return new MultipleFileInfoDictionary(pieceLength, pieces, privateBit, name, files);
  }
}
