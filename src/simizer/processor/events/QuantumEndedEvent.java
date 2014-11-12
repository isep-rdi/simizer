package simizer.processor.events;

import simizer.event.Event;
import simizer.requests.Request;
import simizer.requests.RequestProcessor;

public class QuantumEndedEvent extends Event<Request, RequestProcessor> {

  public QuantumEndedEvent(long timestamp, Request r, RequestProcessor rp) {
    super(timestamp, r, rp);
  }

  @Override
  public void dispatch() {
    this.target.onQuantumEnded(timestamp, this.data);
  }

}
