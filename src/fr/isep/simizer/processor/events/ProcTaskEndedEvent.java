package fr.isep.simizer.processor.events;

import fr.isep.simizer.event.Event;
import fr.isep.simizer.processor.ProcessingUnit;
import fr.isep.simizer.processor.tasks.ProcTask;

public class ProcTaskEndedEvent extends Event<ProcTask, ProcessingUnit> {

  public ProcTaskEndedEvent(long timestamp, ProcTask pt, ProcessingUnit pu) {
    super(timestamp, pt, pu);
  }

  @Override
  public void dispatch() {
    this.target.onProcTaskEnded(timestamp, this.data);
  }

}
