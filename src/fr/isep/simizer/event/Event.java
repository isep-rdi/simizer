package fr.isep.simizer.event;

/**
 * Represents an event in the simulation.
 * <p>
 * Events are used throughout the system to represent a variety of actions:
 * <ul><li>the delivery of network messages
 *     <li>the completion of disk read operations
 *     <li>the completion or interruption of processor tasks</ul>
 * <p>
 * The general idea behind an {@code Event} is that they have a target and data
 * payload.  When they are delivered, the data payload is delivered to the
 * target of the event.
 *
 * @param <D> the type of data contained in this event
 * @param <T> the type of the target where this event will be delivered
 */
public abstract class Event<D, T> implements Comparable<Event> {

  /** The timestamp when the event will be delivered. */
  protected final long timestamp;

  /** The data of the event. */
  protected final D data;

  /** The target of the event. */
  protected final T target;

  /**
   * Initializes a new {@code Event} to be delivered in the future.
   * <p>
   * This creates a new {@code Event} which will be delivered at a the specified
   * time in the future.  Note that it is necessary to add the event to a {@link
   * Channel} to ensure proper delivery.
   *
   * @param timestamp the time when the {@code Event} should be delivered
   * @param data the data to deliver with the event
   * @param target the target where the event should be delivered
   */
  public Event(long timestamp, D data, T target) {
    this.timestamp = timestamp;
    this.data = data;
    this.target = target;
  }

  /**
   * Returns the data of the {@code Event}.
   *
   * @return the data of the {@code Event}
   */
  public D getData() {
    return data;
  }

  /**
   * Returns the target of the {@code Event}.
   *
   * @return the target of the {@code Event}
   */
  public T getTarget() {
    return target;
  }

  /**
   * Returns the timestamp when the {@code Event} will be delivered.
   *
   * @return the timestamp when the {@code Event} will be delivered
   */
  public long getTimestamp() {
    return this.timestamp;
  }

  /**
   * Performs the actual delivery of the event.
   * <p>
   * This method is called by the runtime when it is time to deliver the event.
   * At this point in time, the current time of the simulation will be equal to
   * the time specified for the delivery of this event.
   * <p>
   * It is up to the subclass implementations to define the behavior of this
   * method, but it will usually consist of calling some method on the {@code
   * target}, passing it the provided {@code data}.
   */
  public abstract void dispatch();

  /**
   * Compares two {@code Event} objects by their timestamps.
   * <p>
   * This can be used to sort {@code Event} objects and execute them in order of
   * their timestamps.
   *
   * @param event the object to be compared
   * @return a negative integer, zero, or a positive integer as this object is
   *    less than, equal to, or greater than the specified object
   */
  @Override
  public int compareTo(final Event event) {
    return Long.compare(timestamp, event.timestamp);
  }

}
