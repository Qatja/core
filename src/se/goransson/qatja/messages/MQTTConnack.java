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

import java.io.IOException;

import se.goransson.qatja.MQTTException;

/**
 * MQTT {@link #CONNACK} message
 * 
 * @author andreas
 * 
 */
public class MQTTConnack extends MQTTMessage {

	@SuppressWarnings("unused")
	private byte RESERVED;
	private byte returnCode;

	public MQTTConnack(byte[] buffer, int bufferLength) {

		// setBuffer(buffer, bufferLength);

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

		// Get variable header (always length 2 in CONNACK)
		variableHeader = new byte[2];
		System.arraycopy(buffer, i, variableHeader, 0, variableHeader.length);

		// Get payload
		payload = new byte[remainingLength - variableHeader.length];
		if (payload.length > 0)
			System.arraycopy(buffer, i + variableHeader.length, payload, 0,
					remainingLength - variableHeader.length);

		RESERVED = variableHeader[0];
		returnCode = variableHeader[1];
	}

	/**
	 * @return the returnCode
	 */
	public byte getReturnCode() {
		return returnCode;
	}

	@Override
	protected byte[] generateFixedHeader() throws MQTTException, IOException {
		// Client doesn't generate CONNACK
		return null;
	}

	@Override
	protected byte[] generateVariableHeader() throws MQTTException, IOException {
		// Client doesn't generate CONNACK
		return null;
	}

	@Override
	protected byte[] generatePayload() throws MQTTException, IOException {
		// Client doesn't generate CONNACK
		return null;
	}

}
