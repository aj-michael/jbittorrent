package jbittorrent.metainfo;

import static java.nio.charset.StandardCharsets.UTF_8;

import java.io.File;
import java.io.IOException;

import com.google.common.io.Files;

public final class MetafileParser {

  public static Metainfo parse(File file) throws IOException {
    return parse(Files.toString(file, UTF_8));
  }

  static Metainfo parse(String bencodedInfo) {

    return null;
  }

}
