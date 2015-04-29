package jbittorrent.metainfo;

import java.util.List;
import java.util.Optional;

abstract class InfoDictionary {
  int pieceLength;
  String pieces;
  Optional<Integer> privateBit;
}


final class SingleFileInfoDictionary extends InfoDictionary {
  String name;
  int length;
  Optional<String> md5sum;
}


final class MultipleFileInfoDictionary extends InfoDictionary {
  String name;
  List<FileDictionary> files;
}
