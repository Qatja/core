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

import se.goransson.qatja.MQTTException;
import se.goransson.qatja.MQTTHelper;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

/**
 * MQTT {@link #PUBACK} message is the response to a {@link #PUBLISH} message
 * with QoS level {@link #AT_LEAST_ONCE}
 *
 * @author andreas
 *
 */
public class MQTTPuback extends MQTTMessage {

    public MQTTPuback(int packageIdentifier) {
        this.setType(PUBACK);
        this.setPackageIdentifier(packageIdentifier);
    }

    /**
     * Construct a {@link #PUBACK} message from a byte array
     *
     * @param buffer
     *            the byte array
     * @param bufferLength
     *            the length of the byte array
     */
    public MQTTPuback(byte[] buffer, int bufferLength) {

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

        // Get variable header (always length 2 in PUBACK)
        variableHeader = new byte[2];
        System.arraycopy(buffer, i, variableHeader, 0, variableHeader.length);

        // Get payload
        payload = new byte[remainingLength - variableHeader.length];
        if (payload.length > 0)
            System.arraycopy(buffer, i + variableHeader.length, payload, 0,
                    remainingLength - variableHeader.length);

        // Get package identifier
        packageIdentifier = (variableHeader[variableHeader.length - 2] >> 8 & 0xFF)
                | (variableHeader[variableHeader.length - 1] & 0xFF);
    }

    @Override
    protected byte[] generateFixedHeader() throws MQTTException, IOException {
        // FIXED HEADER
        ByteArrayOutputStream out = new ByteArrayOutputStream();

        // Type and PUBACK flags, reserved bits MUST be [0,0,0,0]
        byte fixed = (byte) ((type << 4) | (0x00 << 3) | (0x00 << 2)
                | (0x00 << 1) | (0x00) << 0);
        out.write(fixed);

        // Flags (none for PUBACK)

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

        out.write(MQTTHelper.MSB(packageIdentifier));
        out.write(MQTTHelper.LSB(packageIdentifier));

        return out.toByteArray();
    }

    @Override
    protected byte[] generatePayload() throws MQTTException, IOException {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        return out.toByteArray();
    }

}
