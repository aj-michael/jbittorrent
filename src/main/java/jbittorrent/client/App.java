package jbittorrent.client;

import java.io.File;

import jbittorrent.metainfo.Metainfo;

public class App {

  public static void main(String[] args) {
    String filename = "test.torrent";
    File inputFile = new File(filename);
    Metainfo metainfo = Metainfo.fromFile(inputFile);
  }

}
