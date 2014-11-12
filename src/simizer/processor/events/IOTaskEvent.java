package simizer.processor.events;

import simizer.event.Event;
import simizer.processor.TaskProcessor;
import simizer.processor.tasks.IOTask;

/**
 *  This event signals the end of an IO related task to its target
 * TaskProcessor.
 *
 * @see IOTask
 * @author isep
 */
public class IOTaskEvent extends Event<IOTask, TaskProcessor> {

  public IOTaskEvent(long timestamp, IOTask d, TaskProcessor pu) {
    super(timestamp, d, pu);
  }

  @Override
  public void dispatch() {
    target.onDataReady(timestamp, data);
  }

}
