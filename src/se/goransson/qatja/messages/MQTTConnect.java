package se.goransson.qatja.messages;

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

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import se.goransson.qatja.MQTTException;
import se.goransson.qatja.MQTTHelper;

/**
 * MQTT {@link #CONNECT} message
 * 
 * @author andreas
 * 
 */
public class MQTTConnect extends MQTTMessage {

	// Connect specific data
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
	 * Create MQTT Connect package using identifier.
	 * 
	 * @param clientIdentifier
	 */
	public MQTTConnect(String clientIdentifier) {
		this(clientIdentifier, null, null);
	}

	public MQTTConnect(String clientIdentifier, String username, String password) {
		this(clientIdentifier, username, password, false);
	}

	public MQTTConnect(String clientIdentifier, String username,
			String password, boolean cleanSession) {
		this(clientIdentifier, username, password, false, AT_MOST_ONCE, null,
				null, cleanSession, 10);
	}

	public MQTTConnect(String clientIdentifier, String username,
			String password, boolean willRetain, byte willQoS,
			String willTopic, String willMessage, boolean cleanSession,
			int keepAlive) {
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

	public void setWill(String willTopic, String willMessage,
			boolean willRetain, byte willQoS) {
		this.willFlag = true;
		this.willTopic = willTopic;
		this.willMessage = willMessage;
		this.willQoS = willQoS;
	}

	/**
	 * Create MQTT message from a byte[].
	 * 
	 * @param buffer
	 */
	public MQTTConnect(byte[] buffer, int bufferLength) {

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

		// Get length of protocol name
		len = ((buffer[i] >> 8) & 0xFF) | (buffer[i + 1] & 0xFF);

		// Get variable header (always length 10 in CONNECT)
		variableHeader = new byte[10];
		System.arraycopy(buffer, i, variableHeader, 0, variableHeader.length);

		// Get payload
		payload = new byte[remainingLength - variableHeader.length];
		System.arraycopy(buffer, i + variableHeader.length, payload, 0,
				remainingLength - variableHeader.length);

		i = 2;
		protocolName = new String(variableHeader, i, len);
		protocolVersion = variableHeader[(i += len)];
		flags = variableHeader[++i];
		keepAlive = (variableHeader[++i] >> 8) & 0xFF
				| (variableHeader[++i] & 0xFF);
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

		// Most packages has this
		out.write(getProtocol());

		// CONNECT FLAGS
		byte flags = (byte) (0x00);
		
		byte username = (byte) ((this.username != null && this.username.length() >= 0) ? 0x01 : 0x00);
		byte password = (byte) ((this.password != null && this.password.length() >= 0) ? 0x01 : 0x00);
		byte willRetain = (byte) (this.willRetain ? 0x01 : 0x00);
		byte willQoS = this.willQoS;
		byte willFlag = (byte) ((this.willRetain && this.willFlag) ? 0x01 : 0x00);
		byte cleanSession = (byte) (this.cleanSession ? 0x01 : 0x00);
		byte RESERVED = (byte) 0x00;
		
		flags = (byte) ((username << 7) | (password << 6) | (willRetain << 5) | (willQoS << 3) | (willFlag << 2) | (cleanSession << 1) | (RESERVED << 0) );
				
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
				throw new MQTTException(
						"Will flag set, will topic MUST be present");
			// Will topic MUST be UTF-8
			if (MQTTHelper.isUTF8(willTopic.getBytes("UTF-8")))
				throw new MQTTException("Invalid will topic encoding");
			// Write Will topic
			payload.write(MQTTHelper.MSB(willTopic.getBytes("UTF-8"))); // MSB
			payload.write(MQTTHelper.LSB(willTopic.getBytes("UTF-8"))); // LSB
			payload.write(willTopic.getBytes("UTF-8"));

			// Will message MUST be UTF-8
			if (willMessage == null)
				throw new MQTTException(
						"Will flag set, will message MUST be present");
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
			if (password.getBytes("UTF-8").length < MIN_LENGTH
					|| password.getBytes("UTF-8").length > MAX_LENGTH)
				throw new MQTTException("Password invalid length");
			// Write password
			payload.write(MQTTHelper.MSB(password.getBytes("UTF-8"))); // MSB
			payload.write(MQTTHelper.LSB(password.getBytes("UTF-8"))); // LSB
			payload.write(password.getBytes("UTF-8"));
		}

		return payload.toByteArray();
	}

	/**
	 * @return the clientIdentifier
	 */
	public String getClientIdentifier() {
		return clientIdentifier;
	}

	/**
	 * @return the willRetain
	 */
	public boolean isWillRetain() {
		return willRetain;
	}

	/**
	 * @return the willFlag
	 */
	public boolean isWillFlag() {
		return willFlag;
	}

	/**
	 * @return the willTopic
	 */
	public String getWillTopic() {
		return willTopic;
	}

	/**
	 * @return the willMessage
	 */
	public String getWillMessage() {
		return willMessage;
	}

	/**
	 * @return the willQoS
	 */
	public byte getWillQoS() {
		return willQoS;
	}

	/**
	 * @return the username
	 */
	public String getUsername() {
		return username;
	}

	/**
	 * @return the password
	 */
	public String getPassword() {
		return password;
	}

	/**
	 * @return the cleanSession
	 */
	public boolean isCleanSession() {
		return cleanSession;
	}

	/**
	 * @return the keepAlive
	 */
	public int getKeepAlive() {
		return keepAlive;
	}

	/**
	 * @return the protocolVersion
	 */
	public byte getProtocolVersion() {
		return protocolVersion;
	}

	/**
	 * @return the protocolName
	 */
	public String getProtocolName() {
		return protocolName;
	}

}
