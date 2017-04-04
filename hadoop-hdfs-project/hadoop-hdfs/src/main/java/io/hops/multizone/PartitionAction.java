package io.hops.multizone;

public interface PartitionAction {
  /**
   * This method is executed by the {@link PartitionMonitor} upon detection of a partition.
   */
  void onPartitionDetected();

  /**
   * This method is executed by the {@link PartitionMonitor} upon resolution of a partition.
   */
  void onPartitionResolved();
}
