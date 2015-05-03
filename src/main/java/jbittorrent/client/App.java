package jbittorrent.client;

import java.io.File;
import java.io.IOException;

import jbittorrent.metainfo.Metainfo;

public class App {

  public static void main(String[] args) throws IOException {
    String filename = "single.torrent";
    File inputFile = new File(filename);
    Metainfo metainfo = Metainfo.fromFile(inputFile);
  }

}
