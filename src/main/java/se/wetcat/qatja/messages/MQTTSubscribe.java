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

/**
 * MQTT {@link #SUBSCRIBE} message
 *
 * @author andreas
 *
 */
public class MQTTSubscribe extends MQTTMessage {

    private String[] topicFilters;
    private byte[] QoSs;

    /**
     * Construct a {@link #SUBSCRIBE} message
     *
     * @param topicFilters
     *            the topics to subscribe to
     * @param QoSs
     *            the QoS for each topic
     */
    public MQTTSubscribe(String[] topicFilters, byte[] QoSs) {
        this.setType(SUBSCRIBE);
        this.topicFilters = topicFilters;
        this.QoSs = QoSs;

        setPackageIdentifier(MQTTHelper.getNewPackageIdentifier());
    }

    @Override
    protected byte[] generateFixedHeader() throws MQTTException, IOException {
        // FIXED HEADER
        ByteArrayOutputStream out = new ByteArrayOutputStream();

        // Type and reserved bits, MUST be [0 0 1 0]
        byte fixed = (byte) ((type << 4) | (0x00 << 3) | (0x00 << 2)
                | (0x01 << 1) | (0x00 << 0));
        out.write(fixed);

        // Flags (none for SUBSCRIBE)

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

        if (topicFilters.length <= 0 || QoSs.length <= 0)
            throw new MQTTException(
                    "The SUBSCRIBE message must contain at least one topic filter and QoS pair");

        if (topicFilters.length != QoSs.length)
            throw new MQTTException(
                    "The SUBSCRIBE message should have the same number of topic filters and QoS");

        for (int i = 0; i < topicFilters.length; i++) {
            if (MQTTHelper.isUTF8(topicFilters[i].getBytes("UTF-8")))
                throw new MQTTException("Invalid topic filter encoding: "
                        + topicFilters[i]);

            out.write(MQTTHelper.MSB(topicFilters[i].length()));
            out.write(MQTTHelper.LSB(topicFilters[i].length()));
            out.write(topicFilters[i].getBytes());
            out.write(QoSs[i]);
        }

        return out.toByteArray();
    }

    /**
     * @return the topicFilters
     */
    public String[] getTopicFilters() {
        return topicFilters;
    }

    /**
     * @return the qoSs
     */
    public byte[] getQoSs() {
        return QoSs;
    }

}
