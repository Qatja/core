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

import se.wetcat.qatja.MQTTConstants;

public class MQTTConnackTest {

  private MQTTPingresp msg;

  @Before
  public void setup() {
    byte[] buffer = { (1 << 5), (1 << 1), (0), (0) };
    msg = new MQTTPingresp(buffer, buffer.length);
  }

  @After
  public void teardown() {
    msg = null;
  }

  @Test
  public void testType() {
    byte expected = MQTTConstants.CONNACK;

    byte actual = msg.getType();

    assertEquals(expected, actual);
  }

  @Test
  public void testRemainingLength() {
    int expected = 2;

    int actual = msg.getRemainingLength();

    assertEquals(expected, actual);
  }

  @Test
  public void testGenerateFixedHeader() throws Exception {
    byte[] expected = null;

    byte[] actual = msg.generateFixedHeader();

    assertEquals(expected, actual);
  }

  @Test
  public void testGenerateVariableHeader() throws Exception {
    byte[] expected = null;

    byte[] actual = msg.generateVariableHeader();

    assertEquals(expected, actual);
  }

  @Test
  public void testGeneratePayload() throws Exception {
    byte[] expected = null;

    byte[] actual = msg.generatePayload();

    assertEquals(expected, actual);
  }

}
