package simizer.processor.events;

import simizer.event.Event;
import simizer.processor.ProcessingUnit;
import simizer.processor.tasks.ProcTask;

public class ProcTaskEndedEvent extends Event<ProcTask, ProcessingUnit> {

  public ProcTaskEndedEvent(long timestamp, ProcTask pt, ProcessingUnit pu) {
    super(timestamp, pt, pu);
  }

  @Override
  public void dispatch() {
    this.target.onProcTaskEnded(timestamp, this.data);
  }

}
