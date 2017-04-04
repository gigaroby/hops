package io.hops.multizone;


import io.hops.leaderElection.NDBLeaderElection;
import io.hops.leader_election.node.ActiveNode;
import io.hops.leader_election.node.SortedActiveNodeList;
import io.hops.metadata.ndb.multizone.SecondaryConnector;

/**
 * This class detects partitions for namenode(s) in the secondary cluster.
 */
public class SecondaryPartitionMonitor extends PartitionMonitor {
  private final SecondaryConnector connector;
  private final NDBLeaderElection leaderElection;

  public SecondaryPartitionMonitor(final PartitionAction action, final NDBLeaderElection le, final SecondaryConnector connector) {
    super(action);
    this.connector = connector;
    this.leaderElection = le;
  }

  /**
   * This methods performs partition detection for the secondary cluster.
   * A partition is detected in the secondary cluster if all the live namenodes lost the connection to the primary cluster.
   * Additionally, this class updates the state of the node's connection in the leader election procedure.
   *
   * @return
   */
  @Override
  protected PartitionEvent tick() {
    boolean connected = connector.isConnectedToPrimary();

    // update the state of the connection in the leader election procedure
    leaderElection.setConnectedToPrimary(connected);
    // if I am connected to primary there is at least one node connected (therefore no partition).
    if (connected) {
      return PartitionEvent.RESOLVED;
    }

    // if at least one of the other nodes is connected, the partition is resolved.
    SortedActiveNodeList namenodes = leaderElection.getActiveNamenodes();
    // this can happen if we run before the first leader election round
    if(namenodes == null) {
      return PartitionEvent.UNKNOWN;
    }
    for (ActiveNode n : namenodes.getActiveNodes()) {
      if (n.isConnectedToPrimary()) {
        return PartitionEvent.RESOLVED;
      }
    }

    // if all the active namenodes aren't connected to the database, we detect a partition.
    return PartitionEvent.DETECTED;
  }
}
