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

import java.io.IOException;

/**
 * A {@link #PINGRESP} Packet is sent by the Server to the Client in response to
 * a {@link #PINGREQ} Packet. It indicates that the Server is alive.
 *
 * @author  Andreas Goransson
 * @version 1.0.0
 * @since   2017-05-06
 */
public class MQTTPingresp extends MQTTMessage {

  /**
   * Construct a {@link #PINGRESP} message from a buffer
   *
   * @param buffer
   *            The buffer
   * @param bufferLength
   *            The buffer length
   */
  public MQTTPingresp(byte[] buffer, int bufferLength) {

    int i = 0;
    // Type (just for clarity sake we'll read it...)
    this.setType((byte) ((buffer[i++] >> 4) & 0x0F));

    // Remaining length
    int multiplier = 1;
    int len = 0;
    byte digit = 0;
    do {
      digit = buffer[i++];
      len += (digit & 127) * multiplier;
      multiplier *= 128;
    } while ((digit & 128) != 0);
    this.setRemainingLength(len);

    // No variable header
    variableHeader = new byte[0];

    // No payload
    payload = new byte[0];
  }

  @Override
  protected byte[] generateFixedHeader() throws MQTTException, IOException {
    // Client doesn't create the PINGRESP
    return null;
  }

  @Override
  protected byte[] generateVariableHeader() throws MQTTException, IOException {
    // Client doesn't create the PINGRESP
    return null;
  }

  @Override
  protected byte[] generatePayload() throws MQTTException, IOException {
    // Client doesn't create the PINGRESP
    return null;
  }

}
