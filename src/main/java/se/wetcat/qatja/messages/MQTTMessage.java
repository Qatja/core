package se.wetcat.qatja.messages;

/*
 * Copyright (C) 2012 Andreas Goransson, David Cuartielles
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

import se.wetcat.qatja.*;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

/**
 * MQTT Message super class, should never be instantiated.
 *
 * @author  Andreas Goransson
 * @version 1.0
 * @since   2017-05-06
 */
public abstract class MQTTMessage implements MQTTConstants, MQTTConnectionConstants, MQTTVersion {

  protected byte flags;

  protected int packageIdentifier;

  protected byte[] fixedHeader;
  protected byte[] variableHeader;
  protected byte[] payload;

  protected byte type;
  protected int remainingLength;

  protected abstract byte[] generateFixedHeader() throws MQTTException, IOException;

  protected abstract byte[] generateVariableHeader() throws MQTTException, IOException;

  protected abstract byte[] generatePayload() throws MQTTException, IOException;

  /**
   * @return The packageIdentifier
   */
  public int getPackageIdentifier() {
    return packageIdentifier;
  }

  /**
   * @param packageIdentifier
   *            The packageIdentifier to set
   */
  public void setPackageIdentifier(int packageIdentifier) {
    this.packageIdentifier = packageIdentifier;
  }

  /**
   * @return The fixedHeader
   */
  public byte[] getFixedHeader() {
    return fixedHeader;
  }

  /**
   * @param fixedHeader
   *            The fixedHeader to set
   */
  public void setFixedHeader(byte[] fixedHeader) {
    this.fixedHeader = fixedHeader;
  }

  /**
   * @return The variableHeader
   */
  public byte[] getVariableHeader() {
    return variableHeader;
  }

  /**
   * @param variableHeader
   *            The variableHeader to set
   */
  public void setVariableHeader(byte[] variableHeader) {
    this.variableHeader = variableHeader;
  }

  /**
   * @return The payload
   */
  public byte[] getPayload() {
    return payload;
  }

  /**
   * @param payload
   *            The payload to set
   */
  public void setPayload(byte[] payload) {
    this.payload = payload;
  }

  /**
   * @return The type
   */
  public byte getType() {
    return type;
  }

  /**
   * @param type
   *            The type to set
   */
  public void setType(byte type) {
    this.type = type;
  }

  /**
   * @return The remainingLength
   */
  public int getRemainingLength() {
    return remainingLength;
  }

  /**
   * @param remainingLength
   *            The remainingLength to set
   */
  public void setRemainingLength(int remainingLength) {
    this.remainingLength = remainingLength;
  }

  protected byte[] getProtocol() throws IOException {
    ByteArrayOutputStream out = new ByteArrayOutputStream();

    // Protocol Name (Used by all messages)
    out.write((NAME_311.getBytes("UTF-8").length >> 8) & 0xFF); // MSB
    out.write(NAME_311.getBytes("UTF-8").length & 0xFF); // LSB
    out.write(NAME_311.getBytes("UTF-8"));

    // Protocol Level (Used by all messages)
    out.write((byte) VERSION_311);

    return out.toByteArray();
  }

  public byte[] get() throws IOException, MQTTException {
    ByteArrayOutputStream out = new ByteArrayOutputStream();

    this.payload = generatePayload();
    this.variableHeader = generateVariableHeader();
    this.fixedHeader = generateFixedHeader();

    out.write(fixedHeader);
    out.write(variableHeader);
    out.write(payload);

    return out.toByteArray();
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();

    sb.append("MQTT Package ").append("\n");
    sb.append("  type: " + MQTTHelper.decodePackageName(this.getType())).append("\n");
    sb.append("  remaining length: " + this.getRemainingLength()).append("\n");
    sb.append("  package identifier: " + this.getPackageIdentifier());

    return sb.toString();
  }

}
