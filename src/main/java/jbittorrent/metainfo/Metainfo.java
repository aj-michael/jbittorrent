package jbittorrent.metainfo;

import static com.google.common.base.Verify.verify;

import java.io.File;
import java.io.IOException;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import jbittorrent.bencoding.Decoding;
import jbittorrent.bencoding.Intermediary;

import com.google.common.base.Charsets;
import com.google.common.io.Files;

public final class Metainfo {

  public final InfoDictionary info;
  public final String announce;
  final Optional<List<String>> announceList;
  final Optional<String> comment;
  final Optional<String> createdBy;
  final Optional<Instant> creationDate;
  final Optional<String> encoding;

  private Metainfo(
      InfoDictionary info,
      String announce,
      Optional<List<String>> announceList,
      Optional<String> comment,
      Optional<String> createdBy,
      Optional<Instant> creationDate,
      Optional<String> encoding) {
    this.info = info;
    this.announce = announce;
    this.announceList = announceList;
    this.comment = comment;
    this.creationDate = creationDate;
    this.createdBy = createdBy;
    this.encoding = encoding;
  }

  @SuppressWarnings("unchecked")
  public static Metainfo fromFile(File file) throws IOException {
    String fullEncodedMetainfo = Files.toString(file, Charsets.ISO_8859_1);
    Intermediary<Map<String, Object>> intermediary = Decoding.readDictionary(fullEncodedMetainfo);
    verify(intermediary.encodedRemainder.isEmpty(), "Torrent file was not fully parsed.");
    Map<String, Object> metainfoRaw = intermediary.decodedValue;

    verify(metainfoRaw.containsKey("info"), "Info dictionary was missing from torrent file.");
    verify(metainfoRaw.get("info") instanceof Map, "Info dictionary has the wrong type.");
    InfoDictionary info = InfoDictionary.fromRaw((Map<String, Object>) metainfoRaw.get("info"));

    verify(metainfoRaw.containsKey("announce"), "Announce URL was missing from torrent file.");
    verify(metainfoRaw.get("announce") instanceof String, "Announce URL has the wrong type.");
    String announce = (String) metainfoRaw.get("announce");

    Optional<List<String>> announceList = Optional.empty();
    if (metainfoRaw.containsKey("announce-list")) {
      Object rawAnnounceList = metainfoRaw.get("announce-list");
      verify(rawAnnounceList instanceof List, "Announce list has the wrong type.");
      announceList = Optional.of((List<String>) rawAnnounceList);
    }

    Optional<Instant> creationDate = Optional.empty();
    if (metainfoRaw.containsKey("creation date")) {
      Object rawCreationDate = metainfoRaw.get("creation date");
      verify(rawCreationDate instanceof Integer, "Creation date has the wrong type.");
      creationDate = Optional.of(Instant.ofEpochSecond((Integer) rawCreationDate));
    }

    Optional<String> createdBy = Optional.empty();
    if (metainfoRaw.containsKey("created by")) {
      verify(metainfoRaw.get("created by") instanceof String, "Created by has the wrong type.");
      createdBy = Optional.of((String) metainfoRaw.get("created by"));
    }

    Optional<String> comment = Optional.empty();
    if (metainfoRaw.containsKey("comment")) {
      verify(metainfoRaw.get("comment") instanceof String, "Comment has the wrong type.");
      comment = Optional.of((String) metainfoRaw.get("comment"));
    }

    Optional<String> encoding = Optional.empty();
    if (metainfoRaw.containsKey("encoding")) {
      verify(metainfoRaw.get("encoding") instanceof String, "Encoding has the wrong type");
      encoding = Optional.of((String) metainfoRaw.get("encoding"));
    }

    return new Metainfo(info, announce, announceList, comment, createdBy, creationDate, encoding);
  }
}
