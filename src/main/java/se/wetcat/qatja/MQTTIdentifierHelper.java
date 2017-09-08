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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

import se.wetcat.qatja.messages.MQTTMessage;

/**
 * A helper class for handling package identifiers.
 *
 * @author andreasgoransson0@gmail.com
 */
public class MQTTIdentifierHelper {

  private ConcurrentHashMap<Integer, MQTTMessage> sentPackages = new ConcurrentHashMap<>();
  private ConcurrentHashMap<Integer, MQTTMessage> receivedPackages = new ConcurrentHashMap<>();

  private List<Integer> availablePackageIdentifiers = new ArrayList<>();

  private final Object lock = new Object();

  /**
   * Add MQTTMessage to the list of sent packages, used for QoS
   * {@link se.wetcat.qatja.MQTTConstants#AT_MOST_ONCE} and
   * {@link se.wetcat.qatja.MQTTConstants#EXACTLY_ONCE}
   *
   * @param msg the MQTTMessage
   */
  public void addSentPackage(MQTTMessage msg) {
    synchronized (lock) {
      sentPackages.put(msg.getPackageIdentifier(), msg);
    }
  }

  /**
   * Remove MQTTMessage from the list of sent packages, used for QoS
   * {@link se.wetcat.qatja.MQTTConstants#AT_MOST_ONCE} and
   * {@link se.wetcat.qatja.MQTTConstants#EXACTLY_ONCE}
   *
   * @param msg the MQTTMessage
   */
  public synchronized void removeSentPackage(MQTTMessage msg) {
    synchronized (lock) {
      sentPackages.remove(msg.getPackageIdentifier());
      availablePackageIdentifiers.add(msg.getPackageIdentifier());
    }
  }

  public ConcurrentHashMap<Integer, MQTTMessage> getSentPackages() {
    synchronized (lock) {
      return sentPackages;
    }
  }

  public ConcurrentHashMap<Integer, MQTTMessage> getReceivedPackages() {
    synchronized (lock) {
      return receivedPackages;
    }
  }

  /**
   * Add MQTTMessage to the list of received packages, used for QoS
   * {@link MQTTConstants#AT_MOST_ONCE} and {@link MQTTConstants#EXACTLY_ONCE}
   *
   * @param msg the MQTTMessage
   */
  public void addReceivedPackage(MQTTMessage msg) {
    synchronized (lock) {
      receivedPackages.put(msg.getPackageIdentifier(), msg);
    }
  }

  /**
   * Remove MQTTMessage to the list of received packages, used for QoS
   * {@link MQTTConstants#AT_MOST_ONCE} and {@link MQTTConstants#EXACTLY_ONCE}
   *
   * @param msg the MQTTMessage
   */
  public void removeReceivedPackage(MQTTMessage msg) {
    synchronized (lock) {
      receivedPackages.remove(msg.getPackageIdentifier());
    }
  }

  /**
   * @return The next available identifier
   */
  public synchronized int getIdentifier() {
    synchronized (lock) {
      int identifier = 1;

      if (availablePackageIdentifiers.size() > 0) {
        // Reuse an identifier
        identifier = availablePackageIdentifiers.remove(0);
      } else if (sentPackages.size() > 0) {
        // Generate the next, new, identifier by looping through the map and find the next biggest id
//        int lastPackage = sentPackages.size() - 1;
//        identifier = sentPackages.get(lastPackage).getPackageIdentifier() + 1;

//        boolean idFound = true;
//        do {
//          for (int id : Collections.list(sentPackages.keys())) {
//            if (identifier == id) {
//              idFound = true;
//            }
//          }
//        } while (idFound);

        List<Integer> keys = Collections.list(sentPackages.keys());
        for (int i = 0; i < keys.size(); i++) {
          if (identifier == keys.get(i)) {
            identifier++;
            i = 0;
          }
        }
      } else {
        // Generate first identifier
        identifier = 1;
      }


      return identifier;
    }
  }
}
