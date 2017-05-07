package se.wetcat.qatja;

/*
 * Copyright (C) 2017 Andreas Goransson
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import se.wetcat.qatja.messages.MQTTConnect;

public class MQTTHelperTest {

  @Test
  public void testHasWildcards() {
    boolean expected = true;
    boolean actual = MQTTHelper.hasWildcards("home/+/sensors/");
    assertEquals(expected, actual);
  }

  @Test
  public void testNoWildcards() {
    boolean expected = false;
    boolean actual = MQTTHelper.hasWildcards("home/kitchen/sensors/");
    assertEquals(expected, actual);
  }

  @Test
  public void testNextPackageIdentifier() {
    int expected = 2;
    int actual = MQTTHelper.getNewPackageIdentifier();
    assertEquals(expected, actual);
  }

  @Test
  public void testBufferMSB() {
    byte expected = 0;
    byte[] buffer = "MQTT".getBytes();
    byte actual = MQTTHelper.MSB(buffer);
    assertEquals(expected, actual);
  }

  @Test
  public void testBufferLSB() {
    byte expected = 4;
    byte[] buffer = "MQTT".getBytes();
    byte actual = MQTTHelper.LSB(buffer);
    assertEquals(expected, actual);
  }

  @Test
  public void testIntMSB() {
    byte expected = 0;
    int len = "MQTT".length();
    byte actual = MQTTHelper.MSB(len);
    assertEquals(expected, actual);
  }

  @Test
  public void testIntLSB() {
    byte expected = 4;
    int len = "MQTT".length();
    byte actual = MQTTHelper.LSB(len);
    assertEquals(expected, actual);
  }

  @Test
  public void testDecodeType() {
    byte[] buffer = { (1 << 5), (1 << 1), (0), (0) };
    byte expected = MQTTConstants.CONNACK;
    byte actual = MQTTHelper.decodeType(buffer);
    assertEquals(expected, actual);
  }

  @Test
  public void testDecodePackageName() {
    MQTTConnect msg = new MQTTConnect();
    String expected = "CONNECT";
    String actual = MQTTHelper.decodePackageName(msg);
    assertEquals(expected, actual);
  }

}
