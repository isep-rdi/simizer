package simizer.event;

import java.util.concurrent.PriorityBlockingQueue;

/**
 * Represents a collection of events that need to be delivered.
 * <p>
 * The {@code Channel} is part of run loop-like component of the simulation, in
 * that it stores events that need to be delivered.  Internally, it uses a
 * priority queue so that events are delivered in order of their timestamps.
 * <p>
 * For a more detailed explanation on the events infrastructure in Simizer, use
 * {@see EventDispatcher}.
 */
public class Channel extends PriorityBlockingQueue<Event> {

  /**
   * Schedules a new event for delivery.
   * <p>
   * Passes the event to the system for delivery at the timestamp specified in
   * the event.  Note that a {@code Channel} is only responsible for storing
   * events.  To deliver the events to the system, see {@see EventDispatcher}.
   *
   * @param event the {@code Event} to be delivered
   */
  public void registerEvent(final Event event) {
    this.offer(event);
  }

}
