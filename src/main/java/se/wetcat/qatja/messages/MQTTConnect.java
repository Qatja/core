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
import static se.wetcat.qatja.MQTTConstants.CONNECT;
import static se.wetcat.qatja.MQTTConstants.MIN_LENGTH;
import static se.wetcat.qatja.MQTTConstants.MAX_LENGTH;
import static se.wetcat.qatja.MQTTVersion.VERSION_311;

/**
 * After a Network Connection is established by a Client to a Server, the first
 * Packet sent from the Client to the Server MUST be a {@link #CONNECT} Packet
 * [MQTT-3.1.0-1].
 *
 * A Client can only send the {@link #CONNECT} Packet once over a Network
 * Connection. The Server MUST process a second {@link #CONNECT} Packet sent
 * from a Client as a protocol violation and disconnect the Client
 * [MQTT-3.1.0-2].  See section 4.8 for information about handling errors.
 *
 * The payload contains one or more encoded fields. They specify a unique Client
 * identifier for the Client, a Will topic, Will Message, User Name and
 * Password. All but the Client identifier are optional and their presence is
 * determined based on flags in the variable header.
 *
 * @author  Andreas Goransson
 * @version 1.0.0
 * @since   2017-05-06
 */
public class MQTTConnect extends MQTTMessage {

  /**
   * The identifier for the client, MUST be present in all {@link #CONNECT}
   * messages
   */
  private String clientIdentifier;

  protected byte protocolVersion;

  protected String protocolName;

  /**
   * Position 5 of Connect Flags. If the Will Flag is set to 0, the Will
   * Retain Flag MUST be set to 0.
   */
  private boolean willRetain;
  private boolean willFlag = true;
  private String willTopic = "wtopic";
  private String willMessage = "wmessage";
  private byte willQoS;

  private String username;
  private String password;

  private boolean cleanSession;
  private int keepAlive;

  /**
   * Default MQTTConnect constructor. All flags set to default values, and no
   * client identifier set (cleanSession set to true). Keep alive set to 10
   * seconds.
   */
  public MQTTConnect() {
    this("");
  }

  /**
   * Create a new MQTT Connect message
   * 
   * @param clientIdentifier
   *            The client identifier
   */
  public MQTTConnect(String clientIdentifier) {
    this(clientIdentifier, null, null);
  }

  /**
   * Create a new MQTT Connect message
   * 
   * @param clientIdentifier
   *            The client identifier
   * @param username
   *            The client username
   * @param password
   *            The client password
   */
  public MQTTConnect(String clientIdentifier, String username, String password) {
    this(clientIdentifier, username, password, false);
  }

  /**
   * Create a new MQTT Connect message
   * 
   * @param clientIdentifier
   *            The client identifier
   * @param username
   *            The client username
   * @param password
   *            The client password
   * @param cleanSession
   *            Connect with clear session state
   */
  public MQTTConnect(String clientIdentifier, String username, String password, boolean cleanSession) {
    this(clientIdentifier, username, password, false, AT_MOST_ONCE, null, null, cleanSession, 10);
  }

  /**
   * Create a new MQTT Connect message
   * 
   * @param clientIdentifier
   *            The client identifier
   * @param username
   *            The client username
   * @param password
   *            The client password
   * @param willRetain
   *            Set last will and testament
   * @param willQoS
   *            Last will QoS
   * @param willTopic
   *            Last will topic
   * @param willMessage
   *            Last will message
   * @param cleanSession
   *            Connect with clean session state
   * @param keepAlive
   *            Time interval in seconds
   */
  public MQTTConnect(String clientIdentifier, String username, String password, boolean willRetain, byte willQoS,
      String willTopic, String willMessage, boolean cleanSession, int keepAlive) {
    this.type = CONNECT;

    this.clientIdentifier = clientIdentifier;
    this.cleanSession = cleanSession;
    this.keepAlive = keepAlive;

    // Default flag values
    this.willRetain = willRetain;
    this.willFlag = (willTopic != null && willMessage != null);
    this.willTopic = willTopic;
    this.willMessage = willMessage;
    this.willQoS = AT_MOST_ONCE;

    this.username = username;
    this.password = password;
  }

  /**
   * Create MQTT Connect message from a byte[].
   *
   * @param buffer
   *            Buffer from which to create message
   */
  public MQTTConnect(byte[] buffer) {
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

    // Get length of protocol name
    len = ((buffer[i] >> 8) & 0xFF) | (buffer[i + 1] & 0xFF);

    // Get variable header (always length 10 in CONNECT)
    variableHeader = new byte[10];
    System.arraycopy(buffer, i, variableHeader, 0, variableHeader.length);

    // Get payload
    payload = new byte[remainingLength - variableHeader.length];
    System.arraycopy(buffer, i + variableHeader.length, payload, 0, remainingLength - variableHeader.length);

    i = 2;
    protocolName = new String(variableHeader, i, len);
    protocolVersion = variableHeader[(i += len)];
    flags = variableHeader[++i];
    keepAlive = (variableHeader[++i] >> 8) & 0xFF | (variableHeader[++i] & 0xFF);
  }

