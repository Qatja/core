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
 * @version 1.0.0
 * @since   2017-05-06
 */
public class MQTTConstants {

  /**
   * Client request to connect to a server
   */
  public static final byte CONNECT = 0x01;

  /** 
   * Connect Acknowledgement
   */
  public static final byte CONNACK = 0x02;

  /**
   * Publish message
   */
  public static final byte PUBLISH = 0x03;

  /**
   * Publish Acknowledgment
   */
  public static final byte PUBACK = 0x04;

  /**
   * Publish Received
   */
  public static final byte PUBREC = 0x05;

  /**
   * Publish Release
   */
  public static final byte PUBREL = 0x06;

  /**
   * Publish Complete
   */
  public static final byte PUBCOMP = 0x07;

  /**
   * Client Subscription request
   */
  public static final byte SUBSCRIBE = 0x08;

  /**
   * Subscription Acknowledgment
   */
  public static final byte SUBACK = 0x09;

  /**
   * Client Unsubscribe request
   */
  public static final byte UNSUBSCRIBE = 0x0a;

  /**
   * Unsubscribe Acknowledgment
   */
  public static final byte UNSUBACK = 0x0b;

  /**
   * PING Request
   */
  public static final byte PINGREQ = 0x0c;

  /**
   * PING Response
   */
  public static final byte PINGRESP = 0x0d;

  /**
   * Client is Disconnecting
   */
  public static final byte DISCONNECT = 0x0e;

  /**
   * Fire and Forget, QoS:0
   */
  public static final byte AT_MOST_ONCE = 0x00;

  /**
   * Acknowledged deliver, QoS:1
   */
  public static final byte AT_LEAST_ONCE = 0x01;

  /**
   * Assured Delivery, QoS:2
   */
  public static final byte EXACTLY_ONCE = 0x02;

  /**
   * Connection was accepted by server
   */
  public static final byte CONNECTION_ACCEPTED = 0x00;

  /**
   * The Server does not support the level of the MQTT protocol requested by the
   * Client
   */
  public static final byte CONNECTION_REFUSED_VERSION = 0x01;

  /**
   * The Client identifier is correct UTF-8 but not allowed by the Server
   */
  public static final byte CONNECTION_REFUSED_IDENTIFIER = 0x02;

  /**
   * The Network Connection has been made but the MQTT service is unavailable
   */
  public static final byte CONNECTION_REFUSED_SERVER = 0x03;

  /**
   * The data in the user name or password is malformed
   */
  public static final byte CONNECTION_REFUSED_USER = 0x04;

  /**
   * The Client is not authorized to connect
   */
  public static final byte CONNECTION_REFUSED_AUTH = 0x05;

  /*
   * Subscription return codes
   */
  public static final byte SUBSCRIBE_SUCCESS_AT_MOST_ONCE = AT_MOST_ONCE;
  public static final byte SUBSCRIBE_SUCCESS_AT_LEAST_ONCE = AT_LEAST_ONCE;
  public static final byte SUBSCRIBE_SUCCESS_EXACTLY_ONCE = EXACTLY_ONCE;
  public static final byte SUBSCRIBE_FAILURE = (byte) 0x80;

  /**
   * Minimum length of strings (byte[] length)
   */
  public static final int MIN_LENGTH = 0;

  /**
   * Maximum length of strings (byte[] length)
   */
  public static final int MAX_LENGTH = 65535;

}
