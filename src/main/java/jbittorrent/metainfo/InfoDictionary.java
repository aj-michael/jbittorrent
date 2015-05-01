package jbittorrent.metainfo;

import static com.google.common.base.Verify.verify;

import java.util.Map;
import java.util.Optional;

public abstract class InfoDictionary {
  public final int pieceLength;
  public final String pieces;
  public final Optional<Integer> privateBit;

  public static InfoDictionary fromRaw(Map<String, Object> map) {
    verify(map.containsKey("piece length"), "Piece length missing from info dictionary.");
    verify(map.get("piece length") instanceof Integer, "Piece length has the wrong type.");
    int pieceLength = (Integer) map.get("piece length");

    verify(map.containsKey("pieces"), "Pieces was missing from info dictionary.");
    verify(map.get("pieces") instanceof String, "Pieces has the wrong type.");
    String pieces = (String) map.get("pieces");

    Optional<Integer> privateBit = Optional.empty();
    if (map.containsKey("private bit")) {
      verify(map.get("private bit") instanceof Integer, "Private bit has the wrong type.");
      privateBit = Optional.of((Integer) map.get("private bit"));
    }

    if (map.containsKey("files")) {
      return MultipleFileInfoDictionary.fromRaw(pieceLength, pieces, privateBit, map);
    } else {
      return SingleFileInfoDictionary.fromRaw(pieceLength, pieces, privateBit, map);
    }
  }

  InfoDictionary(int pieceLength, String pieces, Optional<Integer> privateBit) {
    this.pieceLength = pieceLength;
    this.pieces = pieces;
    this.privateBit = privateBit;
  }
}