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
import se.wetcat.qatja.MQTTHelper;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import static se.wetcat.qatja.MQTTConstants.AT_MOST_ONCE;
import static se.wetcat.qatja.MQTTConstants.AT_LEAST_ONCE;
import static se.wetcat.qatja.MQTTConstants.EXACTLY_ONCE;
import static se.wetcat.qatja.MQTTConstants.PUBLISH;

/**
 * A {@link #PUBLISH} Control Packet is sent from a Client to a Server or from
 * Server to a Client to transport an Application Message.
 *
 * @author  Andreas Goransson
 * @version 1.0.0
 * @since   2017-05-06
 */
public class MQTTPublish extends MQTTMessage {

  private boolean dup;
  private byte QoS;
  private boolean retain;

  private String topicName;

  /**
   * Construct a {@link #PUBLISH} message
   *
   * @param topic
   *            The topic to publish to
   * @param payload
   *            The message payload
   */
  public MQTTPublish(String topic, byte[] payload) {
    this(topic, payload, AT_MOST_ONCE);
  }

  /**
   * Construct a {@link #PUBLISH} message
   *
   * @param topic
   *            the topic to publish to
   * @param payload
   *            the message payload
   * @param QoS
   *            the quality of service for message
   */
  public MQTTPublish(String topic, byte[] payload, byte QoS) {
    this.type = PUBLISH;

    this.topicName = topic;
    this.payload = payload;

    this.QoS = QoS;

    if (this.QoS > AT_MOST_ONCE)
      // Get a new package identifier
      packageIdentifier = MQTTHelper.getNewPackageIdentifier();
  }

  /**
   * Construct a {@link #PUBLISH} message from a buffer
   *
   * @param buffer
   *            The buffer
   * @param bufferLength
   *            The buffer length
   */
  public MQTTPublish(byte[] buffer, int bufferLength) {

    int i = 0;

    // Type (just for clarity sake we'll set it...)
    this.retain = (buffer[i] >> 0 & 1) == 0x01 ? true : false;
    this.QoS = (byte) ((buffer[i] & ((0x00 << 3) | (0x01 << 2) | (0x01 << 1) | (0x00 << 0))) >> 1);
    this.dup = ((buffer[i] >> 3) & 1) == 1 ? true : false;
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

    len = (byte) ((buffer[i] >> 8 & 0xFF) | (buffer[i + 1] & 0xFF));

    // Get variable header (topic length + 2[topic len] + 2[pkg id])

    switch (getQoS()) {
    case AT_MOST_ONCE:
      // No packageIdentifier
      variableHeader = new byte[len + 2];
      break;
    case AT_LEAST_ONCE:
    case EXACTLY_ONCE:
      // 2 byte packageIdentifier
      variableHeader = new byte[len + 2 + 2];
      break;
    }

    System.arraycopy(buffer, i, variableHeader, 0, variableHeader.length);
    i += variableHeader.length;

    // Get topic
    topicName = new String(variableHeader, 2, len);

    // Get payload
    payload = new byte[remainingLength - variableHeader.length];

    switch (getQoS()) {
    case AT_MOST_ONCE:
      // No packageIdentifier
      break;
    case AT_LEAST_ONCE:
    case EXACTLY_ONCE:
      // 2 byte packageIdentifier
      packageIdentifier = (variableHeader[variableHeader.length - 1]) | (variableHeader[variableHeader.length - 2]);
      break;
    }

    System.arraycopy(buffer, i, payload, 0, payload.length);
  }

  @Override
  protected byte[] generateFixedHeader() throws MQTTException, IOException {
    // FIXED HEADER
    ByteArrayOutputStream out = new ByteArrayOutputStream();

    byte type = this.type;
    byte dup = (byte) (this.dup ? 0x01 : 0x00);
    byte QoS = this.QoS;
    byte retain = (byte) (this.retain ? 0x01 : 0x00);

    // Type and PUBLISH flags
    byte fixed = (byte) ((type << 4) | (dup << 3) | (QoS << 1) | (retain << 0));
    out.write(fixed);

    // Flags (none for PUBLISH)

    // Remaining length
    int length = getVariableHeader().length + getPayload().length;
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

    // TOPIC
    // Topic MUST be UTF-8
    if (MQTTHelper.isUTF8(topicName.getBytes("UTF-8")))
      throw new MQTTException("Invalid topic encoding");
    // Topic MUST NOT contain wildcards
    if (MQTTHelper.hasWildcards(topicName))
      throw new MQTTException("Invalid topic, may not contain wildcards");
    out.write(MQTTHelper.MSB(topicName.length()));
    out.write(MQTTHelper.LSB(topicName.length()));
    out.write(topicName.getBytes("UTF-8"));

    // Package identifier ONLY exists if QoS is above AT_MOST_ONCE
    if (QoS > AT_MOST_ONCE) {
      // Package identifier MUST exist for AT_LEAST_ONCE and EXACTLY_ONCE
      if (packageIdentifier == 0)
        throw new MQTTException("Package identifier must not be 0");

      out.write(MQTTHelper.MSB(packageIdentifier));
      out.write(MQTTHelper.LSB(packageIdentifier));
    }

    return out.toByteArray();
  }

  @Override
  protected byte[] generatePayload() throws MQTTException, IOException {
    return payload;
  }

  /**
   * Mark this message as duplicate
   */
  public void setDup() {
    this.dup = true;
  }

  /**
   * @return The dup
   */
  public boolean isDup() {
    return dup;
  }

  /**
   * @return The qoS
   */
  public byte getQoS() {
    return QoS;
  }

  /**
   * @return The retain
   */
  public boolean isRetain() {
    return retain;
  }

  /**
   * @param retain The retain to set
   */
  public void setRetain(boolean retain) {
    this.retain = retain;
  }

  /**
   * @return The topicName
   */
  public String getTopicName() {
    return topicName;
  }

}
