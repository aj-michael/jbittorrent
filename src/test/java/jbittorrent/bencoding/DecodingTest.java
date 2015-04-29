package jbittorrent.bencoding;

import static org.junit.Assert.assertEquals;
import jbittorrent.bencoding.Decoding.Intermediary;

import org.junit.Test;

public class DecodingTest {

  @Test public void testSuccessfulReadString() {
    String encodedData = "4:spamd3:cow3:mooe";
    Intermediary<String> intermediary = Decoding.readString(encodedData);
    assertEquals("spam", intermediary.decodedValue);
    assertEquals("d3:cow3:mooe", intermediary.encodedRemainder);
  }
  
  @Test(expected = IllegalArgumentException.class)
  public void testInvalidReadString() {
    String encodedData = "i3549e4:spamd3:cow3:mooe";
    Decoding.readString(encodedData);
  }
  
  @Test
  public void testSuccessfulReadInteger() {
    String encodedData = "i3549e4:spamd3:cow3:mooe";
    Intermediary<Integer> intermediary = Decoding.readInteger(encodedData);
    assertEquals(new Integer(3549), intermediary.decodedValue);
    assertEquals("4:spamd3:cow3:mooe", intermediary.encodedRemainder);    
  }
  
  @Test(expected = IllegalArgumentException.class)
  public void testInvalidReadInteger() {
    String encodedData = "4:spamd3:cow3:mooe";
    Decoding.readInteger(encodedData);
  }
  
}
