package io.hops.multizone;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.Timeout;

import static org.junit.Assert.assertEquals;

public class PartitionMonitorTest {
  // set a global timeout of a second
  @Rule
  public Timeout global = new Timeout(1000);

  private static class CountPartitionAction implements PartitionAction {
    int resolved = 0, detected = 0;

    @Override
    public void onPartitionDetected() {
      detected++;
    }

    @Override
    public void onPartitionResolved() {
      resolved++;
    }
  }

  /**
   * Test that the correct actions are invoked, and that useless events are ignored.
   * @throws InterruptedException
   */
  @Test
  public void testStateMachine() throws InterruptedException {
    CountPartitionAction cpa = new CountPartitionAction();
    PartitionMonitor m = new PartitionMonitor(cpa) {
      @Override
      protected PartitionEvent tick() {
        return null;
      }
    };

    // no events, counts should be zero
    assertEquals(0, cpa.detected);
    assertEquals(0, cpa.resolved);
    // fire an event
    m.fire(PartitionEvent.DETECTED);
    assertEquals(1, cpa.detected);
    // further detected events should be ignored
    m.fire(PartitionEvent.DETECTED);
    m.fire(PartitionEvent.DETECTED);
    assertEquals(1, cpa.detected);
    // send a resolved event
    m.fire(PartitionEvent.RESOLVED);
    assertEquals(1, cpa.resolved);
  }
}