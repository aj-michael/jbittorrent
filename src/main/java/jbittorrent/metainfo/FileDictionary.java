package jbittorrent.metainfo;

import java.util.List;
import java.util.Optional;

final class FileDictionary {
  int length;
  Optional<String> md5sum;
  List<String> path;
}
