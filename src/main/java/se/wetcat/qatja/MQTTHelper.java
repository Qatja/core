package se.wetcat.qatja;

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

import org.mozilla.universalchardet.UniversalDetector;
import se.wetcat.qatja.messages.MQTTMessage;

import static se.wetcat.qatja.MQTTConstants.CONNECT;
import static se.wetcat.qatja.MQTTConstants.CONNACK;
import static se.wetcat.qatja.MQTTConstants.PUBLISH;
import static se.wetcat.qatja.MQTTConstants.PUBACK;
import static se.wetcat.qatja.MQTTConstants.PUBREC;
import static se.wetcat.qatja.MQTTConstants.PUBREL;
import static se.wetcat.qatja.MQTTConstants.PUBCOMP;
import static se.wetcat.qatja.MQTTConstants.SUBSCRIBE;
import static se.wetcat.qatja.MQTTConstants.SUBACK;
import static se.wetcat.qatja.MQTTConstants.UNSUBSCRIBE;
import static se.wetcat.qatja.MQTTConstants.UNSUBACK;
import static se.wetcat.qatja.MQTTConstants.PINGREQ;
import static se.wetcat.qatja.MQTTConstants.PINGRESP;
import static se.wetcat.qatja.MQTTConstants.DISCONNECT;

/**
 * Helper methods for MQTT
 *
 * @author  Andreas Goransson
 * @version 1.0.0
 * @since   2017-05-07
 */
public class MQTTHelper {

//  private static int lastPackageIdentifier = 1;

  private static final String UTF8 = "UTF-8";

  /**
   * Detects if string is encoded with UTF-8
   *
   * @param buffer String
   * @return true if UTF-8, false otherwise
   */
  public static boolean isUTF8(byte[] buffer) {
    UniversalDetector detector = new UniversalDetector(null);
    detector.handleData(buffer, 0, buffer.length);
    detector.dataEnd();

    String encoding = detector.getDetectedCharset();
    detector.reset();

    return (UTF8.equals(encoding));
  }

  public static boolean hasWildcards(String topic) {
    // TODO, is the $ acceptable?
    return /*topic.contains("/") ||*/ topic.contains("#") || topic.contains("+");
  }

//  public static int getNewPackageIdentifier() {
//    return (lastPackageIdentifier += 1);
//  }

  /**
   * Return the MSB of a string length
   *
   * @param buffer String
   * @return MSB
   */
  public static byte MSB(byte[] buffer) {
    return (byte) ((buffer.length) >> 8 & 0xFF);
  }

  /**
   * Return the LSB of the string length
   *
   * @param buffer String
   * @return LSB
   */
  public static byte LSB(byte[] buffer) {
    return (byte) (buffer.length & 0xFF);
  }

  /**
   * Return most significant byte of integer
   *
   * @param val the value
   * @return the MSB
   */
  public static byte MSB(int val) {
    return (byte) ((val & 0xffff) >> 8 & 0xFF);
  }

  /**
   * Return least significant byte of integer
   *
   * @param val the value
   * @return the LSB
   */
  public static byte LSB(int val) {
    return (byte) ((val & 0xffff) & 0xFF);
  }

  public static void decodeFlags(byte flags) {
    boolean username = ((flags >> 7) == 1 ? true : false);
    System.out.println("username flag: " + username);

    boolean password = ((flags >> 6) == 1 ? true : false);
    System.out.println("password flag: " + password);
  }

  /**
   * Decode message type
   *
   * @param buffer The message buffer to decode
   * @return The message type
   */
  public static byte decodeType(byte[] buffer) {
    return (byte) ((buffer[0] >> 4) & 0x0F);
  }

  /**
   * Get the human readable name of a message type
   *
   * @param message the message
   * @return human readable string of message type
   */
  public static String decodePackageName(MQTTMessage message) {
    return decodePackageName(message.getType());
  }

  /**
   * Get the human readable name of a message type
   *
   * @param messageType the message type
   * @return human readable string of message type
   */
  public static String decodePackageName(byte messageType) {
    switch (messageType) {
    case CONNECT:
      return "CONNECT";
    case CONNACK:
      return "CONNACK";
    case PUBLISH:
      return "PUBLISH";
    case PUBACK:
      return "PUBACK";
    case PUBREC:
      return "PUBREC";
    case PUBREL:
      return "PUBREL";
    case PUBCOMP:
      return "PUBCOMP";
    case SUBSCRIBE:
      return "SUBSCRIBE";
    case SUBACK:
      return "SUBACK";
    case UNSUBSCRIBE:
      return "UNSUBSCRIBE";
    case UNSUBACK:
      return "UNSUBACK";
    case PINGREQ:
      return "PINGREQ";
    case PINGRESP:
      return "PINGRESP";
    case DISCONNECT:
      return "DISCONNECT";
    default:
      return "Unknown message type";
    }
  }

}
