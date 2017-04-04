/*
 * Copyright (C) 2015 hops.io.
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
package io.hops.leader_election.node;

import io.hops.multizone.Zone;

import java.net.InetSocketAddress;

public interface ActiveNode extends Comparable<ActiveNode> {
  String getHostname();

  long getId();

  String getIpAddress();

  int getPort();

  InetSocketAddress getInetSocketAddress();
  
  String getHttpAddress();

  /**
   * @return the zone the namenode belongs to
   */
  Zone getZone();

  /**
   * This method returns true iff the namenode is currently connected to the primary server.
   * This only makes sense for namenodes participating in the secondary-cluster-only leader election.
   * The result of this call in other contexts is undefined.
   */
  boolean isConnectedToPrimary();
}
