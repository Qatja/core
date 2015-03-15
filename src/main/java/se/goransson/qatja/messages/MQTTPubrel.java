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
 * MQTT {@link #PUBREL} message is the reponse to a {@link #PUBREC} message. It
 * is the third message of the {@link #EXACTLY_ONCE} flow
 *
 * @author andreas
 *
 */
public class MQTTPubrel extends MQTTMessage {

    /**
     * Construct a {@link #PUBREL} message
     *
     * @param packageIdentifier
     *            the package identifier
     */
    public MQTTPubrel(int packageIdentifier) {
        setType(PUBREL);
        setPackageIdentifier(packageIdentifier);
    }

    public MQTTPubrel(byte[] buffer, int bufferLength) {

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

        // Get variable header always 2 (just the pkg id) for PUBREL
        variableHeader = new byte[2];

        // We have to step back two bytes since
        System.arraycopy(buffer, i, variableHeader, 0, variableHeader.length);

        // Get payload
        payload = new byte[remainingLength - variableHeader.length];

        if (payload.length > 0)
            System.arraycopy(buffer, i, payload, 0, payload.length);

        // Only get package identifier if the QoS is above AT_MOST_ONCE
        packageIdentifier = (variableHeader[variableHeader.length - 1])
                | (variableHeader[variableHeader.length - 2]);
    }

    @Override
    protected byte[] generateFixedHeader() throws MQTTException, IOException {
        // FIXED HEADER
        ByteArrayOutputStream out = new ByteArrayOutputStream();

        // Type and PUBLISH flags (The last four bits MUST be [0 0 1 0])
        byte fixed = (byte) ((type << 4) | (0x00 << 3) | (0x00 << 2)
                | (0x01 << 1) | (0x00 << 0));
        out.write(fixed);

        // Flags (none for PUBREL)

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

        out.write(MQTTHelper.MSB(getPackageIdentifier()));
        out.write(MQTTHelper.LSB(getPackageIdentifier()));

        return out.toByteArray();
    }

    @Override
    protected byte[] generatePayload() throws MQTTException, IOException {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        return out.toByteArray();
    }

}
