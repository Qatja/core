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

import java.io.IOException;

import se.wetcat.qatja.MQTTException;

/**
 * A {@link se.wetcat.qatja.MQTTConstants#UNSUBACK} package is the response to a
 * {@link se.wetcat.qatja.MQTTConstants#UNSUBSCRIBE} packet
 *
 * @author andreas
 */
public class MQTTUnsuback extends MQTTMessage {

  public static MQTTUnsuback fromBuffer(byte[] buffer) {
    return new MQTTUnsuback(buffer);
  }

  /**
   * Construct a {@link se.wetcat.qatja.MQTTConstants#UNSUBACK} message from a buffer
   *
   * @param buffer the buffer
   */
  private MQTTUnsuback(byte[] buffer) {

    // setBuffer(bufferIn, bufferLength);

    int i = 0;
    // Type (just for clarity sake we'll set it...)
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

    // Get variable header (always length 2 in SUBACK)
    variableHeader = new byte[2];
    System.arraycopy(buffer, i, variableHeader, 0, variableHeader.length);

    // Get payload
    payload = new byte[remainingLength - variableHeader.length];
    if (payload.length > 0)
      System.arraycopy(buffer, i + variableHeader.length, payload, 0,
          remainingLength - variableHeader.length);

    // Get package identifier
    packageIdentifier = (variableHeader[variableHeader.length - 2] >> 8 & 0xFF) | (variableHeader[variableHeader.length - 1] & 0xFF);
  }

  @Override
  protected byte[] generateFixedHeader() throws MQTTException, IOException {
    // Client doesn't create UNSUBACK
    return null;
  }

  @Override
  protected byte[] generateVariableHeader() throws MQTTException, IOException {
    // Client doesn't create UNSUBACK
    return null;
  }

  @Override
  protected byte[] generatePayload() throws MQTTException, IOException {
    // Client doesn't create UNSUBACK
    return null;
  }

}
