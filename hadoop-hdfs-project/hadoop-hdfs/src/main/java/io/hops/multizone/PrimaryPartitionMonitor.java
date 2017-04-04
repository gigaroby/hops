package io.hops.multizone;

import io.hops.leader_election.node.ActiveNode;
import io.hops.leader_election.node.SortedActiveNodeList;
import io.hops.services.LeaderElectionService;

/**
 * This class detects network partitions for namenode(s) in the primary cluster.
 */
public class PrimaryPartitionMonitor extends PartitionMonitor {
  private final LeaderElectionService leaderElection;

  public PrimaryPartitionMonitor(PartitionAction action, LeaderElectionService leaderElection) {
    super(action);
    this.leaderElection = leaderElection;
  }

  /**
   * A partition is detected if and only if all the nodes in the secondary cluster are no longer live.
   */
  @Override
  protected PartitionEvent tick() {
    SortedActiveNodeList list = leaderElection.getActiveNamenodes();
    if(list == null) {
      return PartitionEvent.UNKNOWN;
    }

    for(ActiveNode n: list.getActiveNodes()) {
      if(n.getZone() == Zone.SECONDARY) {
        return PartitionEvent.RESOLVED;
      }
    }
    return PartitionEvent.DETECTED;
  }
}
