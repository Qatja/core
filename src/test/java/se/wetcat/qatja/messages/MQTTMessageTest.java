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
import org.junit.BeforeClass;
import org.junit.Test;

import se.wetcat.qatja.MQTTConstants;

public class MQTTMessageTest {

  private static class MQTTMessageStub extends MQTTMessage {

    public MQTTMessageStub() {
      super();
    }

    protected byte[] generateFixedHeader() throws se.wetcat.qatja.MQTTException, java.io.IOException {
      return null;
    }

    protected byte[] generateVariableHeader() throws se.wetcat.qatja.MQTTException, java.io.IOException {
      return null;
    }

    protected byte[] generatePayload() throws se.wetcat.qatja.MQTTException, java.io.IOException {
      return null;
    }

  }

  private MQTTMessageStub msg;

  private static final byte TYPE = 0x06;
  private static final int IDENTIFIER = MQTTConstants.PUBLISH;
  private static final byte[] FIXED_HEADER = new byte[] { 0x03, 0x02, 0x01 };
  private static final byte[] VARIABLE_HEADER = new byte[] { 0x09, 0x08, 0x07 };
  private static final byte[] PAYLOAD = new byte[] { 0x06, 0x05, 0x04 };
  private static final int REMAINING_LENGTH = 4;

  @Before
  public void setup() {
    msg = new MQTTMessageStub();
    msg.setType(TYPE);
    msg.setPackageIdentifier(IDENTIFIER);
    msg.setFixedHeader(FIXED_HEADER);
    msg.setVariableHeader(VARIABLE_HEADER);
    msg.setPayload(PAYLOAD);
    msg.setRemainingLength(REMAINING_LENGTH);
  }

  @After
  public void teardown() {
    msg = null;
  }

  @Test
  public void testType() {
    assertEquals(TYPE, msg.getType());
  }

  @Test
  public void testIdentifier() {
    assertEquals(IDENTIFIER, msg.getPackageIdentifier());
  }

  @Test
  public void testFixedHeader() {
    assertEquals(FIXED_HEADER, msg.getFixedHeader());
  }

  @Test
  public void testVariableHeader() {
    assertEquals(VARIABLE_HEADER, msg.getVariableHeader());
  }

  @Test
  public void testPayload() {
    assertEquals(PAYLOAD, msg.getPayload());
  }

  @Test
  public void testRemainingLength() {
    assertEquals(REMAINING_LENGTH, msg.getRemainingLength());
  }

}