  /**
   * Set last will and testament
   *  
   * @param willTopic
   *            Set the last will topic
   * @param willMessage
   *            Set last will message
   * @param willRetain
   *            Set last will retain flag
   * @param willQoS
   *            Set last will QoS
   */
  public void setWill(String willTopic, String willMessage, boolean willRetain, byte willQoS) {
    this.willFlag = true;
    this.willTopic = willTopic;
    this.willMessage = willMessage;
    this.willQoS = willQoS;
  }

  @Override
  protected byte[] generateFixedHeader() throws MQTTException, IOException {
    // FIXED HEADER
    ByteArrayOutputStream out = new ByteArrayOutputStream();

    // Type
    out.write((byte) (type << 4));

    // Flags (none for CONNECT)

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

    // Some packages has this
    if (protocolName == null)
      out.write(getProtocol());
    else {
      // For some reason I need to test this in the interop testing...
      out.write((protocolName.getBytes("UTF-8").length >> 8) & 0xFF); // MSB
      out.write(protocolName.getBytes("UTF-8").length & 0xFF); // LSB
      out.write(protocolName.getBytes("UTF-8"));

      // Protocol Level (Used by all messages)
      out.write((byte) VERSION_311);
    }

    // CONNECT FLAGS
    byte flags = (byte) (0x00);

    byte username = (byte) ((this.username != null && this.username.length() >= 0) ? 0x01 : 0x00);
    byte password = (byte) ((this.password != null && this.password.length() >= 0) ? 0x01 : 0x00);
    byte willRetain = (byte) (this.willRetain ? 0x01 : 0x00);
    byte willQoS = this.willQoS;
    byte willFlag = (byte) ((this.willRetain && this.willFlag) ? 0x01 : 0x00);
    byte cleanSession = (byte) (this.cleanSession ? 0x01 : 0x00);
    byte RESERVED = (byte) 0x00;

    flags = (byte) ((username << 7) | (password << 6) | (willRetain << 5) | (willQoS << 3) | (willFlag << 2)
        | (cleanSession << 1) | (RESERVED << 0));

    // Write flags
    out.write(flags);

    // Keep alive (Use lower two bytes of integer)
    out.write(MQTTHelper.MSB(keepAlive));
    out.write(MQTTHelper.LSB(keepAlive));

    return out.toByteArray();
  }

  @Override
  protected byte[] generatePayload() throws MQTTException, IOException {
    // PAYLOAD
    ByteArrayOutputStream payload = new ByteArrayOutputStream();

    // CLIENT IDENTIFIER
    // ClientID MUST be present and MUST be the first field in payload
    if (clientIdentifier == null)
      throw new MQTTException("Client identifier invalid");
    // ClientID MUST be UTF-8
    if (MQTTHelper.isUTF8(clientIdentifier.getBytes("UTF-8")))
      throw new MQTTException("Invalid identifier encoding");
    // ClientID MUST be length between 0 and 65535
    if (clientIdentifier.getBytes("UTF-8").length < MIN_LENGTH
        || clientIdentifier.getBytes("UTF-8").length > MAX_LENGTH)
      throw new MQTTException("Client identifier invalid length");

    payload.write(MQTTHelper.MSB(clientIdentifier.getBytes("UTF-8"))); // MSB
    payload.write(MQTTHelper.LSB(clientIdentifier.getBytes("UTF-8"))); // LSB
    payload.write(clientIdentifier.getBytes("UTF-8"));

    // WILL
    if (willFlag) {
      // Will topic MUST be present
      if (willTopic == null)
        throw new MQTTException("Will flag set, will topic MUST be present");
      // Will topic MUST be UTF-8
      if (MQTTHelper.isUTF8(willTopic.getBytes("UTF-8")))
        throw new MQTTException("Invalid will topic encoding");
      // Write Will topic
      payload.write(MQTTHelper.MSB(willTopic.getBytes("UTF-8"))); // MSB
      payload.write(MQTTHelper.LSB(willTopic.getBytes("UTF-8"))); // LSB
      payload.write(willTopic.getBytes("UTF-8"));

      // Will message MUST be UTF-8
      if (willMessage == null)
        throw new MQTTException("Will flag set, will message MUST be present");
      if (MQTTHelper.isUTF8(willMessage.getBytes("UTF-8")))
        throw new MQTTException("Invalid will message encoding");
      // Write Will message
      payload.write(MQTTHelper.MSB(willMessage.getBytes("UTF-8"))); // MSB
      payload.write(MQTTHelper.LSB(willMessage.getBytes("UTF-8"))); // LSB
      payload.write(willMessage.getBytes("UTF-8"));
    }

    // USERNAME
    if (username != null && username.length() >= 0) {
      // Username MUST be UTF-8
      if (MQTTHelper.isUTF8(username.getBytes("UTF-8")))
        throw new MQTTException("Invalid username encoding");
      // Write username
      payload.write(MQTTHelper.MSB(username.getBytes("UTF-8"))); // MSB
      payload.write(MQTTHelper.LSB(username.getBytes("UTF-8"))); // LSB
      payload.write(username.getBytes("UTF-8"));
    }

    // PASSWORD
    if (password != null && password.length() >= 0) {
      // Password MUST be UTF-8
      if (MQTTHelper.isUTF8(password.getBytes("UTF-8")))
        throw new MQTTException("Invalid password encoding");
      // Password contains 0 to 65535 bytes
      if (password.getBytes("UTF-8").length < MIN_LENGTH || password.getBytes("UTF-8").length > MAX_LENGTH)
        throw new MQTTException("Password invalid length");
      // Write password
      payload.write(MQTTHelper.MSB(password.getBytes("UTF-8"))); // MSB
      payload.write(MQTTHelper.LSB(password.getBytes("UTF-8"))); // LSB
      payload.write(password.getBytes("UTF-8"));
    }

    return payload.toByteArray();
  }

