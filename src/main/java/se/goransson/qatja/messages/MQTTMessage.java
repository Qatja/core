package se.goransson.qatja.messages;

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

import se.goransson.qatja.*;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

/**
 * MQTT Message super class, should never be instantiated.
 *
 * @author andreas
 *
 */
public abstract class MQTTMessage implements MQTTConstants,
        MQTTConnectionConstants, MQTTVersion {

    protected byte flags;

    protected int packageIdentifier;

    protected byte[] fixedHeader;
    protected byte[] variableHeader;
    protected byte[] payload;

    protected byte type;
    protected int remainingLength;

    // protected byte[] buffer;

    protected abstract byte[] generateFixedHeader() throws MQTTException,
            IOException;

    protected abstract byte[] generateVariableHeader() throws MQTTException,
            IOException;

    protected abstract byte[] generatePayload() throws MQTTException,
            IOException;

    /**
     * @return the packageIdentifier
     */
    public int getPackageIdentifier() {
        return packageIdentifier;
    }

    /**
     * @param packageIdentifier
     *            the packageIdentifier to set
     */
    public void setPackageIdentifier(int packageIdentifier) {
        this.packageIdentifier = packageIdentifier;
    }

    /**
     * @return the fixedHeader
     */
    public byte[] getFixedHeader() {
        return fixedHeader;
    }

    /**
     * @param fixedHeader
     *            the fixedHeader to set
     */
    public void setFixedHeader(byte[] fixedHeader) {
        this.fixedHeader = fixedHeader;
    }

    /**
     * @return the variableHeader
     */
    public byte[] getVariableHeader() {
        return variableHeader;
    }

    /**
     * @param variableHeader
     *            the variableHeader to set
     */
    public void setVariableHeader(byte[] variableHeader) {
        this.variableHeader = variableHeader;
    }

    /**
     * @return the payload
     */
    public byte[] getPayload() {
        return payload;
    }

    /**
     * @param payload
     *            the payload to set
     */
    public void setPayload(byte[] payload) {
        this.payload = payload;
    }

    /**
     * @return the type
     */
    public byte getType() {
        return type;
    }

    /**
     * @param type
     *            the type to set
     */
    public void setType(byte type) {
        this.type = type;
    }

    /**
     * @return the remainingLength
     */
    public int getRemainingLength() {
        return remainingLength;
    }

    /**
     * @param remainingLength
     *            the remainingLength to set
     */
    public void setRemainingLength(int remainingLength) {
        this.remainingLength = remainingLength;
    }

    // /**
    // * @return the buffer
    // */
    // public byte[] getBuffer() {
    // return buffer;
    // }
    //
    // /**
    // * @param buffer the buffer to set
    // * @param bufferLength
    // */
    // public void setBuffer(byte[] buffer, int bufferLength) {
    // this.buffer = new byte[bufferLength];
    // System.arraycopy(buffer, 0, this.buffer, 0, bufferLength);
    // }

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
        sb.append("  remaining length: " + this.getRemainingLength()).append(
                "\n");
        sb.append("  package identifier: " + this.getPackageIdentifier());

        return sb.toString();
    }
}
