package simizer.processor.events;

import simizer.event.Event;
import simizer.processor.ProcessingUnit;

public class EpochEndedEvent extends Event<Integer, ProcessingUnit> {

  public EpochEndedEvent(long timestamp, Integer counter, ProcessingUnit pu) {
    super(timestamp, counter, pu);
  }

  @Override
  public void dispatch() {
    target.onEpochEnded(timestamp, data);
  }

}
