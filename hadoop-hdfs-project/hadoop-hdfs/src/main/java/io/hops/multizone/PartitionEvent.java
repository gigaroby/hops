package io.hops.multizone;

public enum PartitionEvent {
  /**
   * This event is fired when the status of the partition is unknown (usually at the beginning).
   */
  UNKNOWN,
  /**
   * This event is fired by a {@link PartitionMonitor} when a network partition is detected.
   */
  DETECTED,
  /**
   * This event is fired by a {@link PartitionMonitor} when a network partition is resolved.
   */
  RESOLVED
}
