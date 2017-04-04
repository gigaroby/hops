package io.hops.multizone;

import io.hops.services.LeaderElectionService;
import io.hops.services.Service;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public abstract class PartitionMonitor implements Service, Runnable {
  public static final Log LOG = LogFactory.getLog(PartitionMonitor.class);


  private enum State {
    NORMAL,
    PARTITIONED
  }

  private final PartitionAction action;
  private State state;

  // check connectivity every 5 seconds
  private final long sleepInterval = 5000;
  // Thread running the service
  private Thread t;

  /**
   * Builds a partition monitor which reacts to partition events by executing the appropriate actions.
   * The definition of when an event should be fired is left to concrete implementations.
   * @param action
   */
  public PartitionMonitor(final PartitionAction action) {
    this.action = action;
    this.state = State.NORMAL;
  }

  @Override
  public void start() {
    if(t != null) {
      return;
    }
    t = new Thread(this);
    t.setDaemon(true);
    t.start();
  }

  @Override
  public void stop() {
    t.interrupt();
    t = null;
  }

  @Override
  public void waitStarted() throws InterruptedException {
  }

  @Override
  public boolean isRunning() {
    return t != null;
  }

  @Override
  public void run() {
    while(!Thread.currentThread().isInterrupted())  {
      try {
        LOG.trace("executing tick()");
        PartitionEvent next = tick();
        LOG.trace("tick() returned " + next.toString());
        fire(next);
        LOG.trace("after firing " + next.toString() + " state=" + state.toString());
        Thread.sleep(sleepInterval);
      } catch (InterruptedException exc) {
        Thread.currentThread().interrupt();
      } catch(Throwable t) {
        LOG.error("tick raised unexpected exception", t);
      }
    }
  }

  /**
   * Tick computes an event to fire.
   * @return the event to fire.
   */
  protected abstract PartitionEvent tick();

  /**
   * This methods executes the appropriate {@link PartitionAction} when required.
   * @param evt the detected partition event
   */
  protected void fire(PartitionEvent evt) {
    // ignore unknown events
    if(evt == PartitionEvent.UNKNOWN) {
      return;
    }

    if (evt == PartitionEvent.DETECTED && state == State.NORMAL) {
      // we need to take action
      this.action.onPartitionDetected();
      LOG.debug(String.format("detected partition: event=%s, state=%s", evt, state));
      this.state = State.PARTITIONED;
    } else if (evt == PartitionEvent.RESOLVED && state == State.PARTITIONED) {
      // we need to resolve the partition
      this.action.onPartitionResolved();
      LOG.debug(String.format("resolved partition: event=%s, state=%s", evt, state));
      this.state = State.NORMAL;
    } else {
      // discard the event, we are already in the correct state
      LOG.debug(String.format("event discarded because event=%s, state=%s", evt, state));
    }
  }
}
