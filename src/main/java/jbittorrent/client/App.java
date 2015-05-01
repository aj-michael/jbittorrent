package jbittorrent.client;

import java.io.File;
import java.io.IOException;

import jbittorrent.metainfo.Metainfo;

public class App {

  public static void main(String[] args) throws IOException {
    String filename = "single.torrent";
    File inputFile = new File(filename);
    Metainfo metainfo = Metainfo.fromFile(inputFile);
    System.out.println("It is: ");
    System.out.println(metainfo.announce);
    System.out.println(metainfo.info.getClass());
    System.out.println(metainfo.info.pieceLength);
    System.out.println(metainfo.info.privateBit);
  }

}