  /**
   * @return The clientIdentifier
   */
  public String getClientIdentifier() {
    return clientIdentifier;
  }

  /**
   * @param clientIdentifier
   *            The clientIdentifier to set
   */
  public void setClientIdentifier(String clientIdentifier) {
    this.clientIdentifier = clientIdentifier;
  }

  /**
   * @return The protocolVersion
   */
  public byte getProtocolVersion() {
    return protocolVersion;
  }

  /**
   * @param protocolVersion
   *            The protocolVersion to set
   */
  public void setProtocolVersion(byte protocolVersion) {
    this.protocolVersion = protocolVersion;
  }

  /**
   * @return The protocolName
   */
  public String getProtocolName() {
    return protocolName;
  }

  /**
   * @param protocolName
   *            The protocolName to set
   */
  public void setProtocolName(String protocolName) {
    this.protocolName = protocolName;
  }

  /**
   * @return The willRetain
   */
  public boolean isWillRetain() {
    return willRetain;
  }

  /**
   * @param willRetain
   *            The willRetain to set
   */
  public void setWillRetain(boolean willRetain) {
    this.willRetain = willRetain;
  }

  /**
   * @return The willFlag
   */
  public boolean isWillFlag() {
    return willFlag;
  }

  /**
   * @param willFlag
   *            The willFlag to set
   */
  public void setWillFlag(boolean willFlag) {
    this.willFlag = willFlag;
  }

  /**
   * @return The willTopic
   */
  public String getWillTopic() {
    return willTopic;
  }

  /**
   * @param willTopic
   *            The willTopic to set
   */
  public void setWillTopic(String willTopic) {
    this.willTopic = willTopic;
  }

  /**
   * @return The willMessage
   */
  public String getWillMessage() {
    return willMessage;
  }

  /**
   * @param willMessage
   *            The willMessage to set
   */
  public void setWillMessage(String willMessage) {
    this.willMessage = willMessage;
  }

  /**
   * @return The willQoS
   */
  public byte getWillQoS() {
    return willQoS;
  }

  /**
   * @param willQoS
   *            The willQoS to set
   */
  public void setWillQoS(byte willQoS) {
    this.willQoS = willQoS;
  }

  /**
   * @return The username
   */
  public String getUsername() {
    return username;
  }

  /**
   * @param username
   *            The username to set
   */
  public void setUsername(String username) {
    this.username = username;
  }

  /**
   * @return The password
   */
  public String getPassword() {
    return password;
  }

  /**
   * @param password
   *            The password to set
   */
  public void setPassword(String password) {
    this.password = password;
  }

  /**
   * @return The cleanSession
   */
  public boolean isCleanSession() {
    return cleanSession;
  }

  /**
   * @param cleanSession
   *            The cleanSession to set
   */
  public void setCleanSession(boolean cleanSession) {
    this.cleanSession = cleanSession;
  }

  /**
   * @return The keepAlive
   */
  public int getKeepAlive() {
    return keepAlive;
  }

  /**
   * @param keepAlive
   *            The keepAlive to set
   */
  public void setKeepAlive(int keepAlive) {
    this.keepAlive = keepAlive;
  }

}
