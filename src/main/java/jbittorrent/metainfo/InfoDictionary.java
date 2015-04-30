package jbittorrent.metainfo;

import static com.google.common.base.Verify.verify;

import java.util.List;
import java.util.Map;
import java.util.Optional;

abstract class InfoDictionary {
  final int pieceLength;
  final String pieces;
  final Optional<Integer> privateBit;

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


final class SingleFileInfoDictionary extends InfoDictionary {

  final String name;
  final int length;
  Optional<String> md5sum;

  private SingleFileInfoDictionary(
      int pieceLength,
      String pieces,
      Optional<Integer> privateBit,
      String name,
      int length) {
    super(pieceLength, pieces, privateBit);
    this.name = name;
    this.length = length;
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

    return new SingleFileInfoDictionary(pieceLength, pieces, privateBit, name, length);
  }
}


final class MultipleFileInfoDictionary extends InfoDictionary {

  String name;
  List<FileDictionary> files;

  private MultipleFileInfoDictionary(
      int pieceLength,
      String pieces,
      Optional<Integer> privateBit,
      String name) {
    super(pieceLength, pieces, privateBit);
    this.name = name;
  }

  static MultipleFileInfoDictionary fromRaw(
      int pieceLength,
      String pieces,
      Optional<Integer> privateBit,
      Map<String, Object> map) {
    verify(map.containsKey("name"), "Name was missing from info dictionary.");
    verify(map.get("name") instanceof String, "Name has the wrong type.");
    String name = (String) map.get("name");

    return new MultipleFileInfoDictionary(pieceLength, pieces, privateBit, name);
  }
}
