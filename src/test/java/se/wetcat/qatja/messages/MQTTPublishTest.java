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

import static se.wetcat.qatja.MQTTConstants.AT_LEAST_ONCE;
import static se.wetcat.qatja.MQTTConstants.PUBLISH;;

public class MQTTPublishTest {

  private MQTTPublish msg;

  private static final byte[] PAYLOAD = "this is a message".getBytes();
  private static final byte QOS = AT_LEAST_ONCE;
  private static final boolean RETAIN = true;
  private static final String TOPIC = "my/test/topic/#";

  @Before
  public void setup() {
    msg = new MQTTPublish(TOPIC, PAYLOAD, QOS);
    msg.setDup();
    msg.setRetain(RETAIN);
  }

  @After
  public void teardown() {
    msg = null;
  }

  @Test
  public void testType() {
    byte expected = PUBLISH;
    byte actual = msg.getType();
    assertEquals(expected, actual);
  }

  @Test
  public void testPayload() {
    byte[] expected = PAYLOAD;
    byte[] actual = msg.getPayload();
    assertEquals(expected, actual);
  }

  @Test
  public void testDup() {
    boolean expected = true;
    boolean actual = msg.isDup();
    assertEquals(expected, actual);
  }

  @Test
  public void testQos() {
    byte expected = QOS;
    byte actual = msg.getQoS();
    assertEquals(expected, actual);
  }

  @Test
  public void testRetain() {
    boolean expected = RETAIN;
    boolean actual = msg.isRetain();
    assertEquals(expected, actual);
  }

  @Test
  public void testTopic() {
    String expected = TOPIC;
    String actual = msg.getTopicName();
    assertEquals(expected, actual);
  }

}
