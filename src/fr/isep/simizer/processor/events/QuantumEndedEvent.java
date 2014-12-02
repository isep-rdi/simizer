package fr.isep.simizer.processor.events;

import fr.isep.simizer.event.Event;
import fr.isep.simizer.requests.Request;
import fr.isep.simizer.requests.RequestProcessor;

public class QuantumEndedEvent extends Event<Request, RequestProcessor> {

  public QuantumEndedEvent(long timestamp, Request r, RequestProcessor rp) {
    super(timestamp, r, rp);
  }

  @Override
  public void dispatch() {
    this.target.onQuantumEnded(timestamp, this.data);
  }

}
