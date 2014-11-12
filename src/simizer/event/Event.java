package simizer.event;

public abstract class Event<D, T> implements Comparable<Event> {

  protected final long timestamp;

  protected final D data;
  protected final T target;

  public Event(long timestamp, D data, T target) {
    this.timestamp = timestamp;
    this.data = data;
    this.target = target;
  }

  public D getData() {
    return data;
  }

  public T getTarget() {
    return target;
  }

  public abstract void dispatch();

  @Override
  public int compareTo(final Event e) {
    return Long.compare(timestamp, e.timestamp);
  }

  public long getTimeStamp() {
    return this.timestamp;
  }

}
