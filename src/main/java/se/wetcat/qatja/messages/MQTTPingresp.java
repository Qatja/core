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
 * A {@link se.wetcat.qatja.MQTTConstants#PINGRESP} Packet is sent by the Server to the Client in
 * response to a {@link se.wetcat.qatja.MQTTConstants#PINGREQ} Packet. It indicates that the Server
 * is alive.
 *
 * @author Andreas Goransson
 * @version 1.0.0
 * @since 2017-05-06
 */
public class MQTTPingresp extends MQTTMessage {

  public static MQTTPingresp fromBuffer(byte[] buffer) {
    return new MQTTPingresp(buffer);
  }

  /**
   * Construct a {@link se.wetcat.qatja.MQTTConstants#PINGRESP} message from a buffer
   *
   * @param buffer The buffer
   */
  private MQTTPingresp(byte[] buffer) {

    int i = 0;
    // Type (just for clarity sake we'll read it...)
    this.setType((byte) ((buffer[i++] >> 4) & 0x0F));

    // Remaining length
    this.setRemainingLength(0);

    // No variable header
    setVariableHeader(null);

    // No payload
    setPayload(null);
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
