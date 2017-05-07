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

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static se.wetcat.qatja.MQTTConstants.CONNECT;
import static se.wetcat.qatja.MQTTConstants.AT_LEAST_ONCE;;

public class MQTTConnectTest {

  private MQTTConnect msg;

  private static final String CLIENT_IDENTIFIER = "myIdentifier";
  private static final String USERNAME = "theUsername";
  private static final String PASSWORD = "thePassword";
  private static final boolean WILL_RETAIN = true;
  private static final byte WILL_QOS = AT_LEAST_ONCE;
  private static final String WILL_TOPIC = "myWillTopic";
  private static final String WILL_MESSAGE = "myWillMesssage";
  private static final boolean CLEAN_SESSION = true;
  private static final int KEEP_ALIVE = 30;

  @Before
  public void setup() {
    msg = new MQTTConnect(CLIENT_IDENTIFIER, USERNAME, PASSWORD, WILL_RETAIN, WILL_QOS, WILL_TOPIC, WILL_MESSAGE,
        CLEAN_SESSION, KEEP_ALIVE);
  }

  @After
  public void teardown() {
    msg = null;
  }

  @Test
  public void testType() {
    byte expected = CONNECT;
    byte actual = msg.getType();
    assertEquals(expected, actual);
  }

  @Test
  public void testIdentifier() {
    assertEquals(CLIENT_IDENTIFIER, msg.getClientIdentifier());
  }

  @Test
  public void testUsername() {
    assertEquals(USERNAME, msg.getUsername());
  }

  @Test
  public void testPassword() {
    assertEquals(PASSWORD, msg.getPassword());
  }

  @Test
  public void testWillRetain() {
    assertEquals(WILL_RETAIN, msg.isWillRetain());
  }

  @Test
  public void testWillQos() {
    assertEquals(WILL_QOS, msg.getWillQoS());
  }

  @Test
  public void testWillTopic() {
    assertEquals(WILL_TOPIC, msg.getWillTopic());
  }

  @Test
  public void testWillMessage() {
    assertEquals(WILL_MESSAGE, msg.getWillMessage());
  }

  @Test
  public void testCleanSession() {
    assertEquals(CLEAN_SESSION, msg.isCleanSession());
  }

  @Test
  public void testKeepAlive() {
    assertEquals(KEEP_ALIVE, msg.getKeepAlive());
  }

}
