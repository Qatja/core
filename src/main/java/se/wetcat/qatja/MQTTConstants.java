package se.wetcat.qatja;

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

/**
 * Defines all MQTT constants; including message types, quality of service, etc.
 *
 * @author  Andreas Goransson
 * @version 1.0
 * @since   2017-05-06
 */
public interface MQTTConstants {

  /**
   * Client request to connect to a server
   */
  static final byte CONNECT = 0x01;

  /** 
   * Connect Acknowledgement
   */
  static final byte CONNACK = 0x02;

  /**
   * Publish message
   */
  static final byte PUBLISH = 0x03;

  /**
   * Publish Acknowledgment
   */
  static final byte PUBACK = 0x04;

  /**
   * Publish Received
   */
  static final byte PUBREC = 0x05;

  /**
   * Publish Release
   */
  static final byte PUBREL = 0x06;

  /**
   * Publish Complete
   */
  static final byte PUBCOMP = 0x07;

  /**
   * Client Subscription request
   */
  static final byte SUBSCRIBE = 0x08;

  /**
   * Subscription Acknowledgment
   */
  static final byte SUBACK = 0x09;

  /**
   * Client Unsubscribe request
   */
  static final byte UNSUBSCRIBE = 0x0a;

  /**
   * Unsubscribe Acknowledgment
   */
  static final byte UNSUBACK = 0x0b;

  /**
   * PING Request
   */
  static final byte PINGREQ = 0x0c;

  /**
   * PING Response
   */
  static final byte PINGRESP = 0x0d;

  /**
   * Client is Disconnecting
   */
  static final byte DISCONNECT = 0x0e;

  /**
   * Fire and Forget, QoS:0
   */
  static final byte AT_MOST_ONCE = 0x00;

  /**
   * Acknowledged deliver, QoS:1
   */
  static final byte AT_LEAST_ONCE = 0x01;

  /**
   * Assured Delivery, QoS:2
   */
  static final byte EXACTLY_ONCE = 0x02;

  /**
   * Connection was accepted by server
   */
  static final byte CONNECTION_ACCEPTED = 0x00;

  /**
   * The Server does not support the level of the MQTT protocol requested by the
   * Client
   */
  static final byte CONNECTION_REFUSED_VERSION = 0x01;

  /**
   * The Client identifier is correct UTF-8 but not allowed by the Server
   */
  static final byte CONNECTION_REFUSED_IDENTIFIER = 0x02;

  /**
   * The Network Connection has been made but the MQTT service is unavailable
   */
  static final byte CONNECTION_REFUSED_SERVER = 0x03;

  /**
   * The data in the user name or password is malformed
   */
  static final byte CONNECTION_REFUSED_USER = 0x04;

  /**
   * The Client is not authorized to connect
   */
  static final byte CONNECTION_REFUSED_AUTH = 0x05;

  /*
   * Subscription return codes
   */
  static final byte SUBSCRIBE_SUCCESS_AT_MOST_ONCE = AT_MOST_ONCE;
  static final byte SUBSCRIBE_SUCCESS_AT_LEAST_ONCE = AT_LEAST_ONCE;
  static final byte SUBSCRIBE_SUCCESS_EXACTLY_ONCE = EXACTLY_ONCE;
  static final byte SUBSCRIBE_FAILURE = (byte) 0x80;

  /**
   * Minimum length of strings (byte[] length)
   */
  static final int MIN_LENGTH = 0;

  /**
   * Maximum length of strings (byte[] length)
   */
  static final int MAX_LENGTH = 65535;

}
