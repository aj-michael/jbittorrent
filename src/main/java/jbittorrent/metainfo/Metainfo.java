package jbittorrent.metainfo;

import java.io.File;
import java.util.Date;
import java.util.List;
import java.util.Optional;

public final class Metainfo {

  InfoDictionary info;
  String announce;
  Optional<List<String>> announceList;
  Optional<Date> creationDate;
  Optional<String> comment;
  Optional<String> createdBy;
  Optional<String> encoding;

  public static Metainfo fromFile(File f) {
    return new Metainfo();
  }

}
