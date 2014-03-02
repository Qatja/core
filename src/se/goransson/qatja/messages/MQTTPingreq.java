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

/**
 * MQTT {@link #PINGREQ} message
 * 
 * @author andreas
 * 
 */
public class MQTTPingreq extends MQTTMessage {

	/**
	 * Construct a {@link #PINGREQ} message
	 */
	public MQTTPingreq() {
		this.setType(PINGREQ);
	}

	@Override
	protected byte[] generateFixedHeader() throws MQTTException, IOException {
		// FIXED HEADER
		ByteArrayOutputStream out = new ByteArrayOutputStream();

		// Type
		byte fixed = (byte) (type << 4);
		out.write(fixed);

		// Flags (none for PINGREQ)

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
		return out.toByteArray();
	}

	@Override
	protected byte[] generatePayload() throws MQTTException, IOException {
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		return out.toByteArray();
	}

}
