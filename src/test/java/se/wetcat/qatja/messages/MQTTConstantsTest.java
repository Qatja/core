package se.wetcat.qatja.messages;

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

import se.wetcat.qatja.MQTTConstants;

public abstract class MQTTConstantsTest {

  @Test
  public void testConnect() {
    byte expected = 1;
    assertEquals(expected, MQTTConstants.CONNECT);
  }

  @Test
  public void testConnack() {
    byte expected = 2;
    assertEquals(expected, MQTTConstants.CONNACK);
  }

  @Test
  public void testPublish() {
    byte expected = 3;
    assertEquals(expected, MQTTConstants.PUBLISH);
  }

  @Test
  public void testPuback() {
    byte expected = 4;
    assertEquals(expected, MQTTConstants.PUBACK);
  }

  @Test
  public void testPubrec() {
    byte expected = 5;
    assertEquals(expected, MQTTConstants.PUBREC);
  }

  @Test
  public void testPubrel() {
    byte expected = 6;
    assertEquals(expected, MQTTConstants.PUBREL);
  }

  @Test
  public void testPubcomp() {
    byte expected = 7;
    assertEquals(expected, MQTTConstants.PUBCOMP);
  }

  @Test
  public void testSubscribe() {
    byte expected = 8;
    assertEquals(expected, MQTTConstants.SUBSCRIBE);
  }

  @Test
  public void testSuback() {
    byte expected = 9;
    assertEquals(expected, MQTTConstants.SUBACK);
  }

  @Test
  public void testUnsubscribe() {
    byte expected = 10;
    assertEquals(expected, MQTTConstants.UNSUBSCRIBE);
  }

  @Test
  public void testUnsuback() {
    byte expected = 11;
    assertEquals(expected, MQTTConstants.UNSUBACK);
  }

  @Test
  public void testPingreq() {
    byte expected = 12;
    assertEquals(expected, MQTTConstants.PINGREQ);
  }

  @Test
  public void testPingresp() {
    byte expected = 13;
    assertEquals(expected, MQTTConstants.PINGRESP);
  }

  @Test
  public void testDisconnect() {
    byte expected = 14;
    assertEquals(expected, MQTTConstants.DISCONNECT);
  }

  @Test
  public void testAtMostOnce() {
    byte expected = 0;
    assertEquals(expected, MQTTConstants.AT_MOST_ONCE);
  }

  @Test
  public void testAtLeastOnce() {
    byte expected = 1;
    assertEquals(expected, MQTTConstants.AT_LEAST_ONCE);
  }

  @Test
  public void testExactlyOnce() {
    byte expected = 2;
    assertEquals(expected, MQTTConstants.EXACTLY_ONCE);
  }

  @Test
  public void testConnectionAccepted() {
    byte expected = 0;
    assertEquals(expected, MQTTConstants.CONNECTION_ACCEPTED);
  }

  @Test
  public void testConnectionRefusedVersion() {
    byte expected = 1;
    assertEquals(expected, MQTTConstants.CONNECTION_REFUSED_VERSION);
  }

  @Test
  public void testConnectionRefusedIdentifier() {
    byte expected = 2;
    assertEquals(expected, MQTTConstants.CONNECTION_REFUSED_IDENTIFIER);
  }

  @Test
  public void testConnectionRefusedServer() {
    byte expected = 3;
    assertEquals(expected, MQTTConstants.CONNECTION_REFUSED_SERVER);
  }

  @Test
  public void testConnectionRefusedUser() {
    byte expected = 4;
    assertEquals(expected, MQTTConstants.CONNECTION_REFUSED_USER);
  }

  @Test
  public void testConnectionRefusedAuth() {
    byte expected = 5;
    assertEquals(expected, MQTTConstants.CONNECTION_REFUSED_AUTH);
  }

  @Test
  public void testSubscribeSuccessAtMostOnce() {
    byte expected = 0;
    assertEquals(expected, MQTTConstants.SUBSCRIBE_SUCCESS_AT_MOST_ONCE);
  }

  @Test
  public void testSubscribeSuccessAtLeastOnce() {
    byte expected = 1;
    assertEquals(expected, MQTTConstants.SUBSCRIBE_SUCCESS_AT_LEAST_ONCE);
  }

  @Test
  public void testSubscribeSuccessExactlyOnce() {
    byte expected = 2;
    assertEquals(expected, MQTTConstants.SUBSCRIBE_SUCCESS_EXACTLY_ONCE);
  }

  @Test
  public void testSubscribeFailure() {
    byte expected = 80;
    assertEquals(expected, MQTTConstants.SUBSCRIBE_FAILURE);
  }

}
