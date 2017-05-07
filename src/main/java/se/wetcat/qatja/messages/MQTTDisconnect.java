package se.wetcat.qatja.messages;

/*
 * Copyright (C) 2014 Andreas Goransson
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

import se.wetcat.qatja.MQTTException;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import static se.wetcat.qatja.MQTTConstants.DISCONNECT;

/**
 * The {@link #DISCONNECT} Packet is the final Control Packet sent from the 
 * Client to the Server. It indicates that the Client is disconnecting cleanly.
 *
 * @author  Andreas Goransson
 * @version 1.0.0
 * @since   2017-05-06
 */
public class MQTTDisconnect extends MQTTMessage {

  /**
   * Construct a {@link #DISCONNECT} message
   */
  public MQTTDisconnect() {
    this.setType(DISCONNECT);
  }

  @Override
  protected byte[] generateFixedHeader() throws MQTTException, IOException {
    // FIXED HEADER
    ByteArrayOutputStream out = new ByteArrayOutputStream();

    // Type
    out.write((byte) (type << 4));

    // Flags (none for PING)

    // Remaining length
    int length = variableHeader.length + payload.length;
    this.setRemainingLength(length);
    do {
      byte digit = (byte) (length % 128);
      length /= 128;
      if (length > 0)
        digit = (byte) (digit | 0x80);
      out.write(digit);
    } while (length > 0);

    return out.toByteArray();
  }

  @Override
  protected byte[] generateVariableHeader() throws MQTTException, IOException {
    ByteArrayOutputStream out = new ByteArrayOutputStream();
    return out.toByteArray();
  }

  @Override
  protected byte[] generatePayload() throws MQTTException, IOException {
    ByteArrayOutputStream out = new ByteArrayOutputStream();
    return out.toByteArray();
  }

}
