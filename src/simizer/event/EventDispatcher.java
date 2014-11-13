package simizer.event;

import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Delivers events from a {@code Channel}.
 * <p>
 * Old Link: http://www.ibm.com/developerworks/library/j-jtp0730/index.html
 *
 * @author Sylvain Lefebvre
 */
public class EventDispatcher implements Runnable {

  /** Whether or not the run loop has been canceled. */
  private boolean canceled = false;

  /**
   * The current time of the simulation.
   * <p>
   * This value is the same as the timestamp of the most recently delivered
   * event.  This value in no way reflects any passage of real time.
   */
  private long clock = 0;

  /** The number of events delivered by this dispatcher. */
  private int eventCount = 0;

  /** The {@code Channel} for which events will be delivered. */
  private final Channel channel;

  /** The maximum amount of time to wait for events before finishing. */
  private static final long WAIT_TIME = 100;
  

  /**
   * Initializes a new instance of the class.
   *
   * @param channel the location to reference for {@code Event}s to deliver
   */
  public EventDispatcher(final Channel channel) {
    this.channel = channel;
  }

  /**
   * Returns the channel associated with the dispatcher.
   *
   * @return the channel associated with the dispatcher
   */
  public Channel getChannel() {
    return this.channel;
  }

  /**
   * Returns the current timestamp of the run loop (ms).
   * <p>
   * This value does not represent real time.  Instead, it represents the times
   * specified by the events in the {@code Channel}.
   *
   * @return the current timestamp in milliseconds
   */
  public long getClock() {
    return clock;
  }

  /**
   * Returns the number of events delivered by the dispatcher.
   *
   * @return the number of events delivered by the dispatcher
   */
  public int getEventCount() {
    return eventCount;
  }

  /**
   * Processes events from the {@code Channel}.
   * <p>
   * This method will not return until
   * <ul><li>all events in the {@link Channel} have been processed (including
   *             those added after execution has begun)
   *     <li>there are no events to process for at least {@value #WAIT_TIME} ms
   *     <li>the {@link #stop()} method is called</ul>
   */
  @Override
  public void run() {
    long start = System.currentTimeMillis();

    while (!canceled) {
      Event event = null;

      try {
        event = channel.poll(WAIT_TIME, TimeUnit.MILLISECONDS);
      } catch (InterruptedException ex) {
        Logger.getLogger(EventDispatcher.class.getName()).log(Level.SEVERE, null, ex);
      }

      // when event is null, it means that we don't have any events to process
      // if that is the case, the channel must be empty, so we'll stop anyways
      if (event == null) {
        break;
      }

      if (event.timestamp < clock) {
        // If the event is before the current clock, it means that the user is
        // trying to send an event in the past.  This (most likely) means that
        // there is a bug or issue with the simulation, and also that the
        // results are not valid.
        throw new RuntimeException("Cannot dispatch an event in the past.");
      }
      
      clock = event.timestamp;
      event.dispatch();
      eventCount++;
    }

    long duration = System.currentTimeMillis() - start;
    System.out.println((canceled ? "Canceled." : "Finished.")
        + "  " + eventCount + " event(s) completed in " + duration + "ms.");
  }

  /**
   * Stops this {@code EventDispatcher}.
   * <p>
   * Future events will not be delivered, and the "run loop" will exit.
   */
  public synchronized void stop() {
    this.canceled = true;
  }

}
