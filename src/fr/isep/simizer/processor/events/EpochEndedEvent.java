package fr.isep.simizer.processor.events;

import fr.isep.simizer.event.Event;
import fr.isep.simizer.processor.ProcessingUnit;

public class EpochEndedEvent extends Event<Integer, ProcessingUnit> {

  public EpochEndedEvent(long timestamp, Integer counter, ProcessingUnit pu) {
    super(timestamp, counter, pu);
  }

  @Override
  public void dispatch() {
    target.onEpochEnded(timestamp, data);
  }

}
