package jbittorrent.bencoding;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.List;
import java.util.Map;
import java.util.Set;

import jbittorrent.bencoding.Decoding.InvalidEncodingException;

import org.junit.Test;

import com.google.common.collect.ImmutableSet;

public class DecodingTest {

  @Test
  public void testSuccessfulReadString() {
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

  @Test
  public void testSuccessfulReadList() {
    String encodedData = "l4:spam4:eggsei3546e";
    Intermediary<List<Object>> intermediary = Decoding.readList(encodedData);
    assertEquals("i3546e", intermediary.encodedRemainder);
    List<Object> list = intermediary.decodedValue;
    assertEquals(2, list.size());
    assertTrue(list.get(0) instanceof String);
    assertEquals("spam", list.get(0));
    assertTrue(list.get(1) instanceof String);
    assertEquals("eggs", list.get(1));
  }
  
  @Test(expected = IllegalArgumentException.class)
  public void testInvalidReadList() {
    String encodedData = "4:spamd3:cow3:mooe";
    Decoding.readList(encodedData);
  }
  
  @Test
  public void testSuccessfulReadDictionary() {
    String encodedData = "d3:cow3:moo4:spami45eei123e";
    Intermediary<Map<String, Object>> intermediary = Decoding.readDictionary(encodedData);
    assertEquals("i123e", intermediary.encodedRemainder);
    Map<String, Object> dictionary = intermediary.decodedValue;
    assertEquals(2, dictionary.size());
    Set<String> expectedKeys = ImmutableSet.of("cow", "spam");
    assertEquals(expectedKeys, dictionary.keySet());
    assertEquals("moo", dictionary.get("cow"));
    assertEquals(45, dictionary.get("spam"));
  }
  
  @Test(expected = IllegalArgumentException.class)
  public void testInvalidReadDictionary() {
    String encodedData = "l4:spam4:eggsei3546e";
    Decoding.readDictionary(encodedData);
  }
  
  @SuppressWarnings("unchecked")
  @Test
  public void testSuccessfulReadObject() {
    String encodedData = "l4:spam4:eggsei3546e";
    Intermediary<? extends Object> intermediary = Decoding.readObject(encodedData);
    assertEquals("i3546e", intermediary.encodedRemainder);
    List<Object> list = (List<Object>) intermediary.decodedValue;
    assertEquals(2, list.size());
    assertTrue(list.get(0) instanceof String);
    assertEquals("spam", list.get(0));
    assertTrue(list.get(1) instanceof String);
    assertEquals("eggs", list.get(1));
  }
  
  @Test(expected = InvalidEncodingException.class)
  public void testInvalidReadObject() {
    String encodedData = "adam";
    Decoding.readObject(encodedData);
  }
}
