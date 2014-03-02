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
 * MQTT {@link #PUBLISH} message
 * 
 * @author andreas
 * 
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
	 *            the topic to publish to
	 * @param payload
	 *            the message payload
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
	 *            the buffer
	 * @param bufferLength
	 *            the buffer length
	 */
	public MQTTPublish(byte[] buffer, int bufferLength) {

		// setBuffer(bufferIn, bufferLength);

		int i = 0;

		// Type (just for clarity sake we'll set it...)
		this.retain = (buffer[i] >> 0 & 1) == 0x01 ? true : false;
		this.QoS = (byte) ((buffer[i] & ((0x00 << 3) | (0x01 << 2)
				| (0x01 << 1) | (0x00 << 0))) >> 1);
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
		variableHeader = new byte[len + 2 + 2];

		// We have to step back two bytes since
		System.arraycopy(buffer, i, variableHeader, 0, variableHeader.length);

		// Get topic
		topicName = new String(variableHeader, 2, len);

		// Get payload
		payload = new byte[remainingLength - variableHeader.length];

		if (payload.length > 0)
			System.arraycopy(buffer, i, payload, 0, payload.length);

		// Only get package identifier if the QoS is above AT_MOST_ONCE
		if (QoS > AT_MOST_ONCE) {
			packageIdentifier = (variableHeader[variableHeader.length - 1])
					| (variableHeader[variableHeader.length - 2]);
		}
	}

	@Override
	protected byte[] generateFixedHeader() throws MQTTException, IOException {
		// FIXED HEADER
		ByteArrayOutputStream out = new ByteArrayOutputStream();

		// Type and PUBLISH flags
		byte fixed = (byte) ((type << 4) | (dup ? 1 : 0) << 3 | (QoS << 1) | (retain ? 1
				: 0) << 0);
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
	 * @return the dup
	 */
	public boolean isDup() {
		return dup;
	}

	/**
	 * @return the qoS
	 */
	public byte getQoS() {
		return QoS;
	}

	/**
	 * @return the retain
	 */
	public boolean isRetain() {
		return retain;
	}

	/**
	 * @return the topicName
	 */
	public String getTopicName() {
		return topicName;
	}

}
