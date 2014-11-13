package simizer.event;

public interface IEventProducer {

  public Channel getOutputChannel();

  public void setChannel(final Channel c);

  public void registerEvent(Event evt);

}
