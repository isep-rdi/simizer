package simizer.event;

/**
 * Provides a simple class implementation of {@code IEventProducer}.
 * <p>
 * This class provides a class implementation of the {@link IEventProducer}
 * interface.  This allows other classes to easily add the functionality of
 * {@link IEventProducer} by deriving from this class.
 */
public class EventProducer implements IEventProducer {

  /** The channel used by this {@code EventProducer}. */
  protected Channel channel;

  @Override
  public Channel getOutputChannel() {
    return this.channel;
  }

  @Override
  public void registerEvent(Event event) {
    channel.registerEvent(event);
  }

  @Override
  public void setChannel(Channel channel) {
    this.channel = channel;
  }

  @Override
  public boolean cancelEvent(Event event) {
    return channel.cancelEvent(event);
  }

}
