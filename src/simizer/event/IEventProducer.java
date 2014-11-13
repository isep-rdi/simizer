package simizer.event;

/**
 * Allows events to be queued for later execution.
 * <p>
 * Classes that can schedule events should be subclasses of {@link
 * EventProducer}.  If that is not possible, it is also acceptable for classes
 * to implement this {@code IEventProducer} interface.  In that situation, the
 * class will likely provide a wrapper to an internal {@link EventProducer}
 * instance.
 * <p>
 * The {@link Simulation} will automatically call {@link
 * #setChannel(simizer.event.Channel)} for classes that implement this
 * interface.
 */
public interface IEventProducer {

  /**
   * Returns the {@code Channel} used by this producer.
   *
   * @return the {@code Channel} used by this producer
   */
  public Channel getOutputChannel();

  /**
   * Changes the {@code Channel} used by this producer.
   *
   * @param channel the new {@link Channel}
   */
  public void setChannel(final Channel channel);

  /**
   * Schedules an {@code Event} for deliver.
   * <p>
   * Passes the event to the system for delivery at the timestamp specified in
   * the event.
   *
   * @param event the {@code Event} to schedule
   */
  public void registerEvent(final Event event);

  /**
   * Cancels a previously-registered event.
   * <p>
   * This will cancel and remove an {@link Event} that was added using the
   * {@link #registerEvent(simizer.event.Event)} method.
   *
   * @param event the {@link Event} to remove
   * @return true if {@code event} was removed, false if it did not exist in the
   *             {@link Channel}
   */
  public boolean cancelEvent(final Event event);

}
